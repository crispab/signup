package util

import org.specs2.mutable._

class HtmlHelperSpec extends Specification {

  "HtmlHelper" should {

    "strip all HTML tags" in {
      HtmlHelper.stripFromHtml("<span>Hej hopp! </span><div class='fnutt'>Grön...</DIV>") must beEqualTo("Hej hopp! Grön...")
    }

    "replace </p> with double newline" in {
      HtmlHelper.stripFromHtml("<p>Hej hopp!</p><div class='fnutt'>Grön...</DIV>") must beEqualTo("Hej hopp!\n\nGrön...")
    }

    "replace <br> with newline" in {
      HtmlHelper.stripFromHtml("Hej hopp!<bR><div class='fnutt'>Grön...</DIV>") must beEqualTo("Hej hopp!\nGrön...")
    }

    "replace <li> with ' * ' and </li> with new line" in {
      HtmlHelper.stripFromHtml("<ul><li>Hej hopp!</li><li>Grön...</li></ul>") must beEqualTo(" * Hej hopp!\n * Grön...\n")
    }

    "remove newlines from HTML" in {
      HtmlHelper.stripFromHtml("<ul>\n\n<li>\nHej\n\n\nhopp!</li>\n<li>Grön...\n\n</li></ul>") must beEqualTo(" * Hej hopp!\n * Grön... \n")
    }

    "remove &nbsp; from HTML" in {
      HtmlHelper.stripFromHtml("<ul><li>Hej&nbsp;&nbsp;hopp!</li><li>Grön...</li></ul>") must beEqualTo(" * Hej hopp!\n * Grön...\n")
    }
  }
}
