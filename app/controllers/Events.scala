package controllers

import java.text.SimpleDateFormat

import anorm.{NotAssigned, Pk}
import models._
import org.apache.poi.xssf.usermodel.{XSSFSheet, XSSFColor, XSSFWorkbook}
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.iteratee.Enumerator
import play.api.mvc._
import util.AuthHelper._
import util.DateHelper._
import util.StatusHelper._
import java.util
import jp.t2v.lab.play2.auth.{AuthElement, OptionalAuthElement}
import models.security.Administrator
import play.api.libs.concurrent.Akka
import services.EventReminder


import scala.concurrent.ExecutionContext

object Events extends Controller with OptionalAuthElement with AuthConfigImpl {

  def show(id: Long) = StackAction { implicit request =>
    val event = Event.find(id)
    Ok(views.html.events.show(event, Participation.findMembers(event), Participation.findGuests(event), LogEntry.findByEvent(event), Reminder.findByEvent(event)))
  }

  def asExcel(id: Long) = StackAction { implicit request =>
    val event = Event.find(id)

    val workbook = new XSSFWorkbook()
    val sheet = workbook.createSheet("Anmälningar")
    createHeading(workbook, sheet)
    populateWithInvitedStatus(sheet, allInvited(event))
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
    heading.createCell(5).setCellValue("Kommentar")

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
    heading
  }

  private def allInvited(event: Event) = {
    val memberParticipations = Participation.findMembers(event)
    val guestParticipations = Participation.findGuests(event)
    (memberParticipations.on
      union guestParticipations.on
      union memberParticipations.maybe
      union guestParticipations.maybe
      union memberParticipations.off
      union guestParticipations.off
      union memberParticipations.unregistered
      union guestParticipations.unregistered)
  }

  private def populateWithInvitedStatus(sheet: XSSFSheet, allInvited: Seq[Participation]) {
    var rowNumber = 1
    for (participation <- allInvited) {
      val row = sheet.createRow(rowNumber)
      row.createCell(0).setCellValue(participation.user.firstName)
      row.createCell(1).setCellValue(participation.user.lastName)
      row.createCell(2).setCellValue(participation.user.email)
      row.createCell(3).setCellValue(asMessage(participation.status))
      row.createCell(4).setCellValue(participation.participantsComing)
      row.createCell(5).setCellValue(participation.comment)
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

