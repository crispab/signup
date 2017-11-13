package se.crisp.signup4.models.dao

import java.util.Date
import javax.inject.{Inject, Singleton}

import anorm.SqlParser.get
import anorm.{RowParser, SQL, ~}
import play.api.db.Database
import se.crisp.signup4.models.{Event, LogEntry}

@Singleton
class LogEntryDAO @Inject() (val database: Database,
                             val eventDAO: EventDAO) {
  import scala.language.postfixOps
  val parser: RowParser[LogEntry] = {
    get[Option[Long]]("id") ~
    get[Long]("event") ~
    get[String]("message") ~
    get[Date]("whenx") map {
      case id ~ event ~ message ~ whenx =>
        LogEntry(
          id = id,
          event = eventDAO.find(event),
          message = message,
          when = whenx
        )
    }
  }

  def findByEvent(event: Event): Seq[LogEntry] = {
    database.withTransaction {
      implicit connection =>
        SQL("SELECT * FROM log_entries l WHERE l.event={eventId} ORDER BY l.id DESC").on('eventId -> event.id).as(parser *)
    }
  }

  def create(logEntry: LogEntry): Long = {
    database.withTransaction {
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
    database.withTransaction {
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
