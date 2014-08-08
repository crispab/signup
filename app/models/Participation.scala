package models

import java.util.Date

import anorm.SqlParser._
import anorm._
import org.joda.time.DateTime
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
                         event: Event,
                         signUpTime: Option[Date]) extends Ordered[Participation] {

  def compare(that: Participation) = this.user.compare(that.user)

  def participantsComing = {
    status match {
      case Status.On => numberOfParticipants
      case Status.Maybe => numberOfParticipants
      case _ => 0
    }
  }

  def isLateSignUp = {
    if(status != On || signUpTime.isEmpty) {
      false
    } else {
      val lastSignUpDate = new DateTime(event.lastSignUpDate).withTimeAtStartOfDay()
      val signUpDate = new DateTime(signUpTime.get).withTimeAtStartOfDay()
      signUpDate.isAfter(lastSignUpDate)
    }
  }
}

object Participation {

  def apply(id: Pk[Long],
           status: Status,
           numberOfParticipants: Int,
           comment: String,
           user: User,
           event: Event): Participation = {
    if (status == On)
      new Participation(id, status, numberOfParticipants, comment, user, event, signUpTime = Option(new Date()))
    else
      new Participation(id, status, numberOfParticipants, comment, user, event, signUpTime = None)
  }

  def apply(status: Status, user: User, event: Event): Participation = apply(id = NotAssigned, status, numberOfParticipants = 1, comment = "", user, event)


  import scala.language.postfixOps
  val parser = {
    get[Pk[Long]]("id") ~
      get[String]("status") ~
      get[Int]("number_of_participants") ~
      get[String]("comment") ~
      get[Long]("userx") ~
      get[Long]("event") ~
      get[Option[Date]]("signup_time") map {
      case id ~ status ~ number_of_participants ~ comment ~ userx ~ event ~ signup_time =>
        Participation(
          id = id,
          status = Status.withName(status),
          numberOfParticipants = number_of_participants,
          comment = comment,
          user = User.find(userx),
          event = Event.find(event),
          signUpTime = signup_time
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
          'event -> participation.event.id,
          'signup_time -> participation.signUpTime
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
          'event -> eventId,
          'signup_time -> None
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
      event,
      signup_time
    )
    VALUES (
      {status},
      {number_of_participants},
      {comment},
      {user},
      {event},
      {signup_time}
    )
    """


  def update(id: Long, newParticipation: Participation) {
    val oldParticipation = find(id)
    val signUpTime = calculateNewSignUpTime(oldParticipation, newParticipation)
    DB.withTransaction {
      implicit connection =>
        SQL(updateQueryString).on(
          'id -> id,
          'status -> newParticipation.status.toString,
          'number_of_participants -> newParticipation.numberOfParticipants,
          'comment -> newParticipation.comment,
          'signup_time -> signUpTime
        ).executeUpdate()
    }
  }

  def changedToOn(oldStatus: Status, newStatus: Status): Boolean = {
    (newStatus == On) && (oldStatus != On)
  }

  def calculateNewSignUpTime(oldParticipation: Participation, newParticipation: Participation) = {
    if(changedToOn(oldParticipation.status, newParticipation.status))
      newParticipation.signUpTime
    else
      oldParticipation.signUpTime
  }

  val updateQueryString =
    """
UPDATE participations
SET status = {status},
    number_of_participants = {number_of_participants},
    comment = {comment},
    signup_time = {signup_time}
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
