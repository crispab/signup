package models

import anorm.SqlParser._
import play.api.db.DB
import anorm._
import java.util.Date
import play.api.Play.current

case class Event(id: Pk[Long] = NotAssigned,
                 name: String = "",
                 description: String = "",
                 when: Date = new Date(),
                 venue: String = ""
                  )

object Event {
  val parser = {
    get[Pk[Long]]("id") ~
      get[String]("name") ~
      get[String]("description") ~
      get[Date]("whenx") ~
      get[String]("venue") map {
      case id ~ name ~ description ~ whenx ~ venue =>
        Event(
          id = id,
          name = name,
          description = description,
          when = whenx,
          venue = venue
        )
    }
  }

  def findAll(): Seq[Event] = {
    DB.withConnection {
      implicit connection =>
        SQL("select * from events").as(Event.parser *)
    }
  }

  def find(id: Long): Event = {
    DB.withConnection {
      implicit connection =>
        SQL("select * from events e where e.id={id}").on('id -> id).as(Event.parser *).head
    }
  }

  def create(event: Event) {
    DB.withConnection {
      implicit connection =>
        SQL(insertQueryString).on(
          'name -> event.name,
          'description -> event.description,
          'when -> event.when,
          'venue -> event.venue
        ).executeUpdate()
    }
  }

  val insertQueryString =
    """
INSERT INTO events (
	  name,
	  description,
	  whenx,
      venue
    )
    values (
      {name},
      {description},
      {when},
      {venue}
    )
    """


}