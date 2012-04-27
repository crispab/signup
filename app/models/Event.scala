package models

import anorm.SqlParser._
import play.api.db.DB
import anorm._
import java.util.Date
import play.api.Play.current

case class Event(
                  id: Pk[Long] = NotAssigned,
                  name: String = "",
                  description: String = "",
                  when: Date = new Date()
                  )

object Event {
  val parser = {
    get[Pk[Long]]("id") ~
      get[String]("name") ~
      get[String]("description") ~
      get[Date]("when") map {
      case id ~ name ~ description ~ when =>
        Event(
          id = id,
          name = name,
          description = description,
          when = when
        )
    }
  }

  def findAll(): Seq[Event] = {
    DB.withConnection {
      implicit connection =>
        SQL("select * from events").as(Event.parser *)
    }
  }
  
  def find(id : Long): Event = {
    DB.withConnection {
      implicit connection =>
        SQL("select * from events e where e.id={id}").on('id -> id).as(Event.parser *).head
    }
  }

  def create(event: Event): Unit = {
    DB.withConnection {
      implicit connection =>
        SQL(insertQueryString).on(
          'name -> event.name,
          'description -> event.description,
          'when -> event.when
        ).executeUpdate()
    }
  }

  val insertQueryString =
    """
INSERT INTO events (
	  name,
	  description,
	  when
    )
    values (
      {name},
      {description},
      {when}
    )
    """


}