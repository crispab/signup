package se.crisp.signup4.unit.util

import javax.inject.Inject

import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.Configuration
import se.crisp.signup4.util.HtmlHelper

class HtmlHelperTest extends PlaySpec with MockitoSugar {

  val htmlHelper = new HtmlHelper(mock[Configuration])

  "HtmlHelper" must {

    "strip all HTML tags" in {
      htmlHelper.stripFromHtml("<span>Hej hopp! </span><div class='fnutt'>Grön...</DIV>") must equal ("Hej hopp! Grön...")
    }

    "replace </p> with double newline" in {
      htmlHelper.stripFromHtml("<p>Hej hopp!</p><div class='fnutt'>Grön...</DIV>") must equal ("Hej hopp!\n\nGrön...")
    }

    "replace <br> with newline" in {
      htmlHelper.stripFromHtml("Hej hopp!<bR><div class='fnutt'>Grön...</DIV>") must equal ("Hej hopp!\nGrön...")
    }

    "replace <li> with ' * ' and </li> with new line" in {
      htmlHelper.stripFromHtml("<ul><li>Hej hopp!</li><li>Grön...</li></ul>") must equal (" * Hej hopp!\n * Grön...\n")
    }

    "remove newlines from HTML" in {
      htmlHelper.stripFromHtml("<ul>\n\n<li>\nHej\n\n\nhopp!</li>\n<li>Grön...\n\n</li></ul>") must equal (" * Hej hopp!\n * Grön... \n")
    }

    "remove &nbsp; from HTML" in {
      htmlHelper.stripFromHtml("<ul><li>Hej&nbsp;&nbsp;hopp!</li><li>Grön...</li></ul>") must equal (" * Hej hopp!\n * Grön...\n")
    }
  }
}
