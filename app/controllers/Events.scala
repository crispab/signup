package controllers

import java.text.SimpleDateFormat

import _root_.util.AuthHelper._
import anorm.{NotAssigned, Pk}
import models._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import util.DateHelper._
import java.util
import jp.t2v.lab.play2.auth.{AuthElement, OptionalAuthElement}
import models.security.Administrator
import play.api.libs.concurrent.Akka
import services.EventReminder

object Events extends Controller with OptionalAuthElement with AuthConfigImpl {

  def show(id : Long) = StackAction { implicit request =>
    val event = Event.find(id)
    Ok(views.html.events.show(event, Participation.findMembers(event), Participation.findGuests(event), LogEntry.findByEvent(event), Reminder.findByEvent(event)))
  }

  def asEmail(eventId: Long, userId: Long) = Action {
    val event = Event.find(eventId)
    val user = User.find(userId)
    import play.api.Play.current
    val baseUrl = play.api.Play.configuration.getString("email.notification.base.url").getOrElse("")
    Ok(views.html.events.email(event, user, baseUrl))
  }
}

object EventsSecured extends Controller with AuthElement with AuthConfigImpl {

  def notifyParticipants(id: Long) = StackAction(AuthorityKey -> hasPermission(Administrator)_) { implicit request =>
    val event = Event.find(id)

    import play.api.Play.current
    import play.api.libs.concurrent.Execution.Implicits._
    import scala.concurrent.duration._
    Akka.system.scheduler.scheduleOnce(1.second) {
      EventReminder.remindParticipants(event)
    }

    Redirect(routes.Events.show(id)).flashing("success" -> "En påminnelse om eventet kommer att skickas till alla delatagare som inte redan meddelat sig.")
  }

  def createForm(groupId: Long) = StackAction(AuthorityKey -> hasPermission(Administrator)_) { implicit request =>
    implicit val loggedInUser = Option(loggedIn)
    val group = Group.find(groupId)
    Ok(views.html.events.edit(eventForm, group))
  }

  def create = StackAction(AuthorityKey -> hasPermission(Administrator)_) { implicit request =>
    implicit val loggedInUser = Option(loggedIn)
      eventForm.bindFromRequest.fold(
        formWithErrors => {
          val groupId = formWithErrors("groupId").value.get.toLong
          val group = Group.find(groupId)
          BadRequest(views.html.events.edit(formWithErrors, group))
        },
        event => {
          val eventId = Event.create(event)
          Reminder.createRemindersForEvent(eventId, event)
          Redirect(routes.Groups.show(event.group.id.get))
        }
      )
  }


  def updateForm(id: Long) = StackAction(AuthorityKey -> hasPermission(Administrator)_) { implicit request =>
    implicit val loggedInUser = Option(loggedIn)
    val event = Event.find(id)
    Ok(views.html.events.edit(eventForm.fill(event), event.group, Option(id)))
  }

  def update(id: Long) = StackAction(AuthorityKey -> hasPermission(Administrator)_) { implicit request =>
    implicit val loggedInUser = Option(loggedIn)
      eventForm.bindFromRequest.fold(
        formWithErrors => {
          val event = Event.find(id)
          BadRequest(views.html.events.edit(formWithErrors, event.group, Option(id)))
        },
        event => {
          Event.update(id, event)
          Reminder.createRemindersForEvent(id, event)
          Redirect(routes.Events.show(id))
        }
      )
  }


  def delete(id: Long) = StackAction(AuthorityKey -> hasPermission(Administrator)_) { implicit request =>
    val event = Event.find(id)
    val groupId = event.group.id.get
    Event.delete(id)
    Redirect(routes.Groups.show(groupId))
  }

  val eventForm: Form[Event] =
    Form(
      mapping(
        "id" -> ignored(NotAssigned:Pk[Long]),
        "name" -> nonEmptyText(maxLength = 127),
        "description" -> text(maxLength = 10240),
        "start_date" -> date("yyyy-MM-dd"),
        "start_time" -> date("HH:mm"),
        "end_time" -> date("HH:mm"),
        "venue" -> text(maxLength = 127),
        "allow_extra_friends" -> boolean,
        "groupId" -> longNumber,
        "same_day" -> boolean,
        "last_signup_date" -> optional(date("yyyy-MM-dd"))
      )(toEvent)(fromEvent)
        .verifying("Sluttid måste vara efter starttid", event => event.startTime.before(event.endTime))
        .verifying("Sista anmälningsdag måste vara före själva eventet",
                   event => event.lastSignUpDate==event.startTime || event.lastSignUpDate.before(event.startTime))
    )

  def fromEvent(event: Event) = {
    val isSameDay = sameDay(event.startTime, event.lastSignUpDate)
    val lastSignUpDay = isSameDay match {
      case true => None
      case _ => Option(event.lastSignUpDate)
    }
    Option((event.id, event.name, event.description, event.startTime, event.startTime, event.endTime, event.venue, event.allowExtraFriends, event.group.id.get, isSameDay, lastSignUpDay))
  }

  def toEvent(
    id: Pk[Long],
    name: String,
    description: String,
    start_date: util.Date,
    start_time: util.Date,
    end_time: util.Date,
    venue: String,
    allow_extra_friends: Boolean,
    groupId: Long,
    sameDay: Boolean,
    last_signup_date: Option[util.Date]): Event = {

    val start_date_str = new SimpleDateFormat("yyyy-MM-dd").format(start_date)
    val start_time_str = new SimpleDateFormat("HH:mm").format(start_time)
    val end_time_str = new SimpleDateFormat("HH:mm").format(end_time)

    val lastSignUpDate = sameDay match {
      case true => start_date
      case _ => last_signup_date.getOrElse(start_date)
    }

    Event(
      id = id,
      name = name,
      description = description,
      startTime = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(start_date_str + " " + start_time_str),
      endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(start_date_str + " " + end_time_str),
      lastSignUpDate = lastSignUpDate,
      venue = venue,
      allowExtraFriends = allow_extra_friends,
      group = Group.find(groupId)
    )
  }
}

