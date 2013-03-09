package models

import anorm._
import java.util.Date
import anorm.SqlParser._
import play.api.db.DB
import play.api.Play.current


case class LogEntry(
  id: Pk[Long] = NotAssigned,
  event: Event,
  message: String,
  when: Date = new Date()
)

object LogEntry {
  val parser = {
    get[Pk[Long]]("id") ~
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
    DB.withConnection {
      implicit connection =>
        SQL("SELECT * FROM log_entries l WHERE l.event={eventId} ORDER BY l.id DESC").on('eventId -> event.id).as(LogEntry.parser *)
    }
  }

  def create(logEntry: LogEntry): Long = {
    DB.withConnection {
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
}

