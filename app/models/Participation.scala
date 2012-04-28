package models

import play.api.db.DB
import play.api.Play.current
import anorm._

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

  val insertQueryString =
    """
INSERT INTO participations (
      status,
      comment,
      user,
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
