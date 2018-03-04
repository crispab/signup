package se.crisp.signup4.models.dao

import java.util.Date
import javax.inject.{Inject, Singleton}

import anorm.SqlParser.get
import anorm.{RowParser, SQL, ~}
import org.joda.time.LocalDate
import play.api.Configuration
import play.api.db.Database
import se.crisp.signup4.models.{Event, Reminder}
import se.crisp.signup4.util.DateHelper.sameDay

@Singleton
class ReminderDAO @Inject() (val database: Database,
                             val configuration: Configuration,
                             val eventDAO: EventDAO) {
  import scala.language.postfixOps
  val parser: RowParser[Reminder] = {
    get[Option[Long]]("id") ~
    get[Date]("datex") ~
    get[Long]("event") map {
      case id ~ datex ~ event =>
        Reminder(
          id = id,
          date = datex,
          event = eventDAO.find(event)
        )
    }
  }

  def create(reminder: Reminder): Long = {
    database.withTransaction {
      implicit connection =>
        SQL(insertQueryString).on(
          'datex -> reminder.date,
          'event -> reminder.event.id
        ).executeInsert()
    } match {
      case Some(primaryKey: Long) => primaryKey
      case _ => throw new RuntimeException("Could not insert into database, no PK returned")
    }
  }

  def firstReminderDays: Int = configuration.get[Int]("event.reminder.first.days")

  def lastReminderDays(event: Event): Int = {
    if(sameDay(event.startTime, event.lastSignUpDate))
      1
    else
      0
  }


  def createRemindersForEvent(eventId: Long, event: Event) {
    database.withTransaction {
      implicit connection =>

        SQL("DELETE FROM reminders r WHERE r.event={eventId}").on('eventId -> eventId).executeUpdate()

        val firstReminderDate = new LocalDate(event.lastSignUpDate).minusDays(firstReminderDays)
        SQL(insertQueryString).on(
          'datex -> firstReminderDate.toDate,
          'event -> eventId
        ).executeInsert()

        val lastReminderDate = new LocalDate(event.lastSignUpDate).minusDays(lastReminderDays(event))
        SQL(insertQueryString).on(
          'datex -> lastReminderDate.toDate,
          'event -> eventId
        ).executeInsert()
    }
  }

  val insertQueryString =
    """
INSERT INTO reminders (
      datex,
      event
    )
    VALUES (
      {datex},
      {event}
    )
    """

  def findByEvent(event: Event): List[Reminder] = {
    database.withTransaction {
      implicit connection =>
        SQL("SELECT * FROM reminders WHERE event={event} ORDER BY datex").on('event -> event.id).as(parser *)
    }
  }

  def findDueReminders(currentTime: Date): Seq[Reminder] = {
    database.withTransaction {
      implicit connection =>
        SQL(dueRemindersQueryString).on('currentTime -> currentTime).as(parser *)
    }
  }

  val dueRemindersQueryString =
  """
SELECT r.*
FROM reminders r
WHERE cast({currentTime} as date) >= r.datex
  """

  def delete(id: Long) {
    database.withTransaction {
      implicit connection => {
        SQL("DELETE FROM reminders r WHERE r.id={id}").on('id -> id).executeUpdate()
      }
    }
  }

}
