import java.util.TimeZone

import models.User
import org.apache.commons.lang.exception.ExceptionUtils
import play.api.libs.concurrent.Akka
import services.{CheckEvents, EventReminderActor}
import play.api._
import play.api.mvc._
import play.api.mvc.Results._
import scala.concurrent.Future


object Global extends GlobalSettings {

  override def onStart(app: play.api.Application) {
    Logger.debug("onStart called")

    setTimeZoneToAppDefault()
    startCheckingForRemindersToSend()
  }


  def startCheckingForRemindersToSend() {
    import play.api.Play.current
    import scala.concurrent.duration._
    import scala.concurrent.ExecutionContext.Implicits.global
    Akka.system.scheduler.schedule(firstRun, 24.hours, EventReminderActor.create(), CheckEvents(loggedIn = User.system))
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

  def setTimeZoneToAppDefault() {
    // not so pretty, but convenient since Heroku servers may run in another time zone
    TimeZone.setDefault(TimeZone.getTimeZone(util.LocaleHelper.TZ_NAME))
  }


  override def onError(request: RequestHeader, ex: Throwable): Future[Result] = {
    val cause = ExceptionUtils.getCause(ex)
    val stackTrace = ExceptionUtils.getStackTrace(cause)
    Future.successful(InternalServerError(
      views.html.error("Sidan du försökte gå till kan inte visas.", cause.getLocalizedMessage + "\n" + stackTrace)
    ))
  }

  override def onHandlerNotFound(request: RequestHeader): Future[Result] = {
    Future.successful(NotFound(
      views.html.error("Sidan du försökte gå till finns inte.", request.uri)
    ))
  }

  override def onBadRequest(request: RequestHeader, error: String): Future[Result] = {
    Future.successful(BadRequest(
      views.html.error("Sidan du försökte gå till kan inte visas.", error)
    ))
  }

}
