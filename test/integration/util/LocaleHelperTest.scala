package integration.util

import java.util.Locale

import org.apache.commons.lang.LocaleUtils
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import util.LocaleHelper

class LocaleHelperTest extends PlaySpec with OneAppPerSuite {

  "LocaleHelper" must {

    "give Locale name" in {
      LocaleHelper.LC_NAME must equal ("sv_SE")
    }

    "give TimeZone name" in {
      LocaleHelper.TZ_NAME must equal ("Europe/Stockholm")
    }

    "be able to set default locale" in {
      Locale.setDefault(LocaleHelper.getConfiguredLocale)
      Locale.getDefault.toLanguageTag.replace('-','_') must equal (LocaleHelper.LC_NAME)
    }

  }
}
