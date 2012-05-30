package models

import anorm.SqlParser._
import play.api.db.DB
import anorm._
import java.util.Date
import play.api.Play.current

case class Event(id: Pk[Long] = NotAssigned,
                 name: String = "",
                 description: String = "",
                 start_time: Date = new Date(),
                 end_time: Date = new Date(),
                 venue: String = ""
                  )

object Event {
  val parser = {
    get[Pk[Long]]("id") ~
      get[String]("name") ~
      get[String]("description") ~
      get[Date]("start_time") ~
      get[Date]("end_time") ~
      get[String]("venue") map {
      case id ~ name ~ description ~ start_time ~ end_time ~ venue =>
        Event(
          id = id,
          name = name,
          description = description,
          start_time = start_time,
          end_time = end_time,
          venue = venue
        )
    }
  }

  def findAll(): Seq[Event] = {
    DB.withConnection {
      implicit connection =>
        SQL("SELECT * FROM events ORDER BY start_time DESC").as(Event.parser *)
    }
  }

  def find(id: Long): Event = {
    DB.withConnection {
      implicit connection =>
        SQL("SELECT * FROM events e WHERE e.id={id}").on('id -> id).as(Event.parser *).head
    }
  }

  def create(event: Event) {
    DB.withConnection {
      implicit connection =>
        SQL(insertQueryString).on(
          'name -> event.name,
          'description -> event.description,
          'start_time -> event.start_time,
          'end_time -> event.end_time,
          'venue -> event.venue
        ).executeUpdate()
    }
  }

  val insertQueryString =
    """
INSERT INTO events (
      name,
      description,
      start_time,
      end_time,
      venue
    )
    values (
      {name},
      {description},
      {start_time},
      {end_time},
      {venue}
    )
    """

  def update(id: Long, event: Event) {
    DB.withConnection {
      implicit connection =>
        SQL(updateQueryString).on(
          'id -> id,
          'name -> event.name,
          'description -> event.description,
          'start_time -> event.start_time,
          'end_time -> event.end_time,
          'venue -> event.venue
        ).executeUpdate()
    }
  }


  val updateQueryString =
    """
UPDATE events
SET name = {name},
    description = {description},
    start_time = {start_time},
    end_time = {end_time},
    venue = {venue}
WHERE id = {id}
    """

  def delete(id: Long) {
    DB.withConnection {
      implicit connection =>
        SQL("DELETE FROM participations p WHERE p.event={id}").on('id -> id).executeUpdate()
    }
    DB.withConnection {
      implicit connection =>
        SQL("DELETE FROM events e WHERE e.id={id}").on('id -> id).executeUpdate()
    }
  }
}
