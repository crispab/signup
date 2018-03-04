package se.crisp.signup4.util

import java.util.{Locale, TimeZone}
import javax.inject.{Inject, Singleton}

import org.apache.commons.lang3.LocaleUtils
import play.api.Configuration
import play.api.i18n.{Lang, Langs, Messages}
import play.api.mvc.RequestHeader

@Singleton
class LocaleHelper @Inject() (val configuration: Configuration,
                              val langs: Langs) {
  val LC_NAME: String = configuration.get[String]("application.locale")
  val TZ_NAME: String = configuration.get[String]("application.timezone")

  private def isKey(s: String) = s.startsWith("error.")

  def errMsg(keyOrMessage: String)(implicit messages: Messages): String = {
    if(isKey(keyOrMessage))
      Messages(keyOrMessage)
    else
      keyOrMessage
  }

  def getConfiguredLocale: Locale = LocaleUtils.toLocale(LC_NAME)
  def getConfiguredTimeZone: TimeZone = TimeZone.getTimeZone(TZ_NAME)
  def getLang(request: RequestHeader): Lang = langs.preferred(request.acceptLanguages)
}
