package services

import models.User
import org.specs2.mutable._

class GravatarUrlSpec extends Specification {

  def CORRECT_URL = "https://secure.gravatar.com/avatar/0bc83cb571cd1c50ba6f3e8a78ef1346.jpg?default=blank"
  "Gravatar util" should {

    "handle simple case" in {
      val user = User(firstName = "Hari", lastName = "Seldon", email = "myemailaddress@example.com", imageProvider = GravatarUrl.identifier)
      GravatarUrl.url(user) must beEqualTo(CORRECT_URL)
    }

    "handle CamelCase" in {
      val user = User(firstName = "Hari", lastName = "Seldon", email = "MyEmailAddress@example.com", imageProvider = GravatarUrl.identifier)
      GravatarUrl.url(user) must beEqualTo(CORRECT_URL)
    }

    "handle white space" in {
      val user = User(firstName = "Hari", lastName = "Seldon", email = "  MyEmailAddress@example.com ", imageProvider = GravatarUrl.identifier)
      GravatarUrl.url(user) must beEqualTo(CORRECT_URL)
    }

    "handle empty string" in {
      val user = User(firstName = "Hari", lastName = "Seldon", email = "", imageProvider = GravatarUrl.identifier)
      GravatarUrl.url(user).size must beGreaterThan(0)
    }
  }
}
