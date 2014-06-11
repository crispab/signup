package models

import anorm.SqlParser._
import play.api.db.DB
import anorm._
import play.api.Play.current
import java.util.Date
import util.DateHelper._


case class Reminder(id: Pk[Long] = NotAssigned,
                         daysBefore: Long,
                         event: Event)

object Reminder {
  import scala.language.postfixOps
  val parser = {
    get[Pk[Long]]("id") ~
    get[Long]("days_before") ~
    get[Long]("event") map {
      case id ~ days_before ~ event =>
        Reminder(
          id = id,
          daysBefore = days_before,
          event = Event.find(event)
        )
    }
  }

  def create(reminder: Reminder): Long = {
    DB.withTransaction {
      implicit connection =>
        SQL(insertQueryString).on(
          'days_before -> reminder.daysBefore,
          'event -> reminder.event.id
        ).executeInsert()
    } match {
      case Some(primaryKey: Long) => primaryKey
      case _ => throw new RuntimeException("Could not insert into database, no PK returned")
    }
  }

  def createRemindersForEvent(eventId: Long, event: Event) {
    DB.withTransaction {
      implicit connection =>

        SQL("DELETE FROM reminders r WHERE r.event={eventId}").on('eventId -> eventId).executeUpdate()

        val daysForLastSignUp = daysBetween(event.startTime, event.lastSignUpDate)

        import play.api.Play.current
        val firstReminderDays = play.api.Play.configuration.getLong("event.reminder.first.days").getOrElse(7L) + daysForLastSignUp
        SQL(insertQueryString).on(
          'days_before -> firstReminderDays,
          'event -> eventId
        ).executeInsert()

        val secondReminderDays = play.api.Play.configuration.getLong("event.reminder.second.days").getOrElse(1L) + daysForLastSignUp
        SQL(insertQueryString).on(
          'days_before -> secondReminderDays,
          'event -> eventId
        ).executeInsert()
    }
  }

  val insertQueryString =
    """
INSERT INTO reminders (
      days_before,
      event
    )
    VALUES (
      {days_before},
      {event}
    )
    """

  def findByEvent(event: Event) = {
    DB.withTransaction {
      implicit connection =>
        SQL("SELECT * FROM reminders WHERE event={event} ORDER BY days_before DESC").on('event -> event.id).as(parser *)
    }
  }

  def findDueReminders(currentTime: Date): Seq[Reminder] = {
    DB.withTransaction {
      implicit connection =>
        SQL(dueRemindersQueryString).on('currentTime -> currentTime).as(parser *)
    }
  }

  val dueRemindersQueryString =
  """
SELECT r.*
FROM events e, reminders r
WHERE r.event = e.id
  AND e.start_time > {currentTime}
  AND cast({currentTime} as date) >= cast(e.start_time as date) - r.days_before
  """

  def delete(id: Long) {
    DB.withTransaction {
      implicit connection => {
        SQL("DELETE FROM reminders r WHERE r.id={id}").on('id -> id).executeUpdate()
      }
    }
  }

}
