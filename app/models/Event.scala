package models

import java.util

import anorm.SqlParser._
import anorm._
import org.joda.time.DateTime
import play.api.Play.current
import play.api.db.DB

object EventStatus extends Enumeration {
  type EventStatus = Value
  val Created, Cancelled = Value
}

import models.EventStatus._

case class Event(
                  id: Option[Long] = None,
                  group: Group,
                  name: String,
                  description: String = "",
                  startTime: util.Date,
                  endTime: util.Date,
                  lastSignUpDate: util.Date,
                  venue: String = "",
                  allowExtraFriends: Boolean = false,
                  eventStatus: EventStatus = Created,
                  maxParticipants: Option[Int] = None,
                  cancellationReason: Option[String] = None
                  ) {

  def lastSignupDatePassed(): Boolean = {
    val today = new DateTime().withTimeAtStartOfDay
    val lastSignUpDay = new DateTime(lastSignUpDate).withTimeAtStartOfDay
    today.isAfter(lastSignUpDay)
  }

  def isCancelled: Boolean = eventStatus == Cancelled
}

object Event {
  import scala.language.postfixOps
  val parser: RowParser[Event] = {
    get[Option[Long]]("id") ~
      get[String]("name") ~
      get[String]("description") ~
      get[util.Date]("start_time") ~
      get[util.Date]("end_time") ~
      get[util.Date]("last_signup_date") ~
      get[String]("venue") ~
      get[Boolean]("allow_extra_friends") ~
      get[Long]("groupx") ~
      get[String]("event_status") ~
      get[Option[Int]]("max_participants") ~
      get[Option[String]]("cancellation_reason") map {
      case id ~ name ~ description ~ start_time ~ end_time ~ last_signup_date ~ venue ~ allow_extra_friends ~ groupx ~ event_status ~ max_participants ~ cancellation_reason =>
        Event(
          id = id,
          name = name,
          description = description,
          startTime = start_time,
          endTime = end_time,
          lastSignUpDate = last_signup_date,
          venue = venue,
          allowExtraFriends = allow_extra_friends,
          group = Group.find(groupx),
          eventStatus = EventStatus.withName(event_status),
          maxParticipants = max_participants,
          cancellationReason = cancellation_reason
        )
    }
  }

  def findAll(): Seq[Event] = {
    DB.withTransaction {
      implicit connection =>
        SQL("SELECT * FROM events ORDER BY start_time DESC").as(Event.parser *)
    }
  }

  def findFutureEventsByUser(user: User): Seq[Event] = {
    DB.withTransaction {
      val today = new DateTime().withTimeAtStartOfDay().toDate
      implicit connection =>
        SQL("""
          SELECT e.*
          FROM events e, memberships m
          WHERE e.groupx = m.groupx AND m.userx = {user} AND e.start_time >= {today} AND e.event_status != 'Cancelled'
          UNION
          SELECT e.*
          FROM events e, participations p
          WHERE p.event=e.id AND p.userx={user} AND e.start_time >= {today} AND e.event_status != 'Cancelled'
          ORDER BY last_signup_date ASC
        """).on('user -> user.id.get, 'today -> today).as(Event.parser *)
    }
  }

  def findFutureEventsByGroup(group: Group): Seq[Event] = {
    DB.withTransaction {
      val today = new DateTime().withTimeAtStartOfDay().toDate
      implicit connection =>
        SQL("SELECT e.* FROM events e WHERE e.groupx={groupId} AND e.start_time >= {today} AND e.event_status != 'Cancelled' ORDER BY e.last_signup_date ASC").on('groupId -> group.id.get, 'today -> today).as(Event.parser *)
    }
  }

  def findAllEventsByGroup(group: Group): Seq[Event] = {
    DB.withTransaction {
      implicit connection =>
        SQL("SELECT e.* FROM events e WHERE e.groupx={groupId} ORDER BY e.start_time DESC").on('groupId -> group.id.get).as(Event.parser *)
    }
  }

  def find(id: Long): Event = {
    DB.withTransaction {
      implicit connection =>
        SQL("SELECT * FROM events e WHERE e.id={id}").on('id -> id).as(Event.parser single)
    }
  }

  def create(event: Event): Long = {
    DB.withTransaction {
      implicit connection =>
        SQL(insertQueryString).on(
          'name -> event.name,
          'description -> event.description,
          'start_time -> event.startTime,
          'end_time -> event.endTime,
          'last_signup_date -> event.lastSignUpDate,
          'venue -> event.venue,
          'allow_extra_friends -> event.allowExtraFriends,
          'groupx -> event.group.id,
          'event_status -> event.eventStatus.toString,
          'max_participants -> event.maxParticipants,
          'cancellation_reason -> event.cancellationReason
        ).executeInsert()
    } match {
      case Some(primaryKey: Long) => primaryKey
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
      last_signup_date,
      venue,
      allow_extra_friends,
      groupx,
      event_status,
      max_participants,
      cancellation_reason
    )
    values (
      {name},
      {description},
      {start_time},
      {end_time},
      {last_signup_date},
      {venue},
      {allow_extra_friends},
      {groupx},
      {event_status},
      {max_participants},
      {cancellation_reason}
    )
    """

  def update(id: Long, event: Event) {
    DB.withTransaction {
      implicit connection =>
        SQL(updateQueryString).on(
          'id -> id,
          'name -> event.name,
          'description -> event.description,
          'start_time -> event.startTime,
          'end_time -> event.endTime,
          'last_signup_date -> event.lastSignUpDate,
          'venue -> event.venue,
          'allow_extra_friends -> event.allowExtraFriends,
          'groupx -> event.group.id,
          'event_status -> event.eventStatus.toString,
          'max_participants -> event.maxParticipants,
          'cancellation_reason -> event.cancellationReason
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
    last_signup_date = {last_signup_date},
    venue = {venue},
    allow_extra_friends = {allow_extra_friends},
    groupx = {groupx},
    event_status = {event_status},
    max_participants = {max_participants},
    cancellation_reason = {cancellation_reason}
WHERE id = {id}
    """


  def cancel(id: Long, reason: Option[String]) {
    DB.withTransaction {
      implicit connection =>
        SQL("UPDATE events SET event_status = 'Cancelled', cancellation_reason = {cancellation_reason} WHERE id = {id}").on('id -> id, 'cancellation_reason -> reason).executeUpdate()
        SQL("DELETE FROM reminders r WHERE r.event={id}").on('id -> id).executeUpdate()
    }
  }


  def delete(id: Long) {
    DB.withTransaction() {
      implicit connection =>
        SQL("DELETE FROM participations p WHERE p.event={id}").on('id -> id).executeUpdate()
        SQL("DELETE FROM log_entries l WHERE l.event={id}").on('id -> id).executeUpdate()
        SQL("DELETE FROM reminders r WHERE r.event={id}").on('id -> id).executeUpdate()
        SQL("DELETE FROM events e WHERE e.id={id}").on('id -> id).executeUpdate()
    }
  }

  def hasSeatsAvailable(id: Long): Boolean = {
    DB.withTransaction {
      implicit connection =>
        SQL(hasSeatsAvailableQueryString).on('eventId -> id).as(scalar[Option[Boolean]].single).get
    }
  }

  val hasSeatsAvailableQueryString =
    """
SELECT
    e.max_participants IS NULL OR (e.max_participants > COALESCE((
        SELECT
            SUM(p.number_of_participants)
        FROM
            participations p
        WHERE
            p.event={eventId}
        AND p.status='On'
        GROUP BY
            p.event ), 0)
    ) AS seats_available
FROM
    events e
WHERE
    e.id = {eventId}
    """

  def isFullyBooked(event: Event): Boolean = !hasSeatsAvailable(event.id.get)

}
