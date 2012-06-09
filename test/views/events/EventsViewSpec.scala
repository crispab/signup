package views.events

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._
import models.Status

class EventsViewSpec extends Specification {

  "Event view object" should {

    "be able to map status" in {
      running(FakeApplication()) {
        val status = Status.Maybe
        val description =
         status match {
            case Status.On => "Kommer"
            case Status.Maybe => "Kanske"
            case Status.Off => "Kommer inte"
        }
        description must equalTo("Kanske")
      }
    }
  }

}
