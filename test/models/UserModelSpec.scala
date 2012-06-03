package models

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._
import anorm.NotAssigned

class UserModelSpec extends Specification {

  "User model object" should {

    "be persistable" in {
      running(FakeApplication()) {
        val user = new User(NotAssigned, "Jan", "Grape", "jg@superb.se")
        User.create(user)
        User.findAll().exists(_.firstName == "Jan") must beTrue
      }
    }

    "exist in database fixture" in {
      running(FakeApplication()) {
        val users = User.findAll()
        users.size must greaterThan(0)
      }
    }
  }

}
