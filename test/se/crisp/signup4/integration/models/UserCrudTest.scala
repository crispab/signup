package se.crisp.signup4.integration.models

import anorm.AnormException
import org.scalatestplus.play._
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import se.crisp.signup4.models.User
import se.crisp.signup4.models.dao.UserDAO
import se.crisp.signup4.util.TestHelper._

class UserCrudTest extends PlaySpec with GuiceOneAppPerSuite {

  val userDAO: UserDAO = app.injector.instanceOf[UserDAO]

  "User object " must {
    "be able to be read from DB" in {
      val users = userDAO.findAll()
      users.size must be > 0
    }

    "be able to find by ID" in {
      noException must be thrownBy userDAO.find(-2)
    }

    "throw exception for non-existing users" in {
      val thrown = the[AnormException] thrownBy userDAO.find(0)
      thrown.getMessage must equal("SqlMappingError(No rows when expecting a single one)")
    }

    "be able to persist new user" in {
      val user = User(
        firstName = withTestId("Rulle"),
        lastName = withTestId("Fnatt"),
        email = withTestId("rulle@mailinator.com")
      )

      val userId = userDAO.create(user)
      userId must not be 0
      userDAO.delete(userId)
    }

  }
}
