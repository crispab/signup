package models

import anorm.SqlParser._
import play.api.db.DB
import anorm._
import play.api.Play.current
import java.util

case class Event(
                  id: Pk[Long] = NotAssigned,
                  group: Group,
                  name: String,
                  description: String = "",
                  start_time: util.Date,
                  end_time: util.Date,
                  venue: String = ""
                  )

object Event {
  val parser = {
    get[Pk[Long]]("id") ~
      get[String]("name") ~
      get[String]("description") ~
      get[util.Date]("start_time") ~
      get[util.Date]("end_time") ~
      get[String]("venue") ~
      get[Long]("groupx") map {
      case id ~ name ~ description ~ start_time ~ end_time ~ venue ~ groupx =>
        Event(
          id = id,
          name = name,
          description = description,
          start_time = start_time,
          end_time = end_time,
          venue = venue,
          group = Group.find(groupx)
        )
    }
  }

  def findAll(): Seq[Event] = {
    DB.withConnection {
      implicit connection =>
        SQL("SELECT * FROM events ORDER BY start_time DESC").as(Event.parser *)
    }
  }

  def findByGroup(group: Group): Seq[Event] = {
    DB.withConnection {
      implicit connection =>
        SQL("SELECT * FROM events WHERE groupx={groupId} ORDER BY start_time DESC").on('groupId -> group.id.get).as(Event.parser *)
    }
  }

  def find(id: Long): Event = {
    DB.withConnection {
      implicit connection =>
        SQL("SELECT * FROM events e WHERE e.id={id}").on('id -> id).as(Event.parser single)
    }
  }

  def create(event: Event): Long = {
    DB.withConnection {
      implicit connection =>
        SQL(insertQueryString).on(
          'name -> event.name,
          'description -> event.description,
          'start_time -> event.start_time,
          'end_time -> event.end_time,
          'venue -> event.venue,
          'groupx -> event.group.id
        ).executeInsert()
    } match {
      case Some(primaryKey) => primaryKey
      case _ => throw new RuntimeException("Could not insert into database, no PK returned")
    }
  }

  val insertQueryString =
    """
INSERT INTO events (
      name,
      description,
      start_time,
      end_time,
      venue,
      groupx
    )
    values (
      {name},
      {description},
      {start_time},
      {end_time},
      {venue},
      {groupx}
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
          'venue -> event.venue,
          'groupx -> event.group.id
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
    venue = {venue},
    groupx = {groupx}
WHERE id = {id}
    """

  def delete(id: Long) {
    DB.withConnection {
      implicit connection =>
        SQL("DELETE FROM participations p WHERE p.event={id}").on('id -> id).executeUpdate()
        SQL("DELETE FROM events e WHERE e.id={id}").on('id -> id).executeUpdate()
    }
  }
}
