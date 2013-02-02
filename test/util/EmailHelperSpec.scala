package util

import org.specs2.mutable._

class EmailHelperSpec extends Specification {

  def name = "kalle.anka"
  def domain = "ankeborg.se"
  def email = name + "@" + domain

  "EmailHelper" should {

    "abbreviate when not logged" in {
      EmailHelper.abbreviated(email, isLoggedIn = false) must beEqualTo("@" + domain)
    }

    "full address when logged" in {
      EmailHelper.abbreviated(email, isLoggedIn = true) must beEqualTo(email)
    }

    "manage empty string" in {
      EmailHelper.abbreviated("") must beEqualTo("")
    }

    "manage invalid email address" in {
      EmailHelper.abbreviated(name) must beEqualTo("")
    }

  }

}
