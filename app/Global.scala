import akka.actor.Props
import java.util.TimeZone
import play.api.GlobalSettings
import play.api.libs.concurrent.Akka
import play.api.Logger
import scala.concurrent.duration.{FiniteDuration, Duration}
import scala.concurrent.ExecutionContext
import services.EventReminderActor

object Global extends GlobalSettings {

  override def onStart(app: play.api.Application) {
    Logger.debug("onStart called")

    setTimeZoneToCET
    startCheckingForRemindersToSend
  }


  def startCheckingForRemindersToSend() {

    import play.api.Play.current
    import ExecutionContext.Implicits._
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
}
