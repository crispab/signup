package controllers

import org.specs2.mutable._
import models.security.{NormalUser, Permission, Administrator}
import play.api.test.Helpers._
import play.api.test.FakeApplication
import models.User

class UsersSpec extends Specification {

  "userUpdateForm" should {

    "be able to bind to Administrator User object" in {
      running(FakeApplication()) {
        val testData: Map[String, String] = Map(
          "id" -> "77",
          "firstName" -> "Jan",
          "lastName" -> "Grape",
          "email" -> "jan.grape@crisp.se",
          "phone" -> "070-6018709",
          "comment" -> "A programmer dude",
          "administrator" -> "true",
          "password" -> "123pass456"
        )
        val user: User = Users.userUpdateForm.bind(testData).get
        user.firstName must equalTo("Jan")
        user.permission must equalTo(Administrator)
      }
    }

    "be able to bind to NormalUser User object" in {
      running(FakeApplication()) {
        val testData: Map[String, String] = Map(
          "id" -> "78",
          "firstName" -> "Mats",
          "lastName" -> "Strandberg",
          "email" -> "mats.strandberg@crisp.se",
          "phone" -> "070-787878",
          "comment" -> "A programmer dude",
          "administrator" -> "false",
          "password" -> ""
        )
        val user: User = Users.userUpdateForm.bind(testData).get
        user.firstName must equalTo("Mats")
        user.permission must equalTo(NormalUser)
      }
    }
  }

}
