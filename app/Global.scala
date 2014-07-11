import java.util.TimeZone

import akka.actor.Props
import org.apache.commons.lang.exception.ExceptionUtils
import play.api.libs.concurrent.Akka
import services.EventReminderActor
import play.api._
import play.api.mvc._
import play.api.mvc.Results._
import scala.concurrent.Future


object Global extends GlobalSettings {

  override def onStart(app: play.api.Application) {
    Logger.debug("onStart called")

    setTimeZoneToCET()
    startCheckingForRemindersToSend()
  }


  def startCheckingForRemindersToSend() {

    import play.api.Play.current

    import scala.concurrent.ExecutionContext.Implicits._
    val eventReminderActor = Akka.system.actorOf(Props[EventReminderActor], name = "EventReminder")

    import scala.concurrent.duration._
    Akka.system.scheduler.schedule(firstRun, 24.hours, eventReminderActor, EventReminderActor.CHECK_EVENTS)
  }

  private def firstRun = {
    import play.api.Play.current
    val sendTimeProperty = play.api.Play.configuration.getString("event.reminder.send.time").getOrElse("01:00")

    var startTime = new org.joda.time.LocalTime(sendTimeProperty).toDateTimeToday
    if(startTime.isBeforeNow) {
      startTime = startTime.plusHours(24)
    }
    Logger.debug("First check for reminders to send happens at: " + startTime)

    val untilFirstRun = new org.joda.time.Duration(org.joda.time.DateTime.now(), startTime)
    scala.concurrent.duration.FiniteDuration(untilFirstRun.getStandardSeconds, java.util.concurrent.TimeUnit.SECONDS)
  }

  def setTimeZoneToCET() {
    // not so pretty, but convenient since Heroku servers run in another time zone
    TimeZone.setDefault(TimeZone.getTimeZone("Europe/Stockholm"))
  }


  override def onError(request: RequestHeader, ex: Throwable) = {
    val cause = ExceptionUtils.getCause(ex)
    val stackTrace = ExceptionUtils.getStackTrace(cause)
    Future.successful(InternalServerError(
      views.html.error("Sidan du försökte gå till kan inte visas.", cause.getLocalizedMessage + "\n" + stackTrace)
    ))
  }

  override def onHandlerNotFound(request: RequestHeader) = {
    Future.successful(NotFound(
      views.html.error("Sidan du försökte gå till finns inte.", request.uri)
    ))
  }

  override def onBadRequest(request: RequestHeader, error: String) = {
    Future.successful(BadRequest(
      views.html.error("Sidan du försökte gå till kan inte visas.", error)
    ))
  }

}
