package se.crisp.signup4.integration.models

import anorm.AnormException
import org.scalatestplus.play._
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import se.crisp.signup4.models._
import se.crisp.signup4.models.dao.{EventDAO, GroupDAO, LogEntryDAO, ReminderDAO}
import se.crisp.signup4.util.TestHelper._

class GroupCrudTest extends PlaySpec with GuiceOneAppPerSuite {

  val groupDAO: GroupDAO = app.injector.instanceOf[GroupDAO]
  val eventDAO: EventDAO = app.injector.instanceOf[EventDAO]
  val logEntryDAO: LogEntryDAO = app.injector.instanceOf[LogEntryDAO]
  val reminderDAO: ReminderDAO = app.injector.instanceOf[ReminderDAO]

  "Group object " must {
    "be able to be read from DB" in {
      val groups = groupDAO.findAll()
      groups.size must be > 0
    }

    "be able to find by ID" in {
      noException must be thrownBy groupDAO.find(-1)
    }

    "throw exception for non-existing group" in {
      val thrown = the[AnormException] thrownBy groupDAO.find(0)
      thrown.getMessage must equal("SqlMappingError(No rows when expecting a single one)")
    }

    "be able to persist new group" in {
      val group = Group(
        name = withTestId("Happy hackers"),
        description = "For people who loves to write code",
        mailFrom = withTestId("happyhackers@mailinator.com"),
        mailSubjectPrefix = "HaHa"
      )

      val groupId = groupDAO.create(group)
      groupId must not be 0
      noException must be thrownBy groupDAO.delete(groupId)
    }

    "be able to remove group with an event" in {
      val group = Group(
        name = withTestId("Happy people"),
        description = "For people who loves to be happy",
        mailFrom = withTestId("happypeople@mailinator.com"),
        mailSubjectPrefix = "HaPe"
      )

      val groupId = groupDAO.create(group)
      groupId must not be 0
      val newGroup = groupDAO.find(groupId)

      val event = Event(group = newGroup, name = withTestId("Midsommardans"), startTime = morningStart, endTime = morningEnd, lastSignUpDate = morningStart)
      val eventId = eventDAO.create(event)
      val newEvent = eventDAO.find(eventId)

      reminderDAO.createRemindersForEvent(eventId, event)
      logEntryDAO.create(event = newEvent, message = "Created!!")

      noException must be thrownBy groupDAO.delete(groupId)
    }

  }
}
