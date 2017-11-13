package se.crisp.signup4.controllers

import javax.inject.{Inject, Singleton}

import jp.t2v.lab.play2.auth.{AuthElement, OptionalAuthElement}
import se.crisp.signup4.models.security.Administrator
import se.crisp.signup4.models._
import se.crisp.signup4.models.Status._
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{I18nSupport, Lang, Messages, MessagesApi}
import play.api.libs.concurrent.Akka
import play.api.mvc._
import se.crisp.signup4.models.dao.{EventDAO, LogEntryDAO, ParticipationDAO, UserDAO}
import se.crisp.signup4.services.{ImageUrl, SlackReminder}
import se.crisp.signup4.util._

@Singleton
class Participations @Inject()(val messagesApi: MessagesApi,
                               implicit val authHelper: AuthHelper,
                               implicit val localeHelper: LocaleHelper,
                               implicit val themeHelper: ThemeHelper,
                               implicit val formHelper: FormHelper,
                               implicit val htmlHelper: HtmlHelper,
                               val slackReminder: SlackReminder,
                               implicit val eventDAO: EventDAO,
                               val userDAO: UserDAO,
                               val participationDAO: ParticipationDAO,
                               val logEntryDAO: LogEntryDAO,
                               implicit val imageUrl: ImageUrl) extends Controller with OptionalAuthElement with AuthConfigImpl with I18nSupport{

  def editForm(eventId: Long, userId: Long): Action[AnyContent] = StackAction { implicit request =>
    val event = eventDAO.find(eventId)
    if (!event.isCancelled) {
      val userToAttend = userDAO.find(userId)
      val participation = participationDAO.findByEventAndUser(eventId, userId).getOrElse(new Participation(status = Unregistered, user = userToAttend, event = event))
      Ok(se.crisp.signup4.views.html.participations.edit(participationForm.fill(participation), userToAttend, event))
    } else {
      Redirect(routes.Events.show(eventId)).flashing("error" -> Messages("error.signup.eventcancelled"))
    }
  }

  private def asLogMessage(participation: Participation)(implicit lang: Lang):String = {
    val message = new StringBuffer()
    message.append(Messages("participation.updated", participation.user.name, StatusHelper.asMessage(participation.status)))

    if(participation.numberOfParticipants > 1) {
      message.append(Messages("participation.people", participation.numberOfParticipants))
    }

    if(!participation.comment.isEmpty) {
      message.append(s" (${participation.comment})")
    }

    message.toString
  }

  def createOrUpdate: Action[AnyContent] = StackAction { implicit request =>
    participationForm.bindFromRequest.fold(
      formWithErrors => {
        val event = eventDAO.find(formWithErrors("eventId").value.get.toLong)
        val userToAttend = userDAO.find(formWithErrors("userId").value.get.toLong)
        BadRequest(se.crisp.signup4.views.html.participations.edit(formWithErrors, userToAttend, event))
      },
      participation => {
        val existingParticipation = participationDAO.findByEventAndUser(participation.event.id.get, participation.user.id.get)
        if (existingParticipation.isEmpty) {
          participationDAO.create(participation)
        } else {
          // TODO: only do this if there actually is a change, to avoid unneccessary log messages
          participationDAO.update(existingParticipation.get.id.get, participation)
          val updatedParticipation = participationDAO.find(existingParticipation.get.id.get)
          logEntryDAO.create(updatedParticipation.event, asLogMessage(updatedParticipation))

          import play.api.Play.current
          import play.api.libs.concurrent.Execution.Implicits._
          import scala.concurrent.duration._
          Akka.system.scheduler.scheduleOnce(1.second) {
            slackReminder.sendUpdatedParticipationMessage(updatedParticipation)
          }

        }
        Redirect(routes.Events.show(participation.event.id.get))
      }
    )
  }


  val participationForm: Form[Participation] =
    Form(
      mapping(
        "id" -> ignored(None: Option[Long]),
        "status" -> nonEmptyText(maxLength = 20),
        "number_of_participants" -> number(min = 1),
        "comment" -> text(maxLength = 127),
        "userId" -> longNumber,
        "eventId" -> longNumber
      )(toParticipation)(fromParticipation)
        .verifying("error.signup.eventcancelled", participation => !participation.event.isCancelled)
    )
  def fromParticipation(participation: Participation): Option[(Option[Long], String, Int, String, Long, Long)] = {
    Option((participation.id, participation.status.toString, participation.numberOfParticipants, participation.comment, participation.user.id.get, participation.event.id.get))
  }

  def toParticipation(
                       id: Option[Long],
                       status: String,
                       numberOfParticipants: Int,
                       comment: String,
                       userId: Long,
                       eventId: Long): Participation = {

    new Participation(
      id = id,
      status = se.crisp.signup4.models.Status.withName(status),
      numberOfParticipants = numberOfParticipants,
      comment = comment,
      user = userDAO.find(userId),
      event = eventDAO.find(eventId)
    )
  }
}


class ParticipationsSecured @Inject()(val participations: Participations,
                                      val messagesApi: MessagesApi,
                                      implicit val authHelper: AuthHelper,
                                      implicit val localeHelper: LocaleHelper,
                                      implicit val themeHelper: ThemeHelper,
                                      implicit val formHelper: FormHelper,
                                      val eventDAO: EventDAO,
                                      val userDAO: UserDAO,
                                      val participationDAO: ParticipationDAO,
                                      implicit val imageUrl: ImageUrl) extends Controller with AuthElement with AuthConfigImpl with I18nSupport {
  def createGuestForm(eventId: Long): Action[AnyContent] = StackAction(AuthorityKey -> authHelper.hasPermission(Administrator)) { implicit request =>
    implicit val loggedInUser: Option[User] = Option(loggedIn)
    val event = eventDAO.find(eventId)
    Ok(se.crisp.signup4.views.html.participations.addGuest(participations.participationForm, event, userDAO.findNonGuests(event.id.get)))
  }

  def createGuest: Action[AnyContent] = StackAction(AuthorityKey -> authHelper.hasPermission(Administrator)) { implicit request =>
    implicit val loggedInUser: Option[User] = Option(loggedIn)
    participations.participationForm.bindFromRequest.fold(
      formWithErrors => {
        val event = eventDAO.find(formWithErrors("eventId").value.get.toLong)
        BadRequest(se.crisp.signup4.views.html.participations.addGuest(formWithErrors, event, userDAO.findNonGuests(event.id.get)))
      },
      participation => {
        participationDAO.createGuest(participation.event.id.get, participation.user.id.get)
        Redirect(routes.Events.show(participation.event.id.get))
      }
    )
  }

  def delete(id: Long): Action[AnyContent] = StackAction(AuthorityKey -> authHelper.hasPermission(Administrator)) { implicit request =>
    val participation = participationDAO.find(id)
    val event = participation.event
    participationDAO.delete(participation.id.get)
    Redirect(routes.Events.show(event.id.get))
  }
}

