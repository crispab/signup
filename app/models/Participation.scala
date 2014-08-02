package models

import anorm.SqlParser._
import anorm._
import play.api.Play.current
import play.api.db.DB


object Status extends Enumeration {
  type Status = Value
  val On, Maybe, Off, Unregistered = Value
}

import models.Status._

case class Participation(id: Pk[Long] = NotAssigned,
                         status: Status = On,
                         numberOfParticipants: Int = 1,
                         comment: String = "",
                         user: User,
                         event: Event) extends Ordered[Participation] {
  def compare(that: Participation) = this.user.compare(that.user)
}

object Participation {
  import scala.language.postfixOps
  val parser = {
    get[Pk[Long]]("id") ~
      get[String]("status") ~
      get[Int]("number_of_participants") ~
      get[String]("comment") ~
      get[Long]("userx") ~
      get[Long]("event") map {
      case id ~ status ~ number_of_participants ~ comment ~ userx ~ event =>
        Participation(
          id = id,
          status = Status.withName(status),
          numberOfParticipants = number_of_participants,
          comment = comment,
          user = User.find(userx),
          event = Event.find(event)
        )
    }
  }

  def create(participation: Participation): Long = {
    DB.withTransaction {
      implicit connection =>
        SQL(insertQueryString).on(
          'status -> participation.status.toString,
          'number_of_participants -> participation.numberOfParticipants,
          'comment -> participation.comment,
          'user -> participation.user.id,
          'event -> participation.event.id
        ).executeInsert()
    } match {
      case Some(primaryKey: Long) => primaryKey
      case _ => throw new RuntimeException("Could not insert into database, no PK returned")
    }
  }

  def createGuest(eventId: Long, userId: Long): Long = {
    DB.withTransaction {
      implicit connection =>
        SQL(insertQueryString).on(
          'status -> Status.Unregistered.toString,
          'number_of_participants -> 1,
          'comment -> "",
          'user -> userId,
          'event -> eventId
        ).executeInsert()
    } match {
      case Some(primaryKey: Long) => primaryKey
      case _ => throw new RuntimeException("Could not insert into database, no PK returned")
    }
  }

  val insertQueryString =
    """
INSERT INTO participations (
      status,
      number_of_participants,
      comment,
      userx,
      event
    )
    VALUES (
      {status},
      {number_of_participants},
      {comment},
      {user},
      {event}
    )
    """


  def update(id: Long, participation: Participation) {
    DB.withTransaction {
      implicit connection =>
        SQL(updateQueryString).on(
          'id -> id,
          'status -> participation.status.toString,
          'number_of_participants -> participation.numberOfParticipants,
          'comment -> participation.comment
        ).executeUpdate()
    }
  }

  val updateQueryString =
    """
UPDATE participations
SET status = {status},
    number_of_participants  = {number_of_participants},
    comment  = {comment}
WHERE id = {id}
    """


  def find(id: Long): Participation = {
    DB.withTransaction {
      implicit connection =>
        SQL("SELECT * FROM participations WHERE id={id}").on('id -> id).as(Participation.parser single)
    }
  }

  def findByEventAndUser(eventId: Long, userId: Long): Option[Participation] = {
    DB.withTransaction {
      implicit connection =>
        SQL("SELECT * FROM participations WHERE event={eventId} AND userx={userId}").on('eventId -> eventId, 'userId -> userId).as(Participation.parser singleOpt)
    }
  }

  def findGuestsByStatus(status: Status, event: Event): Seq[Participation] = {
    DB.withTransaction {
      implicit connection =>
        SQL(findGuestsByStatusQueryString).on('eventId -> event.id, 'status -> status.toString, 'groupId -> event.group.id.get).as(parser *).sorted
    }
  }

  val findGuestsByStatusQueryString =
    """
SELECT p.*
FROM participations p
WHERE p.event={eventId}
  AND p.status={status}
  AND p.userx NOT IN (
    SELECT m.userx
    FROM memberships m
    WHERE m.groupx={groupId}
  )
    """

  def findGuests(event: Event): ParticipationLists = {
    DB.withTransaction {
      implicit connection =>
        ParticipationLists(event,
                           on = findGuestsByStatus(On, event),
                           maybe = findGuestsByStatus(Maybe, event),
                           off = findGuestsByStatus(Off, event),
                           unregistered = findGuestsByStatus(Unregistered, event))
    }
  }

  def findAll(): Seq[Participation] = {
    DB.withTransaction {
      implicit connection =>
        SQL("SELECT * FROM participations").as(parser *).sorted
    }
  }

  def findMembersByStatus(status: Status, event: Event): Seq[Participation] = {
    if (status != Unregistered) {
      DB.withTransaction {
        implicit connection =>
          SQL(findMembersByStatusQueryString).on('eventId -> event.id, 'status -> status.toString, 'groupId -> event.group.id).as(parser *).sorted
      }
    } else {
      User.findUnregisteredMembers(event) map { user => Participation(status = Unregistered, user = user, event = event) }
    }
  }

  val findMembersByStatusQueryString =
    """
SELECT p.*
FROM participations p
WHERE p.event={eventId}
  AND p.status={status}
  AND p.userx IN (
    SELECT m.userx
    FROM memberships m
    WHERE m.groupx={groupId}
  )
    """


  def findMembers(event: Event): ParticipationLists = {
    DB.withTransaction {
      implicit connection =>
        ParticipationLists(event,
                           on = findMembersByStatus(On, event),
                           maybe = findMembersByStatus(Maybe, event),
                           off = findMembersByStatus(Off, event),
                           unregistered = findMembersByStatus(Unregistered, event))
    }
  }


  def delete(id: Long) {
    DB.withTransaction {
      implicit connection => {
        SQL("DELETE FROM participations p WHERE p.id={id}").on('id -> id).executeUpdate()
      }
    }
  }

}
