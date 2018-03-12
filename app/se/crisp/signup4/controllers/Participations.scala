package se.crisp.signup4.controllers

import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import com.mohiva.play.silhouette.api.Silhouette
import play.api.Configuration
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n._
import play.api.mvc._
import se.crisp.signup4.models.Status._
import se.crisp.signup4.models._
import se.crisp.signup4.models.dao.{EventDAO, LogEntryDAO, ParticipationDAO, UserDAO}
import se.crisp.signup4.models.security.Administrator
import se.crisp.signup4.services.{ImageUrl, SlackReminder}
import se.crisp.signup4.silhouette.{DefaultEnv, WithPermission}
import se.crisp.signup4.util._

import scala.concurrent.ExecutionContext

@Singleton
class Participations @Inject()(val silhouette: Silhouette[DefaultEnv],
                               val actorSystem: ActorSystem,
                               implicit val configuration: Configuration,
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
                               implicit val imageUrl: ImageUrl,
                               implicit val ec: ExecutionContext) extends InjectedController  with I18nSupport{


  def editForm(eventId: Long, userId: Long): Action[AnyContent] = silhouette.UserAwareAction { implicit request =>
    implicit val user: Option[User] = request.identity
    val event = eventDAO.find(eventId)
    if (!event.isCancelled) {
      val userToAttend = userDAO.find(userId)
      val participation = participationDAO.findByEventAndUser(eventId, userId).getOrElse(new Participation(status = Unregistered, user = userToAttend, event = event))
      Ok(se.crisp.signup4.views.html.participations.edit(participationForm.fill(participation), userToAttend, event))
    } else {
      Redirect(routes.Events.show(eventId)).flashing("error" -> Messages("error.signup.eventcancelled"))
    }
  }

  private def asLogMessage(participation: Participation)(implicit lang: Lang, request: RequestHeader):String = {
    val message = new StringBuffer()
    message.append(Messages("participation.updated", participation.user.name, StatusHelper.asMessage(participation.status)(request2Messages)))

    if(participation.numberOfParticipants > 1) {
      message.append(" ").append(Messages("participation.people", participation.numberOfParticipants))
    }

    if(!participation.comment.isEmpty) {
      message.append(s" (${participation.comment})")
    }

    message.toString
  }

  def createOrUpdate: Action[AnyContent] = silhouette.UserAwareAction { implicit request =>
    implicit val user: Option[User] = request.identity
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
          logEntryDAO.create(updatedParticipation.event, asLogMessage(updatedParticipation)(localeHelper.getLang(request),request))


          import scala.concurrent.duration._
          actorSystem.scheduler.scheduleOnce(1.second) {
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


  def createGuestForm(eventId: Long): Action[AnyContent] = silhouette.SecuredAction(WithPermission(Administrator)) { implicit request =>
    implicit val loggedInUser: Option[User] = Option(request.identity)
    val event = eventDAO.find(eventId)
    Ok(se.crisp.signup4.views.html.participations.addGuest(participationForm, event, userDAO.findNonGuests(event.id.get)))
  }

  def createGuest: Action[AnyContent] = silhouette.SecuredAction(WithPermission(Administrator)) { implicit request =>
    implicit val loggedInUser: Option[User] = Option(request.identity)
    participationForm.bindFromRequest.fold(
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

  def delete(id: Long): Action[AnyContent] = silhouette.SecuredAction(WithPermission(Administrator)) { implicit request =>
    val participation = participationDAO.find(id)
    val event = participation.event
    participationDAO.delete(participation.id.get)
    Redirect(routes.Events.show(event.id.get))
  }
}
