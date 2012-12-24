package controllers

import models._
import java.text.SimpleDateFormat
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import anorm.{Pk, NotAssigned}
import java.util
import play.api.libs.concurrent.Akka
import services.EventNotifier
import jp.t2v.lab.play20.auth.Auth
import models.security.Administrator

object Events extends Controller with Auth with AuthConfigImpl {

  def list = optionalUserAction { implicit user => implicit request =>
    val events = Event.findAll()
    Ok(views.html.events.list(events))
  }

  def show(id : Long) = optionalUserAction { implicit user => implicit request =>
    val event = Event.find(id)
    Ok(views.html.events.show(event, User.findUnregisteredMembers(event), Participation.findRegisteredMembers(event), Participation.findGuests(event)))
  }

  def asCalendar(id: Long) = optionalUserAction { implicit user => implicit request =>
    val event = Event.find(id)
    Ok(views.txt.events.ical(event)).as("text/calendar")
  }

  def asEmail(id: Long) = Action {
    val event = Event.find(id)
    import play.api.Play.current
    val baseUrl = play.api.Play.configuration.getString("email.notification.base.url").getOrElse("")
    Ok(views.html.events.email(event, baseUrl))
  }

  def notifyParticipants(id: Long) = authorizedAction(Administrator) { user => implicit request =>
    val event = Event.find(id)

    import play.api.Play.current
    Akka.future {
      EventNotifier.notifyParticipants(event)
    }

    Redirect(routes.Events.show(id)).flashing("success" -> "En påminnelse om eventet kommer att skickas till alla delatagare som inte redan meddelat sig.")
  }

  def createForm(groupId: Long) = authorizedAction(Administrator) { user => implicit request =>
    implicit val loggedInUser = Option(user)
    val group = Group.find(groupId)
    Ok(views.html.events.edit(eventForm, group))
  }

  def create = authorizedAction(Administrator) { user => implicit request =>
    implicit val loggedInUser = Option(user)
      eventForm.bindFromRequest.fold(
        formWithErrors => {
          val groupId = formWithErrors("groupId").value.get.toLong
          val group = Group.find(groupId)
          BadRequest(views.html.events.edit(formWithErrors, group))
        },
        event => {
          Event.create(event)
          Redirect(routes.Groups.show(event.group.id.get))
        }
      )
  }


  def updateForm(id: Long) = authorizedAction(Administrator) { user => implicit request =>
    implicit val loggedInUser = Option(user)
    val event = Event.find(id)
    Ok(views.html.events.edit(eventForm.fill(event), event.group, Option(id)))
  }

  def update(id: Long) = authorizedAction(Administrator) { user => implicit request =>
    implicit val loggedInUser = Option(user)
      eventForm.bindFromRequest.fold(
        formWithErrors => {
          val event = Event.find(id)
          BadRequest(views.html.events.edit(formWithErrors, event.group, Option(id)))
        },
        event => {
          Event.update(id, event)
          Redirect(routes.Events.show(id))
        }
      )
  }


  def delete(id: Long) = authorizedAction(Administrator) { user => implicit request =>
    val event = Event.find(id)
    val groupId = event.group.id.get
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
    Option((event.id, event.name, event.description, event.startTime, event.startTime, event.endTime, event.venue, event.group.id.get))
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
      startTime = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(start_date_str + " " + start_time_str),
      endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(start_date_str + " " + end_time_str),
      venue = venue,
      group = Group.find(groupId)
    )
  }
}

