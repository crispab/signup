package integration.models

import models.User
import org.scalatestplus.play._
import util.TestHelper._

class UserCrudTest extends PlaySpec with OneAppPerSuite {

  "User object " must {
    "be able to be read from DB" in {
      val users = User.findAll()
      users.size must be > 0
    }

    "be able to find by ID" in {
      noException must be thrownBy User.find(-2)
    }

    "throw exception for non-existing users" in {
      val thrown = the[RuntimeException] thrownBy User.find(0)
      thrown.getMessage must equal("SqlMappingError(No rows when expecting a single one)")
    }

    "be able to persist new user" in {
      val user = User(
        firstName = withTestId("Rulle"),
        lastName = withTestId("Fnatt"),
        email = withTestId("rulle@mailinator.com")
      )

      val userId = User.create(user)
      userId must not be 0
      User.delete(userId)
    }

  }
}
