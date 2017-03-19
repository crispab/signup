package controllers

import java.text.SimpleDateFormat

import models._
import models.Status._
import play.api.libs.json.{JsValue, Json}
import util.AuthHelper._
import util.DateHelper._
import util.StatusHelper._
import util.ThemeHelper._
import org.apache.poi.xssf.usermodel.{XSSFSheet, XSSFWorkbook}
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.iteratee.Enumerator
import play.api.mvc._
import java.util
import java.util.Date

import jp.t2v.lab.play2.auth.{AuthElement, OptionalAuthElement}
import jp.t2v.lab.play2.stackc.RequestWithAttributes
import models.security.Administrator
import play.api.libs.concurrent.Akka
import services.{EventReminderActor, MailReminder, RemindAllParticipants, SlackReminder}

import scala.concurrent.ExecutionContext

object Events extends Controller with OptionalAuthElement with AuthConfigImpl {

  def show(id: Long) = StackAction { implicit request =>
    val event = Event.find(id)
    if(event.isCancelled) {
      Ok(views.html.events.showcancelled(event, LogEntry.findByEvent(event)))
    } else {
      Ok(views.html.events.show(event, Participation.findMembers(event), Participation.findGuests(event), LogEntry.findByEvent(event), Reminder.findByEvent(event)))
    }
  }

  def asExcel(id: Long) = StackAction { implicit request =>

    val workbook = new XSSFWorkbook()
    val sheet = workbook.createSheet("Anmälningar")
    createHeading(workbook, sheet)

    val event = Event.find(id)
    val guests = allGuests(event)
    populateWithInvitedStatus(sheet, guests, areGuests = true)

    val members = allMembers(event)
    populateWithInvitedStatus(sheet, members, startRow = guests.size + 1)

    autosizeAllColumns(sheet)

    import ExecutionContext.Implicits.global
    val enumerator = Enumerator.outputStream { outputStream =>
      workbook.write(outputStream)
      outputStream.close()
    }

    Ok.chunked(enumerator >>> Enumerator.eof).withHeaders(
      "Content-Type" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
      "Content-Disposition" -> ("attachment; filename=" + event.name + ".xlsx")
    )
  }

  private def createHeading(workbook: XSSFWorkbook, sheet: XSSFSheet) {
    val heading = sheet.createRow(0)
    heading.createCell(0).setCellValue("Förnamn")
    heading.createCell(1).setCellValue("Efternamn")
    heading.createCell(2).setCellValue("Epost")
    heading.createCell(3).setCellValue("Status")
    heading.createCell(4).setCellValue("Antal")
    heading.createCell(5).setCellValue("Datum")
    heading.createCell(6).setCellValue("Gäst?")
    heading.createCell(7).setCellValue("Sen?")
    heading.createCell(8).setCellValue("Kommentar")

    val headingFont = workbook.createFont()
    headingFont.setBold(true)
    val headingStyle = workbook.createCellStyle()
    headingStyle.setFont(headingFont)

    heading.getCell(0).setCellStyle(headingStyle)
    heading.getCell(1).setCellStyle(headingStyle)
    heading.getCell(2).setCellStyle(headingStyle)
    heading.getCell(3).setCellStyle(headingStyle)
    heading.getCell(4).setCellStyle(headingStyle)
    heading.getCell(5).setCellStyle(headingStyle)
    heading.getCell(6).setCellStyle(headingStyle)
    heading.getCell(7).setCellStyle(headingStyle)
    heading.getCell(8).setCellStyle(headingStyle)
  }

  private def allGuests(event: Event) = {
    val guestParticipations = Participation.findGuests(event)
    (guestParticipations.on
      union guestParticipations.maybe
      union guestParticipations.off
      union guestParticipations.unregistered)
  }

  private def allMembers(event: Event) = {
    val memberParticipations = Participation.findMembers(event)
    (memberParticipations.on
      union memberParticipations.maybe
      union memberParticipations.off
      union memberParticipations.unregistered)
  }

  private def populateWithInvitedStatus(sheet: XSSFSheet, invited: Seq[Participation], startRow: Int = 1, areGuests: Boolean = false) {
    var rowNumber = startRow

    val workbook = sheet.getWorkbook
    val createHelper = workbook.getCreationHelper
    val dateStyle = workbook.createCellStyle()
    dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-mm-dd hh:mm;@"))

    for (participation <- invited) {
      val row = sheet.createRow(rowNumber)
      row.createCell(0).setCellValue(participation.user.firstName)
      row.createCell(1).setCellValue(participation.user.lastName)
      row.createCell(2).setCellValue(participation.user.email)
      row.createCell(3).setCellValue(asMessage(participation.status))
      row.createCell(4).setCellValue(participation.participantsComing)
      if(participation.status != Unregistered && participation.signUpTime.isDefined) {
        val cell = row.createCell(5)
        cell.setCellStyle(dateStyle)
        cell.setCellValue(participation.signUpTime.get)
      }
      if(areGuests){
        row.createCell(6).setCellValue("Gäst")
      }
      if(participation.isLateSignUp){
        row.createCell(7).setCellValue("Sen")
      }
      row.createCell(8).setCellValue(participation.comment)
      rowNumber += 1
    }
  }

  private def autosizeAllColumns(sheet: XSSFSheet) {
    sheet.autoSizeColumn(0)
    sheet.autoSizeColumn(1)
    sheet.autoSizeColumn(2)
    sheet.autoSizeColumn(3)
    sheet.autoSizeColumn(4)
    sheet.autoSizeColumn(5)
    sheet.autoSizeColumn(6)
    sheet.autoSizeColumn(7)
    sheet.autoSizeColumn(8)
  }



  def asEmailReminder(eventId: Long, userId: Long) = Action {
    val event = Event.find(eventId)
    val user = User.find(userId)
    import play.api.Play.current
    val baseUrl = play.api.Play.configuration.getString("application.base.url").getOrElse("")

    // TODO: get rid of this by using SendGrid mail templates instead
    if(THEME == "b73") {
      Ok(views.html.events.b73.emailremindermessage(event, user, baseUrl))
    } else {
      Ok(views.html.events.crisp.emailremindermessage(event, user, baseUrl))
    }
  }


  def asEmailCancellation(eventId: Long, userId: Long) = Action {
    val event = Event.find(eventId)
    val user = User.find(userId)
    import play.api.Play.current
    val baseUrl = play.api.Play.configuration.getString("application.base.url").getOrElse("")

    // TODO: get rid of this by using SendGrid mail templates instead
    if(THEME == "b73") {
      Ok(views.html.events.b73.emailcancellationmessage(event, user, baseUrl))
    } else {
      Ok(views.html.events.crisp.emailcancellationmessage(event, user, baseUrl))
    }
  }


  def asSlackReminder(id: Long) = Action { implicit request =>
    val event = Event.find(id)

    import play.api.Play.current
    val baseUrl = play.api.Play.configuration.getString("application.base.url").getOrElse("")
    val message: JsValue = Json.parse(views.txt.events.slackremindermessage(event, baseUrl).toString())

    Ok(message)
  }

  def asSlackCancellation(id: Long) = Action { implicit request =>
    val event = Event.find(id)

    import play.api.Play.current
    val baseUrl = play.api.Play.configuration.getString("application.base.url").getOrElse("")
    val message: JsValue = Json.parse(views.txt.events.slackcancellationmessage(event, baseUrl).toString())

    Ok(message)
  }


}

object EventsSecured extends Controller with AuthElement with AuthConfigImpl {

  def remindParticipants(id: Long): Action[AnyContent] = StackAction(AuthorityKey -> hasPermission(Administrator)) { implicit request =>
    val event = Event.find(id)
    if(!event.isCancelled) {
      EventReminderActor.instance() ! RemindAllParticipants(event, loggedIn)
      Redirect(routes.Events.show(id)).flashing("success" -> "En påminnelse om sammankomsten kommer att skickas till alla delatagare som inte redan meddelat sig.")
    } else {
      Redirect(routes.Events.show(id)).flashing("error" -> "Sammankomsten är inställd. Det går inte att skicka påminnelser.")
    }
  }

  def createForm(groupId: Long): Action[AnyContent] = StackAction(AuthorityKey -> hasPermission(Administrator)) { implicit request =>
    implicit val loggedInUser = Option(loggedIn)
    val group = Group.find(groupId)
    Ok(views.html.events.edit(eventForm, group))
  }

  def create: Action[AnyContent] = StackAction(AuthorityKey -> hasPermission(Administrator)) { implicit request =>
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
          LogEntry.create(eventId, "Sammankomsten skapad av " + loggedIn.name)

          if(isReminderToBeSent(request)) {
            val storedEvent = Event.find(eventId)
            EventReminderActor.instance() ! RemindAllParticipants(storedEvent, loggedIn)
            Redirect(routes.Events.show(storedEvent.id.get)).flashing("success" -> "En inbjudan till sammankomsten håller på att skickas till alla.")
          } else {
            Redirect(routes.Events.show(eventId))
          }
        }
      )
  }

  private def isReminderToBeSent(request: RequestWithAttributes[AnyContent]) = {
    val immediateReminderParameter = request.body.asFormUrlEncoded.get.get("immediate_reminder")
    immediateReminderParameter.isDefined && immediateReminderParameter.head.head.equals("true")
  }


  def updateForm(id: Long): Action[AnyContent] = StackAction(AuthorityKey -> hasPermission(Administrator)) { implicit request =>
    implicit val loggedInUser = Option(loggedIn)
    val event = Event.find(id)
    if(!event.isCancelled) {
      Ok(views.html.events.edit(eventForm.fill(event), event.group, Option(id)))
    } else {
      Redirect(routes.Events.show(id)).flashing("error" -> "Sammankomsten är inställd. Ded går inte att redigera. Skapa en ny istället.")
    }
  }

  def update(id: Long): Action[AnyContent] = StackAction(AuthorityKey -> hasPermission(Administrator)) { implicit request =>
    implicit val loggedInUser = Option(loggedIn)
    val event = Event.find(id)
    if(!event.isCancelled) {
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
    } else {
      Redirect(routes.Events.show(id)).flashing("error" -> "Sammankomsten är inställd. Den går inte att redigera. Skapa en ny istället.")
    }
  }


  def cancel(id: Long): Action[AnyContent] = StackAction(AuthorityKey -> hasPermission(Administrator)) { implicit request =>
    val event = Event.find(id)

    val reason = Option(request.body.asFormUrlEncoded.get.get("reason").head.head).filter(_.trim.nonEmpty)
    Event.cancel(id, reason)
    LogEntry.create(event, "Sammankomsten inställd av " + loggedIn.name + ". Orsak: " + reason.getOrElse("ej angiven"))

    import play.api.Play.current
    import play.api.libs.concurrent.Execution.Implicits._
    import scala.concurrent.duration._
    Akka.system.scheduler.scheduleOnce(1.second) {
      val cancelledEvent = Event.find(id)
      MailReminder.sendCancellationMessage(cancelledEvent)
      SlackReminder.sendCancellationMessage(cancelledEvent)
    }

    Redirect(routes.Events.show(id))
  }


  def delete(id: Long): Action[AnyContent] = StackAction(AuthorityKey -> hasPermission(Administrator)) { implicit request =>
    val event = Event.find(id)
    val groupId = event.group.id.get
    Event.delete(id)
    Redirect(routes.Groups.show(groupId))
  }

  val eventForm: Form[Event] =
    Form(
      mapping(
        "id" -> ignored(None:Option[Long]),
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
        .verifying("Sluttid måste vara efter starttid", event => event.startTime.before(event.endTime))
        .verifying("Sista anmälningsdag måste vara före själva sammankomsten",
                   event => event.lastSignUpDate==event.startTime || event.lastSignUpDate.before(event.startTime))
    )

  def fromEvent(event: Event): Option[(Option[Long], String, String, Date, Date, Date, String, String, Long, Boolean, Option[Date], Option[Int])] = {
    val isSameDay = sameDay(event.startTime, event.lastSignUpDate)
    val lastSignUpDay = if (isSameDay) {
      None
    } else {
      Option(event.lastSignUpDate)
    }

    val invited = if(event.allowExtraFriends) {
      "allow_extra_friends"
    } else if(event.maxParticipants.isDefined) {
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
    start_date: util.Date,
    start_time: util.Date,
    end_time: util.Date,
    venue: String,
    invited: String,
    groupId: Long,
    sameDay: Boolean,
    last_signup_date: Option[util.Date],
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
      group = Group.find(groupId),
      maxParticipants = max_participants
    )
  }
}

