package se.crisp.signup4.integration.util

import java.util.Locale

import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import se.crisp.signup4.util.LocaleHelper

class LocaleHelperTest extends PlaySpec with GuiceOneAppPerSuite {

  val localeHelper: LocaleHelper = app.injector.instanceOf[LocaleHelper]

  def TEST_LOCALE = "en_US"

  override def fakeApplication(): Application = new GuiceApplicationBuilder().configure(Map("application.locale" -> TEST_LOCALE)).build()

  "LocaleHelper" must {

    "give Locale name" in {
      localeHelper.LC_NAME must equal (TEST_LOCALE)
    }

    "give TimeZone name" in {
      localeHelper.TZ_NAME must equal ("Europe/Stockholm")
    }

    "be able to set default locale" in {
      Locale.setDefault(localeHelper.getConfiguredLocale)
      Locale.getDefault.toLanguageTag.replace('-','_') must equal (localeHelper.LC_NAME)
    }

  }
}
