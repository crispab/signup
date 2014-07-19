package util


object LocaleHelper {
  import play.api.Play.current
  val LC_NAME = play.api.Play.configuration.getString("application.locale").getOrElse("sv_SE")
  val TZ_NAME = play.api.Play.configuration.getString("application.timezone").getOrElse("Europe/Stockholm")
}
