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
                  eventStatus: EventStatus = Created
                  ) {

  def lastSignupDatePassed() = {
    val today = new DateTime().withTimeAtStartOfDay
    val lastSignUpDay = new DateTime(lastSignUpDate).withTimeAtStartOfDay
    today.isAfter(lastSignUpDay)
  }

  def isCancelled = eventStatus == Cancelled
}

object Event {
  import scala.language.postfixOps
  val parser = {
    get[Option[Long]]("id") ~
      get[String]("name") ~
      get[String]("description") ~
      get[util.Date]("start_time") ~
      get[util.Date]("end_time") ~
      get[util.Date]("last_signup_date") ~
      get[String]("venue") ~
      get[Boolean]("allow_extra_friends") ~
      get[Long]("groupx") ~
      get[String]("event_status") map {
      case id ~ name ~ description ~ start_time ~ end_time ~ last_signup_date ~ venue ~ allow_extra_friends ~ groupx ~ event_status =>
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
          eventStatus = EventStatus.withName(event_status)
        )
    }
  }

  def findAll(): Seq[Event] = {
    DB.withTransaction {
      implicit connection =>
        SQL("SELECT * FROM events ORDER BY start_time DESC").as(Event.parser *)
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
          'event_status -> event.eventStatus.toString
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
      event_status
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
      {event_status}
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
          'event_status -> event.eventStatus.toString
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
    event_status = {event_status}
WHERE id = {id}
    """


  def cancel(id: Long) {
    DB.withTransaction {
      implicit connection =>
        SQL("UPDATE events SET event_status = 'Cancelled' WHERE id = {id}").on('id -> id).executeUpdate()
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
}
