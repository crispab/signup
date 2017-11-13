package se.crisp.signup4.models.dao

import java.util.Date
import javax.inject.{Inject, Singleton}

import anorm.SqlParser.{get, scalar}
import anorm.{RowParser, SQL, ~}
import play.api.db.Database
import se.crisp.signup4.models.Status._
import se.crisp.signup4.models._

@Singleton
class ParticipationDAO @Inject() (val database: Database,
                                  val eventDAO: EventDAO,
                                  val userDAO: UserDAO){

  import scala.language.postfixOps

  val parser: RowParser[Participation] = {
    get[Option[Long]]("id") ~
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
          user = userDAO.find(userx),
          event = eventDAO.find(event),
          signUpTime = signup_time
        )
    }
  }

  def create(participation: Participation): Long = {
    database.withTransaction {
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
    database.withTransaction {
      implicit connection =>
        SQL(insertQueryString).on(
          'status -> Status.Unregistered.toString,
          'number_of_participants -> 1,
          'comment -> "",
          'user -> userId,
          'event -> eventId,
          'signup_time -> Option.empty[Date]
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
    database.withTransaction {
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

  def calculateNewSignUpTime(oldParticipation: Participation, newParticipation: Participation): Option[Date] = {
    if(newParticipation.status != oldParticipation.status)
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
    database.withTransaction {
      implicit connection =>
        SQL("SELECT * FROM participations WHERE id={id}").on('id -> id).as(parser single)
    }
  }

  def findByEventAndUser(eventId: Long, userId: Long): Option[Participation] = {
    database.withTransaction {
      implicit connection =>
        SQL("SELECT * FROM participations WHERE event={eventId} AND userx={userId}").on('eventId -> eventId, 'userId -> userId).as(parser singleOpt)
    }
  }

  def findGuestsByStatus(status: Status, event: Event): Seq[Participation] = {
    database.withTransaction {
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
    database.withTransaction {
      implicit connection =>
        ParticipationLists(event,
          on = findGuestsByStatus(On, event),
          maybe = findGuestsByStatus(Maybe, event),
          off = findGuestsByStatus(Off, event),
          unregistered = findGuestsByStatus(Unregistered, event))
    }
  }

  def findAll(): Seq[Participation] = {
    database.withTransaction {
      implicit connection =>
        SQL("SELECT * FROM participations").as(parser *).sorted
    }
  }

  def findMembersByStatus(status: Status, event: Event): Seq[Participation] = {
    if (status != Unregistered) {
      database.withTransaction {
        implicit connection =>
          SQL(findMembersByStatusQueryString).on('eventId -> event.id, 'status -> status.toString, 'groupId -> event.group.id).as(parser *).sorted
      }
    } else {
      userDAO.findUnregisteredMembers(event) map { user => Participation(status = Unregistered, user = user, event = event, signUpTime = None) }
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
    database.withTransaction {
      implicit connection =>
        ParticipationLists(event,
          on = findMembersByStatus(On, event),
          maybe = findMembersByStatus(Maybe, event),
          off = findMembersByStatus(Off, event),
          unregistered = findMembersByStatus(Unregistered, event))
    }
  }


  def delete(id: Long) {
    database.withTransaction {
      implicit connection => {
        SQL("DELETE FROM participations p WHERE p.id={id}").on('id -> id).executeUpdate()
      }
    }
  }


  def findStatus(user: User, event: Event): Status = {
    database.withTransaction {
      implicit connection =>
        val statusStr = SQL("""
          SELECT p.status
          FROM participations p
          WHERE p.userx={user} AND p.event={event}
        """).on('user -> user.id.get, 'event -> event.id.get).as(scalar[String].singleOpt).getOrElse("Unregistered")
        Status.withName(statusStr)
    }
  }

}
