package controllers

import scala.Long
import play.api.data.Form
import play.api.data.Forms._
import anorm.{Pk, NotAssigned}
import models.{Participation, Event, User}
import play.api.mvc._

object Participations extends Controller {

  def createForm(eventId: Long, userId: Long) = Action {
    Ok(views.html.participations.edit(participationForm, User.find(userId), Event.find(eventId)))
  }

  def create = Action {
    implicit request =>
      participationForm.bindFromRequest.fold(
        formWithErrors => {
          val event = Event.find(formWithErrors("eventId").value.get.toLong)
          val user = User.find(formWithErrors("userId").value.get.toLong)
          BadRequest(views.html.participations.edit(formWithErrors, user, event))
        },
        participation => {
          Participation.create(participation)
          Redirect(routes.Events.show(participation.event.id.get))
        }
      )
  }

  def createGuestForm(eventId: Long) = Action {
    val event = Event.find(eventId)
    Ok(views.html.participations.addGuest(participationForm, event, User.findNonGuests(event.id.get)))
  }

  def createGuest = Action {
    implicit request =>
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

  def updateForm(id: Long) = Action {
    val participation = Participation.find(id)
    Ok(views.html.participations.edit(participationForm.fill(participation), participation.user, participation.event, Option(id)))
  }

  def update(id: Long) = Action {
    implicit request =>
      participationForm.bindFromRequest.fold(
        formWithErrors => {
          val event = Event.find(formWithErrors("eventId").value.get.toLong)
          val user = User.find(formWithErrors("userId").value.get.toLong)
          BadRequest(views.html.participations.edit(formWithErrors, user, event, Option(id)))
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

