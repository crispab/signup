package se.crisp.signup4.util

import java.util.{Locale, TimeZone}

import org.apache.commons.lang3.LocaleUtils
import play.api.Play.configuration
import play.api.i18n.{Lang, Messages}
import play.api.mvc.RequestHeader


object LocaleHelper {
  import play.api.Play.current
  val LC_NAME: String = configuration.getString("application.locale").getOrElse("sv_SE")
  val TZ_NAME: String = configuration.getString("application.timezone").getOrElse("Europe/Stockholm")

  private def isKey(s: String) = s.startsWith("error.")

  def errMsg(keyOrMessage: String)(implicit messages: Messages): String = {
    if(isKey(keyOrMessage))
      Messages(keyOrMessage)
    else
      keyOrMessage
  }

  def getConfiguredLocale: Locale = LocaleUtils.toLocale(LocaleHelper.LC_NAME)
  def getConfiguredTimeZone: TimeZone = TimeZone.getTimeZone(se.crisp.signup4.util.LocaleHelper.TZ_NAME)
  def getLang(request: RequestHeader): Lang = Lang.preferred(request.acceptLanguages)
}
