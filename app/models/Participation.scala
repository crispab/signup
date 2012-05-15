package models

import anorm.SqlParser._
import play.api.db.DB
import anorm._
import play.api.Play.current

object Status extends Enumeration {
  type Status = Value
  val On, Maybe, Off, Unregistered = Value
}

import Status._

case class Participation(id: Pk[Long] = NotAssigned,
                         status: Status = Unregistered,
                         comment: String = "",
                         user: User,
                         event: Event) extends Ordered[Participation] {
  def compare(that: Participation) = this.status.compare(that.status)
}

object Participation {

  def create(participation: Participation) {
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
    get[String]("comment") ~
    get[Long]("userx") ~
    get[Long]("event") map {
      case id ~ status ~ comment ~ userx ~ event =>
        Participation(
          id = id,
          status = Status.withName(status),
          comment = comment,
          user = User.find(userx),
          event = Event.find(event)
        )
    }
  }
  
  def findRegistered(event: Event):Seq[Participation] = {
    DB.withConnection {
      implicit connection =>
        SQL(findRegisteredQueryString).on('event_id -> event.id).as(parser *).sorted
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
SELECT * FROM participations
    """

  val findRegisteredQueryString =
    """
SELECT * FROM participations p WHERE p.event={event_id}
    """

  val insertQueryString =
    """
INSERT INTO participations (
      status,
      comment,
      userx,
      event
    )
    VALUES (
      {status},
      {comment},
      {user},
      {event}
    )
    """
}
