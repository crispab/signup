package models

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

case class User(
                 id: Pk[Long] = NotAssigned,
                 firstName: String,
                 nickName: String = "",
                 lastName: String = "",
                 primaryEmail: String = "",
                 secondaryEmail: String = "",
                 mobileNr: String = "",
                 comment: String = ""
                 )

object User {

  val parser = {
    get[Pk[Long]]("id") ~
      get[String]("first_name") ~
      get[String]("nick_name") ~
      get[String]("last_name") ~
      get[String]("primary_email") ~
      get[String]("secondary_email") ~
      get[String]("mobile_nr") ~
      get[String]("comment") map {
      case id ~ firstName ~ nickName ~ lastName ~ primaryEmail ~ secondaryEmail ~ mobileNr ~ comment =>
        User(
          id = id,
          firstName = firstName,
          nickName = nickName,
          lastName = lastName,
          primaryEmail = primaryEmail,
          secondaryEmail = secondaryEmail,
          mobileNr = mobileNr,
          comment = comment

        )
    }
  }

  def find(id: Long): User = {
    DB.withConnection {
      implicit connection =>
        SQL("select * from users where id={id}").on('id -> id).as(User.parser *).head
    }
  }

  def findUnregistered(event: Event): Seq[User] = {
    DB.withConnection {
      implicit connection =>
        SQL(findUnregisteredQueryString).on('event_id -> event.id).as(parser *)
    }
  }

  val findUnregisteredQueryString =
    """
SELECT *
FROM users u
WHERE u.id NOT IN (
  SELECT p.userx FROM participations p WHERE p.event = {event_id}
)
ORDER BY u.first_name, u.last_name
    """

  def findAll(): Seq[User] = {
    DB.withConnection {
      implicit connection =>
        SQL("select * from users").as(User.parser *)
    }
  }

  def create(user: User) {
    DB.withConnection {
      implicit connection =>
        SQL(insertQueryString).on(
          'firstName -> user.firstName,
          'nickName -> user.nickName,
          'lastName -> user.lastName,
          'primaryEmail -> user.primaryEmail,
          'secondaryEmail -> user.secondaryEmail,
          'mobileNr -> user.mobileNr,
          'comment -> user.comment
        ).executeUpdate()
    }
  }

  val insertQueryString =
    """
INSERT INTO users (
      first_name,
      nick_name,
	  last_name,
      primary_email,
      secondary_email,
	  mobile_nr,
      comment
    )
    values (
      {firstName},
      {nickName},
      {lastName},
      {primaryEmail},
      {secondaryEmail},
      {mobileNr},
      {comment}
    )      
    """

  def update(id: Long, user: User) {
    DB.withConnection {
      implicit connection =>
        SQL(updateQueryString).on(
          'id -> id,
          'firstName -> user.firstName,
          'nickName -> user.nickName,
          'lastName -> user.lastName,
          'primaryEmail -> user.primaryEmail,
          'secondaryEmail -> user.secondaryEmail,
          'mobileNr -> user.mobileNr,
          'comment -> user.comment
        ).executeUpdate()
    }
  }

  val updateQueryString =
    """
UPDATE users
SET first_name = {firstName},
    nick_name = {nickName},
    last_name = {lastName},
    primary_email = {primaryEmail},
    secondary_email = {secondaryEmail},
    mobile_nr = {mobileNr},
    comment = {comment}
WHERE id = {id}
    """
}

