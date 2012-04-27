package controllers

import play.api.mvc._
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms.{tuple, nonEmptyText, text, optional, date}
import anorm.NotAssigned
import models.Event

object Events extends Controller {

  def list = Action {
    val events = Event.findAll()
    Ok(views.html.events.list("Found events: " + events))
  }

  def createForm = Action {
    val event = new Event(NotAssigned)
    Ok(views.html.events.edit(event, newEvent = true))
  }

  def create = Action {
    implicit request =>
      eventForm.bindFromRequest.fold(
      failingForm => {
        Logger.info("Errors: " + failingForm.errors)
        Redirect(routes.Events.createForm)
      }, {
        case (name, description, when) => {
          Event.create(Event(
            name = name,
            description = description.getOrElse(""),
            when = when
          ))
          Redirect(routes.Events.list)
        }
      }
      )
  }

  val eventForm = Form(
    tuple(
      "name" -> nonEmptyText,
      "description" -> optional(text),
      "when" -> date("yyyy-MM-dd HH:mm")
    )
  )

}

