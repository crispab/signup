package models

import anorm.SqlParser._
import play.api.db.DB
import anorm._
import java.util.Date
import play.api.Play.current

object Status extends Enumeration {
  type Status = Value
  val On, Off, Maybe, Unregistered = Value
}

import Status._

case class Participation(id: Pk[Long] = NotAssigned,
                         status: Status = Unregistered,
                         comment: String = "",
                         user: User,
                         event: Event)

object Participation {
  def create(participation: Participation): Unit = {
    DB.withConnection {
      implicit connection =>
        SQL(insertQueryString).on(
          'status -> participation.status.toString,
          'comment -> participation.comment,
          'user -> participation.user.id,
          'event -> participation.event.id
        ).executeUpdate()
    }
  }
  
  val parser = {
    get[Pk[Long]]("id") ~
    get[String]("status") ~
    get[String]("comment") map {
      case id ~ status ~ comment =>
        Participation(
          id = id,
          status = Status.withName(status),
          comment = comment,
          user = User(firstName = "DummyUser"),
          event = Event(name = "DummyEvent")
        )
    }
  }
  
  def findAll(): Seq[Participation] = {
    DB.withConnection {
      implicit connection =>
        SQL(findAllQueryString).as(parser *)
    }
  }
  
  val findAllQueryString = 
    """
		  SELECT * from participations
    """

  val insertQueryString =
    """
INSERT INTO participations (
      status,
      comment,
      userx,
      event
    )
    values (
      {status},
      {comment},
      {user},
      {event}
    )
    """
}
