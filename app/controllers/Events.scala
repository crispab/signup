package controllers

import play.api.mvc._
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms.{tuple, nonEmptyText, text, optional, date}
import anorm.NotAssigned
import models.{Participation, Event, User}
import java.text.SimpleDateFormat

object Events extends Controller {

  def list = Action {
    val events = Event.findAll()
    Ok(views.html.events.list(events))
  }

  def createForm = Action {
    val event = new Event(NotAssigned)
    Ok(views.html.events.edit(event, newEvent = true))
  }
  
  def updateForm(id: Long) = Action {
    val event = Event.find(id)
    Ok(views.html.events.edit(event, newEvent = false))
  }

  def show(id : Long) = Action {
    val event = Event.find(id)
    val registeredUsers = Participation.findRegistered(event)
    val unregisteredUsers = User.findUnregistered(event)
    Ok(views.html.events.show(event, unregisteredUsers, registeredUsers))
  }

  def asCalendar(id: Long) = Action {
    val event = Event.find(id)
    Ok(views.txt.events.ical(event)).as("text/calendar")
  }
  
  def create = Action {
    implicit request =>
      eventForm.bindFromRequest.fold(
      failingForm => {
        Logger.info("Errors: " + failingForm.errors)
        Redirect(routes.Events.createForm())
      }, {
        case (name, description, start_date, start_time, end_time, venue) => {
          val start_date_str = new SimpleDateFormat("yyyy-MM-dd").format(start_date)
          val start_time_str = new SimpleDateFormat("HH:mm").format(start_time)
          val end_time_str = new SimpleDateFormat("HH:mm").format(end_time)
          Event.create(Event(
            name = name,
            description = description.getOrElse(""),
            start_time = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(start_date_str + " " + start_time_str),
            end_time = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(start_date_str + " " + end_time_str),
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
        case (name, description, start_date, start_time, end_time, venue) => {
          val start_date_str = new SimpleDateFormat("yyyy-MM-dd").format(start_date)
          val start_time_str = new SimpleDateFormat("HH:mm").format(start_time)
          val end_time_str = new SimpleDateFormat("HH:mm").format(end_time)
          Event.update(id, Event(
            name = name,
            description = description.getOrElse(""),
            start_time = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(start_date_str + " " + start_time_str),
            end_time = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(start_date_str + " " + end_time_str),
            venue = venue
          ))
          Redirect(routes.Events.show(id))
        }
      }
      )
  }

  def delete(id: Long) = Action {
    Event.delete(id)
    Redirect(routes.Events.list())
  }

  val eventForm = Form(
    tuple(
      "name" -> nonEmptyText,
      "description" -> optional(text),
      "start_date" -> date("yyyy-MM-dd"),
      "start_time" -> date("HH:mm"),
      "end_time" -> date("HH:mm"),
      "venue" -> text
    )
  )

}

