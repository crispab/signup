package controllers

import play.api.mvc._
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms.{tuple, nonEmptyText, text, optional, date}
import anorm.NotAssigned
import models.{Participation, Event, User}

object Events extends Controller {

  def list = Action {
    val events = Event.findAll()
    Ok(views.html.events.list("Found events: " + events))
  }

  def createForm = Action {
    val event = new Event(NotAssigned)
    Ok(views.html.events.edit(event, newEvent = true))
  }
  
  def updateForm(id: Long) = Action {
    val event = Event.find(id);
    Ok(views.html.events.edit(event, newEvent = false))
  }

  def show(id : Long) = Action {
    val event = Event.find(id)
    val registeredUsers = Participation.findRegistered(event)
    val unregisteredUsers = User.findUnregistered(event)
    Ok(views.html.events.show(event, unregisteredUsers, registeredUsers))
  }
  
  def create = Action {
    implicit request =>
      eventForm.bindFromRequest.fold(
      failingForm => {
        Logger.info("Errors: " + failingForm.errors)
        Redirect(routes.Events.createForm())
      }, {
        case (name, description, when, venue) => {
          Event.create(Event(
            name = name,
            description = description.getOrElse(""),
            when = when,
            venue = venue
          ))
          Redirect(routes.Events.list())
        }
      }
      )
  }

  def update(id: Long) = Action {
    implicit request =>
      eventForm.bindFromRequest.fold(
      failingForm => {
        Logger.info("Errors: " + failingForm.errors)
        Redirect(routes.Events.createForm())
      }, {
        case (name, description, when, venue) => {
          Event.update(id, Event(
            name = name,
            description = description.getOrElse(""),
            when = when,
            venue = venue
          ))
          Redirect(routes.Events.show(id))
        }
      }
      )
  }

  val eventForm = Form(
    tuple(
      "name" -> nonEmptyText,
      "description" -> optional(text),
      "when" -> date("yyyy-MM-dd HH:mm"),
      "venue" -> text
    )
  )

}

