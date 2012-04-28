package models

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._

class ParticipationModelSpec extends Specification {

  "Participation model object" should {

    "be persistable" in {
      running(FakeApplication()) {
        val user = User.findAll().filter(_.firstName == "Göran").head
        val event = Event.findAll().filter(_.name == "Crisp RD").head

        val participation = Participation(status = Status.On, user = user, event = event)
        Participation.create(participation)

        true must beTrue

      }
    }
    
    "exist in database fixture" in {
      running(FakeApplication()) {
        val participations = Participation.findAll()
        participations.size must greaterThan(0)
      }
    }

    "be able to create a Status from a String" in {
      val status = Status.withName("On")
      status must beEqualTo(Status.On)
    }
  }

}
