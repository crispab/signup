import java.util.{Locale, TimeZone}

import org.apache.commons.lang.exception.ExceptionUtils
import play.api._
import play.api.i18n.Messages
import play.api.libs.concurrent.Akka
import play.api.mvc.Results._
import play.api.mvc._
import se.crisp.signup4.models.User
import se.crisp.signup4.services.{CheckEvents, EventReminderActor}
import se.crisp.signup4.util.{LocaleHelper, ThemeHelper}

import scala.concurrent.Future


object Global extends GlobalSettings {

  override def onStart(app: play.api.Application) {
    Logger.debug("onStart called")

    setTimeZoneAndLocaleToAppDefault()

    Logger.info("Application name is " + ThemeHelper.APPLICATION_NAME)
    Logger.info("Application name is " + ThemeHelper.APPLICATION_NAME2)

    startCheckingForRemindersToSend()
  }


  def startCheckingForRemindersToSend() {
    import play.api.Play.current

    import scala.concurrent.ExecutionContext.Implicits.global
    import scala.concurrent.duration._
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

  def setTimeZoneAndLocaleToAppDefault() {
    // not so pretty, but convenient since Heroku servers may run in another time zone and locale
    TimeZone.setDefault(se.crisp.signup4.util.LocaleHelper.getConfiguredTimeZone)
    Locale.setDefault(se.crisp.signup4.util.LocaleHelper.getConfiguredLocale)

    Logger.info("TimeZone = " + TimeZone.getDefault)
    Logger.info("Locale = " + Locale.getDefault)
  }


  override def onError(request: RequestHeader, ex: Throwable): Future[Result] = {
    val cause = ExceptionUtils.getCause(ex)
    val stackTrace = ExceptionUtils.getStackTrace(cause)
    val lang = LocaleHelper.getLang(request)
    Future.successful(InternalServerError(
      se.crisp.signup4.views.html.error(Messages("http.error")(lang = lang), cause.getLocalizedMessage + "\n" + stackTrace)(lang = lang)
    ))
  }

  override def onHandlerNotFound(request: RequestHeader): Future[Result] = {
    val lang = LocaleHelper.getLang(request)
    Future.successful(NotFound(
      se.crisp.signup4.views.html.error(Messages("http.notfound")(lang = lang), request.uri)(lang = lang)
    ))
  }

  override def onBadRequest(request: RequestHeader, error: String): Future[Result] = {
    val lang = LocaleHelper.getLang(request)
    Future.successful(BadRequest(
      se.crisp.signup4.views.html.error(Messages("http.badrequest")(lang = lang), error)(lang = lang)
    ))
  }

}
