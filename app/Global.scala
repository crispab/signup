import play.api.GlobalSettings
import play.api.Logger

object Global extends GlobalSettings {

  override def onStart(app: play.api.Application) {
    Logger.debug("        *-*-*-* onStart called *-*-*-*")
    // todo: schedule EventNotifierActors for all future events
  }

}
