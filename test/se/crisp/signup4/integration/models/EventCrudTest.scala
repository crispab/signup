package se.crisp.signup4.integration.models

import java.util.Date
import javax.inject.Inject

import se.crisp.signup4.models._
import org.scalatestplus.play._
import se.crisp.signup4.models.dao._
import se.crisp.signup4.util.TestHelper._

class EventCrudTest @Inject() (val membershipDAO: MembershipDAO,
                               val participationDAO: ParticipationDAO,
                               val userDAO: UserDAO,
                               val eventDAO: EventDAO,
                               val logEntryDAO: LogEntryDAO,
                               val reminderDAO: ReminderDAO) extends PlaySpec with OneAppPerSuite {

  "Event object " must {
    "be able to be read from DB" in {
      val events = eventDAO.findAll()
      events.size must be > 0
    }

    "be able to find by ID" in {
      noException must be thrownBy eventDAO.find(-2)
    }

    "throw exception for non-existing events" in {
      val thrown = the[RuntimeException] thrownBy eventDAO.find(0)
      thrown.getMessage must equal("SqlMappingError(No rows when expecting a single one)")
    }

    "be able to persist new event" in {
      val group = Group.findAll().head
      val event = Event(group = group, name = withTestId("Julgransplundring"), startTime = morningStart, endTime = morningEnd, lastSignUpDate = morningStart)
      val eventId = eventDAO.create(event)
      eventId must not be 0
      eventDAO.delete(eventId)
    }


    "report seats available when nothing specified" in {
      val group = Group.findAll().head
      val event = Event(group = group, name = withTestId("Julgransplundring"), startTime = morningStart, endTime = morningEnd, lastSignUpDate = morningStart)
      val eventId = eventDAO.create(event)
      val newEvent = eventDAO.find(eventId)

      eventDAO.hasSeatsAvailable(eventId) must be (true)

      userDAO.findUnregisteredMembers(newEvent) map { member =>
        val participation = Participation(user = member, event = newEvent, signUpTime = Option(new Date()))
        participationDAO.create(participation)
      }

      eventDAO.hasSeatsAvailable(eventId) must be (true)

      eventDAO.delete(eventId)
    }

    "report seats available when fewer than max" in {
      val group = Group.findAll().head
      val noOfMembers = membershipDAO.findMembers(group).size

      noOfMembers must be > 1

      val event = Event(maxParticipants = Option(noOfMembers + 1), group = group, name = withTestId("Julgransplundring"), startTime = morningStart, endTime = morningEnd, lastSignUpDate = morningStart)
      val eventId = eventDAO.create(event)
      val newEvent = eventDAO.find(eventId)

      eventDAO.hasSeatsAvailable(eventId) must be (true)

      userDAO.findUnregisteredMembers(newEvent) map { member =>
        val participation = Participation(user = member, event = newEvent, signUpTime = Option(new Date()))
        participationDAO.create(participation)
      }

      eventDAO.hasSeatsAvailable(eventId) must be (true)

      eventDAO.delete(eventId)
    }

    "report NO seats available when more than max" in {
      val group = Group.findAll().head
      val noOfMembers = membershipDAO.findMembers(group).size

      noOfMembers must be > 1

      val event = Event(maxParticipants = Option(noOfMembers - 1), group = group, name = withTestId("Julgransplundring"), startTime = morningStart, endTime = morningEnd, lastSignUpDate = morningStart)
      val eventId = eventDAO.create(event)
      val newEvent = eventDAO.find(eventId)

      eventDAO.hasSeatsAvailable(eventId) must be (true)

      userDAO.findUnregisteredMembers(newEvent) map { member =>
        val participation = Participation(user = member, event = newEvent, signUpTime = Option(new Date()))
        participationDAO.create(participation)
      }


      eventDAO.hasSeatsAvailable(eventId) must be (false)

      eventDAO.delete(eventId)
    }

    "report NO seats available when more than max including guests" in {
      val group = Group.findAll().head
      val noOfMembers = membershipDAO.findMembers(group).size

      noOfMembers must be > 1

      val event = Event(maxParticipants = Option(noOfMembers), group = group, name = withTestId("Julgransplundring"), startTime = morningStart, endTime = morningEnd, lastSignUpDate = morningStart)
      val eventId = eventDAO.create(event)
      val newEvent = eventDAO.find(eventId)

      eventDAO.hasSeatsAvailable(eventId) must be (true)

      userDAO.findUnregisteredMembers(newEvent) map { member =>
        val participation = Participation(user = member, event = newEvent, signUpTime = Option(new Date()), numberOfParticipants = 2)
        participationDAO.create(participation)
      }


      eventDAO.hasSeatsAvailable(eventId) must be (false)

      eventDAO.delete(eventId)
    }

    "report NO seats available when equal to max" in {
      val group = Group.findAll().head
      val noOfMembers = membershipDAO.findMembers(group).size

      noOfMembers must be > 1

      val event = Event(maxParticipants = Option(noOfMembers), group = group, name = withTestId("Julgransplundring"), startTime = morningStart, endTime = morningEnd, lastSignUpDate = morningStart)
      val eventId = eventDAO.create(event)
      val newEvent = eventDAO.find(eventId)

      val members = userDAO.findUnregisteredMembers(newEvent)

      eventDAO.hasSeatsAvailable(eventId) must be (true)

      members map { member =>
        val participation = Participation(user = member, event = newEvent, signUpTime = Option(new Date()))
        participationDAO.create(participation)
      }

      eventDAO.hasSeatsAvailable(eventId) must be (false)

      eventDAO.delete(eventId)
    }

    "be possible to delete with log entries" in {
      val group = Group.findAll().head
      val event = Event(group = group, name = withTestId("Midsommardans"), startTime = morningStart, endTime = morningEnd, lastSignUpDate = morningStart)
      val eventId = eventDAO.create(event)
      val newEvent = eventDAO.find(eventId)

      logEntryDAO.create(event = newEvent, message = "Created!!")

      noException must be thrownBy eventDAO.delete(eventId)
    }

    "be possible to delete with reminders" in {
      val group = Group.findAll().head
      val event = Event(group = group, name = withTestId("Midsommardans"), startTime = morningStart, endTime = morningEnd, lastSignUpDate = morningStart)
      val eventId = eventDAO.create(event)

      reminderDAO.createRemindersForEvent(eventId, event)

      noException must be thrownBy eventDAO.delete(eventId)
    }

  }
}
