package se.crisp.signup4.models

import java.util.Date

import anorm.SqlParser._
import anorm._
import play.api.Play.current
import play.api.db.DB


case class LogEntry(
  id: Option[Long] = None,
  event: Event,
  message: String,
  when: Date = new Date()
)

object LogEntry {
  import scala.language.postfixOps
  val parser: RowParser[LogEntry] = {
    get[Option[Long]]("id") ~
    get[Long]("event") ~
    get[String]("message") ~
    get[Date]("whenx") map {
      case id ~ event ~ message ~ whenx =>
        LogEntry(
          id = id,
          event = Event.find(event),
          message = message,
          when = whenx
        )
    }
  }

  def findByEvent(event: Event): Seq[LogEntry] = {
    DB.withTransaction {
      implicit connection =>
        SQL("SELECT * FROM log_entries l WHERE l.event={eventId} ORDER BY l.id DESC").on('eventId -> event.id).as(LogEntry.parser *)
    }
  }

  def create(logEntry: LogEntry): Long = {
    DB.withTransaction {
      implicit connection =>
        SQL(insertQueryString).on(
          'event -> logEntry.event.id,
          'message -> logEntry.message,
          'whenx -> logEntry.when
        ).executeInsert()
    } match {
      case Some(primaryKey: Long) => primaryKey
      case _ => throw new RuntimeException("Could not insert into database, no PK returned")
    }
  }

  def create(eventId: Long, message: String, when: Date): Long = {
    DB.withTransaction {
      implicit connection =>
        SQL(insertQueryString).on(
          'event -> eventId,
          'message -> message,
          'whenx -> when
        ).executeInsert()
    } match {
      case Some(primaryKey: Long) => primaryKey
      case _ => throw new RuntimeException("Could not insert into database, no PK returned")
    }
  }

  val insertQueryString =
    """
INSERT INTO log_entries (
      event,
      message,
      whenx
    )
    values (
      {event},
      {message},
      {whenx}
    )
    """

  def create(event: Event, message: String) {
    create(LogEntry(event = event, message = message))
  }

  def create(eventId: Long, message: String) {
    create(eventId, message, new Date())
  }
}

