package models

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._
import java.util

class EventModelSpec extends Specification {

  "Event model object" should {

    "be persistable" in {
      running(FakeApplication()) {
        val group = new Group(name = "Crisp RD", description = "Cool gang of hackers")
        Group.create(group)
        val crisp = Group.findAll().head
        val event = new Event(name = "Julafton", group = crisp, start_time = new util.Date(), end_time = new util.Date())
        Event.create(event)
        Event.findAll().exists(_.name == "Julafton") must beTrue
      }
    }

    "exist in database fixture" in {
      running(FakeApplication()) {
        val events = Event.findAll()
        events.size must greaterThan(0)
      }
    }
  }

}
