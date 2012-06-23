package controllers

import models.{Group, Participation, Event, User}
import java.text.SimpleDateFormat
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import anorm.{Pk, NotAssigned}
import java.util

object Events extends Controller {

  def list = Action {
    val events = Event.findAll()
    Ok(views.html.events.list(events))
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
  
  def createForm(groupId: Long) = Action {
    // TODO: use groupId
    Ok(views.html.events.edit(eventForm))
  }

  def create = Action {
    implicit request =>
      eventForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.events.edit(formWithErrors)),
        event => {
          Event.create(event)
          Redirect(routes.Groups.show(event.group.id.get))
        }
      )
  }


  def updateForm(id: Long) = Action {
    val event = Event.find(id)
    Ok(views.html.events.edit(eventForm.fill(event), Option(id)))
  }

  def update(id: Long) = Action {
    implicit request =>
      eventForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.events.edit(formWithErrors, Option(id))),
        event => {
          Event.update(id, event)
          Redirect(routes.Events.show(id))
        }
      )
  }


  def delete(id: Long) = Action {
    val event = Event.find(id)
    val groupId = event.group.id.get;
    Event.delete(id)
    Redirect(routes.Groups.show(groupId))
  }

  val eventForm: Form[Event] =
    Form(
      mapping(
        "id" -> ignored(NotAssigned:Pk[Long]),
        "name" -> nonEmptyText,
        "description" -> text,
        "start_date" -> date("yyyy-MM-dd"),
        "start_time" -> date("HH:mm"),
        "end_time" -> date("HH:mm"),
        "venue" -> text,
        "groupId" -> longNumber
      )(toEvent)(fromEvent)
    )

  def fromEvent(event: Event) = {
    Option((event.id, event.name, event.description, event.start_time, event.start_time, event.end_time, event.venue, event.group.id.get))
  }

  def toEvent(
    id: Pk[Long],
    name: String,
    description: String,
    start_date: util.Date,
    start_time: util.Date,
    end_time: util.Date,
    venue: String,
    groupId: Long): Event = {

    val start_date_str = new SimpleDateFormat("yyyy-MM-dd").format(start_date)
    val start_time_str = new SimpleDateFormat("HH:mm").format(start_time)
    val end_time_str = new SimpleDateFormat("HH:mm").format(end_time)
    Event(
      id = id,
      name = name,
      description = description,
      start_time = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(start_date_str + " " + start_time_str),
      end_time = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(start_date_str + " " + end_time_str),
      venue = venue,
      group = Group.find(groupId)
    )
  }
}

