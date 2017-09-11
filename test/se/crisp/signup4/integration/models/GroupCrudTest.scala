package se.crisp.signup4.integration.models

import se.crisp.signup4.models._
import org.scalatestplus.play._
import se.crisp.signup4.util.TestHelper._

class GroupCrudTest extends PlaySpec with OneAppPerSuite {

  "Group object " must {
    "be able to be read from DB" in {
      val groups = Group.findAll()
      groups.size must be > 0
    }

    "be able to find by ID" in {
      noException must be thrownBy Group.find(-1)
    }

    "throw exception for non-existing group" in {
      val thrown = the[RuntimeException] thrownBy Group.find(0)
      thrown.getMessage must equal("SqlMappingError(No rows when expecting a single one)")
    }

    "be able to persist new group" in {
      val group = Group(
        name = withTestId("Happy hackers"),
        description = "For people who loves to write code",
        mailFrom = withTestId("happyhackers@mailinator.com"),
        mailSubjectPrefix = "HaHa"
      )

      val groupId = Group.create(group)
      groupId must not be 0
      noException must be thrownBy Group.delete(groupId)
    }

    "be able to remove group with an event" in {
      val group = Group(
        name = withTestId("Happy people"),
        description = "For people who loves to be happy",
        mailFrom = withTestId("happypeople@mailinator.com"),
        mailSubjectPrefix = "HaPe"
      )

      val groupId = Group.create(group)
      groupId must not be 0
      val newGroup = Group.find(groupId)

      val event = Event(group = newGroup, name = withTestId("Midsommardans"), startTime = morningStart, endTime = morningEnd, lastSignUpDate = morningStart)
      val eventId = Event.create(event)
      val newEvent = Event.find(eventId)

      Reminder.createRemindersForEvent(eventId, event)
      LogEntry.create(event = newEvent, message = "Created!!")

      noException must be thrownBy Group.delete(groupId)
    }

  }
}
