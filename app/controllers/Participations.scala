package controllers

import jp.t2v.lab.play2.auth.{AuthElement, OptionalAuthElement}
import models.security.Administrator
import models.{Event, LogEntry, Participation, User}
import models.Status._
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{Lang, Messages}
import play.api.libs.concurrent.Akka
import play.api.mvc._
import services.SlackReminder
import util.AuthHelper._
import util.StatusHelper

object Participations extends Controller with OptionalAuthElement with AuthConfigImpl {

  def editForm(eventId: Long, userId: Long) = StackAction { implicit request =>
    val event = Event.find(eventId)
    if (!event.isCancelled) {
      val userToAttend = User.find(userId)
      val participation = Participation.findByEventAndUser(eventId, userId).getOrElse(Participation(status = Unregistered, user = userToAttend, event = event))
      Ok(views.html.participations.edit(participationForm.fill(participation), userToAttend, event))
    } else {
      Redirect(routes.Events.show(eventId)).flashing("error" -> Messages("error.signup.eventcancelled"))
    }
  }

  private def asLogMessage(participation: Participation)(implicit lang: Lang):String = {
    val message = new StringBuffer()
    message.append(Messages("participation.updated", participation.user.name, StatusHelper.asMessage(participation.status)))

    if(participation.numberOfParticipants > 1) {
      message.append(s" ${participation.numberOfParticipants} ")
      message.append(Messages("participation.people"))
    }

    if(!participation.comment.isEmpty) {
      message.append(s" (${participation.comment})")
    }

    message.toString
  }

  def createOrUpdate = StackAction { implicit request =>
    participationForm.bindFromRequest.fold(
      formWithErrors => {
        val event = Event.find(formWithErrors("eventId").value.get.toLong)
        val userToAttend = User.find(formWithErrors("userId").value.get.toLong)
        BadRequest(views.html.participations.edit(formWithErrors, userToAttend, event))
      },
      participation => {
        val existingParticipation = Participation.findByEventAndUser(participation.event.id.get, participation.user.id.get)
        if (existingParticipation.isEmpty) {
          Participation.create(participation)
        } else {
          Participation.update(existingParticipation.get.id.get, participation)
          val updatedParticipation = Participation.find(existingParticipation.get.id.get)
          LogEntry.create(updatedParticipation.event, asLogMessage(updatedParticipation))

          import play.api.Play.current
          import play.api.libs.concurrent.Execution.Implicits._
          import scala.concurrent.duration._
          Akka.system.scheduler.scheduleOnce(1.second) {
            SlackReminder.sendUpdatedParticipationMessage(updatedParticipation)
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

  def toParticipation(
                       id: Option[Long],
                       status: String,
                       numberOfParticipants: Int,
                       comment: String,
                       userId: Long,
                       eventId: Long): Participation = {

    Participation(
      id = id,
      status = models.Status.withName(status),
      numberOfParticipants = numberOfParticipants,
      comment = comment,
      user = User.find(userId),
      event = Event.find(eventId)
    )
  }

  def fromParticipation(participation: Participation): Option[(Option[Long], String, Int, String, Long, Long)] = {
    Option((participation.id, participation.status.toString, participation.numberOfParticipants, participation.comment, participation.user.id.get, participation.event.id.get))
  }
}


object ParticipationsSecured extends Controller with AuthElement with AuthConfigImpl {
  def createGuestForm(eventId: Long): Action[AnyContent] = StackAction(AuthorityKey -> hasPermission(Administrator)) { implicit request =>
    implicit val loggedInUser = Option(loggedIn)
    val event = Event.find(eventId)
    Ok(views.html.participations.addGuest(Participations.participationForm, event, User.findNonGuests(event.id.get)))
  }

  def createGuest: Action[AnyContent] = StackAction(AuthorityKey -> hasPermission(Administrator)) { implicit request =>
    implicit val loggedInUser = Option(loggedIn)
    Participations.participationForm.bindFromRequest.fold(
      formWithErrors => {
        val event = Event.find(formWithErrors("eventId").value.get.toLong)
        BadRequest(views.html.participations.addGuest(formWithErrors, event, User.findNonGuests(event.id.get)))
      },
      participation => {
        Participation.createGuest(participation.event.id.get, participation.user.id.get)
        Redirect(routes.Events.show(participation.event.id.get))
      }
    )
  }

  def delete(id: Long): Action[AnyContent] = StackAction(AuthorityKey -> hasPermission(Administrator)) { implicit request =>
    val participation = Participation.find(id)
    val event = participation.event
    Participation.delete(participation.id.get)
    Redirect(routes.Events.show(event.id.get))
  }
}

