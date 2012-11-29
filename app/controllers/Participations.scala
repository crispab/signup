package controllers

import scala.Long
import play.api.data.Form
import play.api.data.Forms._
import anorm.{Pk, NotAssigned}
import models.{Participation, Event, User}
import play.api.mvc._
import jp.t2v.lab.play20.auth.Auth
import models.security.Administrator

object Participations extends Controller with Auth with AuthConfigImpl {

  def createForm(eventId: Long, userId: Long) = optionalUserAction { implicit user => implicit request =>
    Ok(views.html.participations.edit(participationForm, User.find(userId), Event.find(eventId)))
  }

  def create = optionalUserAction { implicit user => implicit request =>
      participationForm.bindFromRequest.fold(
        formWithErrors => {
          val event = Event.find(formWithErrors("eventId").value.get.toLong)
          val userToAttend = User.find(formWithErrors("userId").value.get.toLong)
          BadRequest(views.html.participations.edit(formWithErrors, userToAttend, event))
        },
        participation => {
          Participation.create(participation)
          Redirect(routes.Events.show(participation.event.id.get))
        }
      )
  }

  def createGuestForm(eventId: Long) = authorizedAction(Administrator) { user => implicit request =>
    implicit val loggedInUser = Option(user)
    val event = Event.find(eventId)
    Ok(views.html.participations.addGuest(participationForm, event, User.findNonGuests(event.id.get)))
  }

  def createGuest = authorizedAction(Administrator) { user => implicit request =>
    implicit val loggedInUser = Option(user)
      participationForm.bindFromRequest.fold(
        formWithErrors => {
          val event = Event.find(formWithErrors("eventId").value.get.toLong)
          BadRequest(views.html.participations.addGuest(formWithErrors, event, User.findNonGuests(event.id.get)))
        },
        participation => {
          Participation.create(participation)
          Redirect(routes.Events.show(participation.event.id.get))
        }
      )
  }

  def updateForm(id: Long) = optionalUserAction { implicit user => implicit request =>
    val participation = Participation.find(id)
    Ok(views.html.participations.edit(participationForm.fill(participation), participation.user, participation.event, Option(id)))
  }

  def update(id: Long) = optionalUserAction { implicit user => implicit request =>
      participationForm.bindFromRequest.fold(
        formWithErrors => {
          val event = Event.find(formWithErrors("eventId").value.get.toLong)
          val userToAttend = User.find(formWithErrors("userId").value.get.toLong)
          BadRequest(views.html.participations.edit(formWithErrors, userToAttend, event, Option(id)))
        },
        participation => {
          Participation.update(id, participation)
          Redirect(routes.Events.show(participation.event.id.get))
        }
      )
  }

  val participationForm:Form[Participation] =
    Form(
      mapping(
        "id" -> ignored(NotAssigned:Pk[Long]),
        "status" -> nonEmptyText,
        "comment" -> text,
        "userId" -> longNumber,
        "eventId" -> longNumber
      )(toParticipation)(fromParticipation)
    )

  def toParticipation(
    id: Pk[Long],
    status: String,
    comment: String,
    userId: Long,
    eventId: Long): Participation = {

    Participation(
      id = id,
      status = models.Status.withName(status),
      comment = comment,
      user = User.find(userId),
      event = Event.find(eventId)
    )
  }

  def fromParticipation(participation: Participation) = {
    Option((participation.id, participation.status.toString, participation.comment, participation.user.id.get, participation.event.id.get))
  }
}

