package util

import org.specs2.mutable._

class HtmlHelperSpec extends Specification {

  "HtmlHelper" should {

    "strip all HTML tags" in {
      HtmlHelper.stripFromHtml("<span>Hej hopp! </span><div class='fnutt'>Grön...</DIV>") must beEqualTo("Hej hopp! Grön...")
    }

    "replace </p> with new line" in {
      HtmlHelper.stripFromHtml("<p>Hej hopp!</p><div class='fnutt'>Grön...</DIV>") must beEqualTo("Hej hopp!\nGrön...")
    }

    "replace <br> with new line" in {
      HtmlHelper.stripFromHtml("Hej hopp!<bR><div class='fnutt'>Grön...</DIV>") must beEqualTo("Hej hopp!\nGrön...")
    }

    "replace <li> with ' * ' and </li> with new line" in {
      HtmlHelper.stripFromHtml("<ul><li>Hej hopp!</li><li>Grön...</li></ul>") must beEqualTo(" * Hej hopp!\n * Grön...\n")
    }
  }
}
