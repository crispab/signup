package controllers

import anorm.{NotAssigned, Pk}
import jp.t2v.lab.play2.auth.Auth
import models.security.Administrator
import models.{Event, Participation, User}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._

object Participations extends Controller with Auth with AuthConfigImpl {

  def editForm(eventId: Long, userId: Long) = optionalUserAction { implicit user => implicit request =>
    val participation = Participation.findByEventAndUser(eventId, userId)
    if(participation.isDefined) {
      Ok(views.html.participations.edit(participationForm.fill(participation.get), User.find(userId), Event.find(eventId), Option(participation.get.id.get)))
    } else {
      Ok(views.html.participations.edit(participationForm, User.find(userId), Event.find(eventId)))
    }
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

  def delete(id: Long) = authorizedAction(Administrator) { user => implicit request =>
    val participation = Participation.find(id)
    val event = participation.event
    Participation.delete(participation.id.get)
    Redirect(routes.Events.show(event.id.get))
  }


  val participationForm:Form[Participation] =
    Form(
      mapping(
        "id" -> ignored(NotAssigned:Pk[Long]),
        "status" -> nonEmptyText(maxLength = 20),
        "number_of_participants" -> number(min = 1),
        "comment" -> text(maxLength = 127),
        "userId" -> longNumber,
        "eventId" -> longNumber
      )(toParticipation)(fromParticipation)
    )

  def toParticipation(
    id: Pk[Long],
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

  def fromParticipation(participation: Participation) = {
    Option((participation.id, participation.status.toString, participation.numberOfParticipants, participation.comment, participation.user.id.get, participation.event.id.get))
  }
}

