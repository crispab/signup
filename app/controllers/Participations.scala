package controllers

import play.api.mvc._
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms.{tuple, nonEmptyText, text, optional, longNumber}
import models.{Participation, Event, User}
import scala.Long

object Participations extends Controller {

  def createForm(eventId: Long, userId: Long) = Action {
    val event = Event.find(eventId);
    val user = User.find(userId);
    val participation = new Participation(user = user, event = event);
    Ok(views.html.participations.edit(participation, newParticipation = true))
  }

  def create = Action {
    implicit request =>
      participationForm.bindFromRequest.fold(
      failingForm => {
        Logger.info("Errors: " + failingForm.errors)
        Redirect(routes.Participations.createForm(-1, -1)) // TODO: Felhantering; user, event
      }, {
        case (status, comment, userId, eventId) => {
          Participation.create(Participation(
            status = models.Status.withName(status),
            comment = comment.getOrElse(""),
            user = User.find(userId),
            event = Event.find(eventId)
          ))
          Redirect(routes.Events.show(eventId))
        }
      }
      )
  }

  def updateForm(id: Long) = Action {
    val participation = Participation.find(id);
    Ok(views.html.participations.edit(participation, newParticipation = false))
  }

  def update(id: Long) = Action {
    implicit request =>
      participationForm.bindFromRequest.fold(
      failingForm => {
        Logger.info("Errors: " + failingForm.errors)
        Redirect(routes.Participations.createForm(-1, -1)) // TODO: Felhantering; user, event
      }, {
        case (status, comment, userId, eventId) => {
          Participation.update(id, Participation(
            status = models.Status.withName(status),
            comment = comment.getOrElse(""),
            user = User.find(userId),
            event = Event.find(eventId)
          ))
          Redirect(routes.Events.show(eventId))
        }
      }
      )
  }


  val participationForm = Form(
    tuple(
      "status" -> nonEmptyText,
      "comment" -> optional(text),
      "userId" -> longNumber,
      "eventId" -> longNumber
    )
  )

}

