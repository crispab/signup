package util

import org.specs2.mutable._

class UrlHelperUtilSpec extends Specification {

  def CORRECT_PATH_ELEMENT = "crisp_ab-2aao"

  "UrlHelper" should {

    "not modify a good path element" in {
      UrlHelper.toPathElement(CORRECT_PATH_ELEMENT) must beEqualTo(CORRECT_PATH_ELEMENT)
    }

    "convert to lower case" in {
      UrlHelper.toPathElement(CORRECT_PATH_ELEMENT.toUpperCase) must beEqualTo(CORRECT_PATH_ELEMENT)
    }

    "remove white space" in {
      UrlHelper.toPathElement(" C r I   sp_ A b  - 2 ÅäÖ") must beEqualTo(CORRECT_PATH_ELEMENT)
    }

    "remove non alpha numeric characters" in {
      UrlHelper.toPathElement("C%r;i(sp)_A#B!!-!...2åÄö") must beEqualTo(CORRECT_PATH_ELEMENT)
    }
  }
}
