package util

import play.api.i18n.Messages


object LocaleHelper {
  import play.api.Play.current
  val LC_NAME: String = play.api.Play.configuration.getString("application.locale").getOrElse("sv_SE")
  val TZ_NAME: String = play.api.Play.configuration.getString("application.timezone").getOrElse("Europe/Stockholm")

  private def isKey(s: String) = s.startsWith("error.")

  def errMsg(keyOrMessage: String): String = {
    if(isKey(keyOrMessage))
      Messages(keyOrMessage)
    else
      keyOrMessage
  }
}
