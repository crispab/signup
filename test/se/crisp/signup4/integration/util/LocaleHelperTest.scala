package se.crisp.signup4.integration.util

import java.util.Locale
import javax.inject.Inject

import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import se.crisp.signup4.util.LocaleHelper

class LocaleHelperTest @Inject() (localeHelper: LocaleHelper) extends PlaySpec with OneAppPerSuite {

  "LocaleHelper" must {

    "give Locale name" in {
      localeHelper.LC_NAME must equal ("sv_SE")
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
