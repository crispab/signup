package se.crisp.signup4.controllers

import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.{Inject, Named}

import akka.actor.{ActorRef, ActorSystem}
import akka.stream.scaladsl.Source
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import play.api.Configuration
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{I18nSupport, Messages}
import play.api.libs.iteratee.Enumerator
import play.api.libs.iteratee.streams.IterateeStreams
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import se.crisp.signup4.models._
import se.crisp.signup4.models.dao._
import se.crisp.signup4.models.security.Administrator
import se.crisp.signup4.services.{ImageUrl, MailReminder, RemindAllParticipants, SlackReminder}
import se.crisp.signup4.silhouette.{DefaultEnv, WithPermission}
import se.crisp.signup4.util.DateHelper.sameDay
import se.crisp.signup4.util._

import scala.concurrent.ExecutionContext

class Events @Inject()(val silhouette: Silhouette[DefaultEnv],
                       implicit val configuration: Configuration,
                       implicit val authHelper: AuthHelper,
                       implicit val localeHelper: LocaleHelper,
                       implicit val themeHelper: ThemeHelper,
                       implicit val formHelper: FormHelper,
                       implicit val htmlHelper: HtmlHelper,
                       implicit val eventDAO: EventDAO,
                       val userDAO: UserDAO,
                       val participationDAO: ParticipationDAO,
                       val logEntryDAO: LogEntryDAO,
                       implicit val reminderDAO: ReminderDAO,
                       implicit val imageUrl: ImageUrl,
                       val actorSystem: ActorSystem,
                       val mailReminder: MailReminder,
                       @Named("event-reminder-actor") val eventReminderActor: ActorRef,
                       val slackReminder: SlackReminder,
                       val groupDAO: GroupDAO,
                       implicit val ec: ExecutionContext
                      ) extends InjectedController  with I18nSupport  {

  def show(id: Long): Action[AnyContent] = silhouette.UserAwareAction { implicit request =>
    implicit val user: Option[User] =  request.identity
    val event = eventDAO.find(id)
    if (event.isCancelled) {
      Ok(se.crisp.signup4.views.html.events.showcancelled(event, logEntryDAO.findByEvent(event)))
    } else {
      Ok(se.crisp.signup4.views.html.events.show(event, participationDAO.findMembers(event), participationDAO.findGuests(event), logEntryDAO.findByEvent(event), reminderDAO.findByEvent(event)))
    }
  }

  def asExcel(id: Long): Action[AnyContent] = silhouette.UserAwareAction { implicit request =>

    val event = eventDAO.find(id)
    val workbook = ExcelHelper.createWorkbook(allGuests(event), allMembers(event))

    val enumerator = Enumerator.outputStream { outputStream =>
      workbook.write(outputStream)
      outputStream.close()
    }

    val source = Source.fromPublisher(IterateeStreams.enumeratorToPublisher(enumerator))

    Ok.chunked(source).withHeaders(
      "Content-Type" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
      "Content-Disposition" -> ("attachment; filename=" + event.name + ".xlsx")
    )
  }


  private def allGuests(event: Event) = {
    val guestParticipations = participationDAO.findGuests(event)
    (guestParticipations.on
      union guestParticipations.maybe
      union guestParticipations.off
      union guestParticipations.unregistered)
  }

  private def allMembers(event: Event) = {
    val memberParticipations = participationDAO.findMembers(event)
    (memberParticipations.on
      union memberParticipations.maybe
      union memberParticipations.off
      union memberParticipations.unregistered)
  }


  def asEmailReminder(eventId: Long, userId: Long) = Action { implicit request =>
    val event = eventDAO.find(eventId)
    val user = userDAO.find(userId)
    val baseUrl = configuration.get[String]("application.base.url")

    // TODO: get rid of this by using SendGrid mail templates instead
    if (themeHelper.THEME == "b73") {
      Ok(se.crisp.signup4.views.html.events.b73.emailremindermessage(event, user, baseUrl))
    } else {
      Ok(se.crisp.signup4.views.html.events.crisp.emailremindermessage(event, user, baseUrl))
    }
  }


  def asEmailCancellation(eventId: Long, userId: Long) = Action { implicit request =>
    val event = eventDAO.find(eventId)
    val user = userDAO.find(userId)
    val baseUrl = configuration.get[String]("application.base.url")

    // TODO: get rid of this by using SendGrid mail templates instead
    if (themeHelper.THEME == "b73") {
      Ok(se.crisp.signup4.views.html.events.b73.emailcancellationmessage(event, user, baseUrl))
    } else {
      Ok(se.crisp.signup4.views.html.events.crisp.emailcancellationmessage(event, user, baseUrl))
    }
  }


  def asSlackReminder(id: Long) = Action { implicit request =>
    val event = eventDAO.find(id)

    val baseUrl = configuration.get[String]("application.base.url")
    val message: JsValue = Json.parse(se.crisp.signup4.views.txt.events.slackremindermessage(event, baseUrl).toString())

    Ok(message)
  }

  def asSlackCancellation(id: Long) = Action { implicit request =>
    val event = eventDAO.find(id)

    val baseUrl = configuration.get[String]("application.base.url")
    val message: JsValue = Json.parse(se.crisp.signup4.views.txt.events.slackcancellationmessage(event, baseUrl).toString())

    Ok(message)
  }

  def remindParticipants(id: Long): Action[AnyContent] = silhouette.SecuredAction(WithPermission(Administrator)) { implicit request =>
    val event = eventDAO.find(id)
    if (!event.isCancelled) {
      eventReminderActor ! RemindAllParticipants(event, request.identity)
      Redirect(routes.Events.show(id)).flashing("success" -> Messages("event.remindersent"))
    } else {
      Redirect(routes.Events.show(id)).flashing("error" -> Messages("event.cancelled.noreminders"))
    }
  }

  def createForm(groupId: Long): Action[AnyContent] = silhouette.SecuredAction(WithPermission(Administrator)) { implicit request =>
    implicit val loggedInUser: Option[User] = Option(request.identity)
    val group = groupDAO.find(groupId)
    Ok(se.crisp.signup4.views.html.events.edit(eventForm, group))
  }


  def create: Action[AnyContent] = silhouette.SecuredAction(WithPermission(Administrator)) { implicit request =>
    implicit val loggedInUser: Option[User] = Option(request.identity)
    eventForm.bindFromRequest.fold(
      formWithErrors => {
        val groupId = formWithErrors("groupId").value.get.toLong
        val group = groupDAO.find(groupId)
        BadRequest(se.crisp.signup4.views.html.events.edit(formWithErrors, group))
      },
      event => {
        val eventId = eventDAO.create(event)
        reminderDAO.createRemindersForEvent(eventId, event)
        logEntryDAO.create(eventId, Messages("event.createdby", request.identity.name))

        if (isReminderToBeSent(request)) {
          val storedEvent = eventDAO.find(eventId)
          eventReminderActor ! RemindAllParticipants(storedEvent, request.identity)
          Redirect(routes.Events.show(storedEvent.id.get)).flashing("success" -> Messages("event.remindersent.all"))
        } else {
          Redirect(routes.Events.show(eventId))
        }
      }
    )
  }

  private def isReminderToBeSent(request: SecuredRequest[DefaultEnv, AnyContent]) = {
    val immediateReminderParameter = request.body.asFormUrlEncoded.get.get("immediate_reminder")
    immediateReminderParameter.isDefined && immediateReminderParameter.head.head.equals("true")
  }


  def updateForm(id: Long): Action[AnyContent] = silhouette.SecuredAction(WithPermission(Administrator)) { implicit request =>
    implicit val loggedInUser: Option[User] = Option(request.identity)
    val event = eventDAO.find(id)
    if (!event.isCancelled) {
      Ok(se.crisp.signup4.views.html.events.edit(eventForm.fill(event), event.group, Option(id)))
    } else {
      Redirect(routes.Events.show(id)).flashing("error" -> Messages("event.cancelled.noedit"))
    }
  }

  def update(id: Long): Action[AnyContent] = silhouette.SecuredAction(WithPermission(Administrator)) { implicit request =>
    implicit val loggedInUser: Option[User] = Option(request.identity)
    val event = eventDAO.find(id)
    if (!event.isCancelled) {
      eventForm.bindFromRequest.fold(
        formWithErrors => {
          val event = eventDAO.find(id)
          BadRequest(se.crisp.signup4.views.html.events.edit(formWithErrors, event.group, Option(id)))
        },
        event => {
          eventDAO.update(id, event)
          reminderDAO.createRemindersForEvent(id, event)
          Redirect(routes.Events.show(id))
        }
      )
    } else {
      Redirect(routes.Events.show(id)).flashing("error" -> Messages("event.cancelled.noedit"))
    }
  }


  def cancel(id: Long): Action[AnyContent] = silhouette.SecuredAction(WithPermission(Administrator)) { implicit request =>
    implicit val user: User =  request.identity
    val event = eventDAO.find(id)

    val reason = Option(request.body.asFormUrlEncoded.get.get("reason").head.head).filter(_.trim.nonEmpty)
    eventDAO.cancel(id, reason)
    logEntryDAO.create(event, Messages("event.cancelledby", request.identity.name, reason.getOrElse("ej angiven")))


    import scala.concurrent.duration._
    actorSystem.scheduler.scheduleOnce(1.second) {
      val cancelledEvent = eventDAO.find(id)
      mailReminder.sendCancellationMessage(cancelledEvent)
      slackReminder.sendCancellationMessage(cancelledEvent)
    }

    Redirect(routes.Events.show(id))
  }


  def delete(id: Long): Action[AnyContent] = silhouette.SecuredAction(WithPermission(Administrator)) { implicit request =>
    val event = eventDAO.find(id)
    val groupId = event.group.id.get
    eventDAO.delete(id)
    Redirect(routes.Groups.show(groupId))
  }

  val eventForm: Form[Event] =
    Form(
      mapping(
        "id" -> ignored(None: Option[Long]),
        "name" -> nonEmptyText(maxLength = 127),
        "description" -> text(maxLength = 10240),
        "start_date" -> date("yyyy-MM-dd"),
        "start_time" -> date("HH:mm"),
        "end_time" -> date("HH:mm"),
        "venue" -> text(maxLength = 127),
        "invited" -> nonEmptyText,
        "groupId" -> longNumber,
        "same_day" -> boolean,
        "last_signup_date" -> optional(date("yyyy-MM-dd")),
        "max_participants" -> optional(number(min = 1))
      )(toEvent)(fromEvent)
        .verifying("error.signup.endtime", event => event.startTime.before(event.endTime))
        .verifying("error.signup.lastsignup", event => event.lastSignUpDate == event.startTime || event.lastSignUpDate.before(event.startTime))
    )

  def fromEvent(event: Event): Option[(Option[Long], String, String, Date, Date, Date, String, String, Long, Boolean, Option[Date], Option[Int])] = {
    val isSameDay = sameDay(event.startTime, event.lastSignUpDate)
    val lastSignUpDay = if (isSameDay) {
      None
    } else {
      Option(event.lastSignUpDate)
    }

    val invited = if (event.allowExtraFriends) {
      "allow_extra_friends"
    } else if (event.maxParticipants.isDefined) {
      "max_number_of_participants_selected"
    } else {
      "invited_only"
    }

    Option((event.id, event.name, event.description, event.startTime, event.startTime, event.endTime, event.venue, invited, event.group.id.get, isSameDay, lastSignUpDay, event.maxParticipants))
  }

  def toEvent(
               id: Option[Long],
               name: String,
               description: String,
               start_date: Date,
               start_time: Date,
               end_time: Date,
               venue: String,
               invited: String,
               groupId: Long,
               sameDay: Boolean,
               last_signup_date: Option[Date],
               max_participants: Option[Int]): Event = {

    val start_date_str = new SimpleDateFormat("yyyy-MM-dd").format(start_date)
    val start_time_str = new SimpleDateFormat("HH:mm").format(start_time)
    val end_time_str = new SimpleDateFormat("HH:mm").format(end_time)

    val lastSignUpDate = if (sameDay) {
      start_date
    } else {
      last_signup_date.getOrElse(start_date)
    }

    Event(
      id = id,
      name = name,
      description = description,
      startTime = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(start_date_str + " " + start_time_str),
      endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(start_date_str + " " + end_time_str),
      lastSignUpDate = lastSignUpDate,
      venue = venue,
      allowExtraFriends = invited equals "allow_extra_friends",
      group = groupDAO.find(groupId),
      maxParticipants = max_participants
    )
  }
}



