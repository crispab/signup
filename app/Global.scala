import java.util.TimeZone
import play.api.GlobalSettings
import play.api.Logger

object Global extends GlobalSettings {

  override def onStart(app: play.api.Application) {
    Logger.debug("onStart called")

    // todo: add TZ to groups
    // not so pretty, but convenient right now
    TimeZone.setDefault(TimeZone.getTimeZone("Europe/Stockholm"))

    // todo: schedule EventNotifierActors for all future events
  }

}
