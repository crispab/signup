package se.crisp.signup4.integration.models

import java.util.Date
import javax.inject.Inject

import se.crisp.signup4.models._
import org.scalatestplus.play._
import se.crisp.signup4.util.TestHelper._

class EventCrudTest @Inject() (val membershipDAO: MembershipDAO,
                               val participationDAO: ParticipationDAO,
                               val userDAO: UserDAO) extends PlaySpec with OneAppPerSuite {

  "Event object " must {
    "be able to be read from DB" in {
      val events = Event.findAll()
      events.size must be > 0
    }

    "be able to find by ID" in {
      noException must be thrownBy Event.find(-2)
    }

    "throw exception for non-existing events" in {
      val thrown = the[RuntimeException] thrownBy Event.find(0)
      thrown.getMessage must equal("SqlMappingError(No rows when expecting a single one)")
    }

    "be able to persist new event" in {
      val group = Group.findAll().head
      val event = Event(group = group, name = withTestId("Julgransplundring"), startTime = morningStart, endTime = morningEnd, lastSignUpDate = morningStart)
      val eventId = Event.create(event)
      eventId must not be 0
      Event.delete(eventId)
    }


    "report seats available when nothing specified" in {
      val group = Group.findAll().head
      val event = Event(group = group, name = withTestId("Julgransplundring"), startTime = morningStart, endTime = morningEnd, lastSignUpDate = morningStart)
      val eventId = Event.create(event)
      val newEvent = Event.find(eventId)

      Event.hasSeatsAvailable(eventId) must be (true)

      userDAO.findUnregisteredMembers(newEvent) map { member =>
        val participation = new Participation(user = member, event = newEvent, signUpTime = Option(new Date()))
        participationDAO.create(participation)
      }

      Event.hasSeatsAvailable(eventId) must be (true)

      Event.delete(eventId)
    }

    "report seats available when fewer than max" in {
      val group = Group.findAll().head
      val noOfMembers = membershipDAO.findMembers(group).size

      noOfMembers must be > 1

      val event = Event(maxParticipants = Option(noOfMembers + 1), group = group, name = withTestId("Julgransplundring"), startTime = morningStart, endTime = morningEnd, lastSignUpDate = morningStart)
      val eventId = Event.create(event)
      val newEvent = Event.find(eventId)

      Event.hasSeatsAvailable(eventId) must be (true)

      userDAO.findUnregisteredMembers(newEvent) map { member =>
        val participation = new Participation(user = member, event = newEvent, signUpTime = Option(new Date()))
        participationDAO.create(participation)
      }

      Event.hasSeatsAvailable(eventId) must be (true)

      Event.delete(eventId)
    }

    "report NO seats available when more than max" in {
      val group = Group.findAll().head
      val noOfMembers = membershipDAO.findMembers(group).size

      noOfMembers must be > 1

      val event = Event(maxParticipants = Option(noOfMembers - 1), group = group, name = withTestId("Julgransplundring"), startTime = morningStart, endTime = morningEnd, lastSignUpDate = morningStart)
      val eventId = Event.create(event)
      val newEvent = Event.find(eventId)

      Event.hasSeatsAvailable(eventId) must be (true)

      userDAO.findUnregisteredMembers(newEvent) map { member =>
        val participation = new Participation(user = member, event = newEvent, signUpTime = Option(new Date()))
        participationDAO.create(participation)
      }


      Event.hasSeatsAvailable(eventId) must be (false)

      Event.delete(eventId)
    }

    "report NO seats available when more than max including guests" in {
      val group = Group.findAll().head
      val noOfMembers = membershipDAO.findMembers(group).size

      noOfMembers must be > 1

      val event = Event(maxParticipants = Option(noOfMembers), group = group, name = withTestId("Julgransplundring"), startTime = morningStart, endTime = morningEnd, lastSignUpDate = morningStart)
      val eventId = Event.create(event)
      val newEvent = Event.find(eventId)

      Event.hasSeatsAvailable(eventId) must be (true)

      userDAO.findUnregisteredMembers(newEvent) map { member =>
        val participation = new Participation(user = member, event = newEvent, signUpTime = Option(new Date()), numberOfParticipants = 2)
        participationDAO.create(participation)
      }


      Event.hasSeatsAvailable(eventId) must be (false)

      Event.delete(eventId)
    }

    "report NO seats available when equal to max" in {
      val group = Group.findAll().head
      val noOfMembers = membershipDAO.findMembers(group).size

      noOfMembers must be > 1

      val event = Event(maxParticipants = Option(noOfMembers), group = group, name = withTestId("Julgransplundring"), startTime = morningStart, endTime = morningEnd, lastSignUpDate = morningStart)
      val eventId = Event.create(event)
      val newEvent = Event.find(eventId)

      val members = userDAO.findUnregisteredMembers(newEvent)

      Event.hasSeatsAvailable(eventId) must be (true)

      members map { member =>
        val participation = new Participation(user = member, event = newEvent, signUpTime = Option(new Date()))
        participationDAO.create(participation)
      }

      Event.hasSeatsAvailable(eventId) must be (false)

      Event.delete(eventId)
    }

    "be possible to delete with log entries" in {
      val group = Group.findAll().head
      val event = Event(group = group, name = withTestId("Midsommardans"), startTime = morningStart, endTime = morningEnd, lastSignUpDate = morningStart)
      val eventId = Event.create(event)
      val newEvent = Event.find(eventId)

      LogEntry.create(event = newEvent, message = "Created!!")

      noException must be thrownBy Event.delete(eventId)
    }

    "be possible to delete with reminders" in {
      val group = Group.findAll().head
      val event = Event(group = group, name = withTestId("Midsommardans"), startTime = morningStart, endTime = morningEnd, lastSignUpDate = morningStart)
      val eventId = Event.create(event)

      Reminder.createRemindersForEvent(eventId, event)

      noException must be thrownBy Event.delete(eventId)
    }

  }
}
