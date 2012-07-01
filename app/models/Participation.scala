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
                         status: Status = On,
                         comment: String = "",
                         user: User,
                         event: Event) extends Ordered[Participation] {
  def compare(that: Participation) = this.status.compare(that.status)
}

object Participation {

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

  def create(participation: Participation): Long = {
    DB.withConnection {
      implicit connection =>
        SQL(insertQueryString).on(
          'status -> participation.status.toString,
          'comment -> participation.comment,
          'user -> participation.user.id,
          'event -> participation.event.id
        ).executeInsert()
    } match {
      case Some(primaryKey) => primaryKey
      case _ => throw new RuntimeException("Could not insert into database, no PK returned")
    }
  }

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


  def update(id: Long, participation: Participation) {
    DB.withConnection {
      implicit connection =>
        SQL(updateQueryString).on(
          'id -> id,
          'status -> participation.status.toString,
          'comment -> participation.comment
        ).executeUpdate()
    }
  }

  val updateQueryString =
    """
UPDATE participations
SET status = {status},
    comment  = {comment}
WHERE id = {id}
    """


  def find(id: Long): Participation = {
    DB.withConnection {
      implicit connection =>
        SQL("SELECT * FROM participations WHERE id={id}").on('id -> id).as(Participation.parser single)
    }
  }

  def findRegisteredMembers(event: Event): Seq[Participation] = {
    DB.withConnection {
      implicit connection =>
        SQL(findRegisteredMembersQueryString).on('event_id -> event.id, 'group_id -> event.group.id.get).as(parser *).sorted
    }
  }

  val findRegisteredMembersQueryString =
    """
SELECT p.*
FROM participations p
WHERE p.event={event_id}
  AND p.userx IN (
    SELECT m.userx
    FROM memberships m
    WHERE m.groupx={group_id}
  )
    """


  def findGuests(event: Event): Seq[Participation] = {
    DB.withConnection {
      implicit connection =>
        SQL(findGuestsQueryString).on('event_id -> event.id, 'group_id -> event.group.id.get).as(parser *).sorted
    }
  }

  val findGuestsQueryString =
    """
SELECT p.*
FROM participations p
WHERE p.event={event_id}
  AND p.userx NOT IN (
    SELECT m.userx
    FROM memberships m
    WHERE m.groupx={group_id}
  )
    """


  def findAll(): Seq[Participation] = {
    DB.withConnection {
      implicit connection =>
        SQL("SELECT * FROM participations").as(parser *)
    }
  }

}
