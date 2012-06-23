package models

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._

class GroupModelSpec extends Specification {

  "Group model object" should {

    "be persistable" in {
      running(FakeApplication()) {
        val group = new Group(name = "Crisp RD", description = "Cool gang of hackers")
        Group.create(group)
        Group.findAll().exists(_.name == "Crisp RD") must beTrue
      }
    }
  }

}
