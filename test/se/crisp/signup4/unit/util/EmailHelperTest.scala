package se.crisp.signup4.unit.util

import org.scalatestplus.play._
import se.crisp.signup4.util.EmailHelper

class EmailHelperTest extends PlaySpec {

  private val name = "kalle.anka"

  private val domain = "ankeborg.se"

  private val email = name + "@" + domain

  "EmailHelper" must {

    "abbreviate when not logged" in {
      EmailHelper.abbreviated(email) must equal("at " + domain)
    }

    "full address when logged" in {
      EmailHelper.abbreviated(email, isLoggedIn = true) must equal(email)
    }

    "manage empty string" in {
      EmailHelper.abbreviated("") must equal("")
    }

    "manage invalid email address" in {
      EmailHelper.abbreviated(name) must equal("")
    }

  }
}

