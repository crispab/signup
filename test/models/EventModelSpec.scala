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
        val event = new Event(name = "Julafton", group = crisp, startTime = new util.Date(), endTime = new util.Date())
        Event.create(event) must beGreaterThanOrEqualTo(1L)
        Event.findAll().exists(_.name == "Julafton") must beTrue
      }
    }

    "be deletable" in {
      running(FakeApplication()) {
        val group = new Group(name = "Crisp RD", description = "Cool gang of hackers")
        Group.create(group)
        val crisp = Group.findAll().head
        val event = new Event(name = "Julafton", group = crisp, startTime = new util.Date(), endTime = new util.Date())
        val id = Event.create(event)
        id must beGreaterThanOrEqualTo(1L)
        Event.findAll().exists(_.name == "Julafton") must beTrue

        val user = User.findAll().filter(_.firstName == "Göran").head
        val participation = Participation(status = Status.On, user = user, event = event)
        Participation.create(participation) must beGreaterThanOrEqualTo(1L)

        LogEntry.create(event, "A log message!")

        Event.delete(id)
        Event.findAll().exists(_.name == "Julafton") must beFalse
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
