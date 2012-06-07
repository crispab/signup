package models

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

case class User(
                 id: Pk[Long] = NotAssigned,
                 firstName: String,
                 lastName: String,
                 email: String,
                 phone: String = "",
                 comment: String = ""
                 )

object User {

  val parser = {
    get[Pk[Long]]("id") ~
      get[String]("first_name") ~
      get[String]("last_name") ~
      get[String]("email") ~
      get[String]("phone") ~
      get[String]("comment") map {
      case id ~ firstName ~ lastName ~ email ~ phone ~ comment =>
        User(
          id = id,
          firstName = firstName,
          lastName = lastName,
          email = email,
          phone = phone,
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
        SQL("select * from users u ORDER BY u.first_name, u.last_name").as(User.parser *)
    }
  }

  def create(user: User) {
    DB.withConnection {
      implicit connection =>
        SQL(insertQueryString).on(
          'firstName -> user.firstName,
          'lastName -> user.lastName,
          'email -> user.email,
          'phone -> user.phone,
          'comment -> user.comment
        ).executeUpdate()
    }
  }

  val insertQueryString =
    """
INSERT INTO users (
      first_name,
      last_name,
      email,
      phone,
      comment
    )
    values (
      {firstName},
      {lastName},
      {email},
      {phone},
      {comment}
    )      
    """

  def update(id: Long, user: User) {
    DB.withConnection {
      implicit connection =>
        SQL(updateQueryString).on(
          'id -> id,
          'firstName -> user.firstName,
          'lastName -> user.lastName,
          'email -> user.email,
          'phone -> user.phone,
          'comment -> user.comment
        ).executeUpdate()
    }
  }

  val updateQueryString =
    """
UPDATE users
SET first_name = {firstName},
    last_name = {lastName},
    email = {email},
    phone = {phone},
    comment = {comment}
WHERE id = {id}
    """

  def delete(id: Long) {
    DB.withConnection {
      implicit connection => {
        SQL("DELETE FROM participations p WHERE p.userx={id}").on('id -> id).executeUpdate()
        SQL("DELETE FROM users u WHERE u.id={id}").on('id -> id).executeUpdate()
      }
    }
  }

}

