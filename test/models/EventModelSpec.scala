package models

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._

class EventModelSpec extends Specification {

  "Event model object" should {

    "be persistable" in {
      running(FakeApplication()) {
        val event = new Event(name = "Julafton")
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
