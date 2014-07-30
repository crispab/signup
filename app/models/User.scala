package models

import anorm.SqlParser._
import anorm._
import models.security.{NormalUser, Permission}
import play.api.Play.current
import play.api.db._
import util.AuthHelper

case class User(
                 id: Pk[Long] = NotAssigned,
                 firstName: String,
                 lastName: String,
                 email: String,
                 phone: String = "",
                 comment: String = "",
                 permission: Permission = NormalUser,
                 password: String = "*",
                 imageUrl: String
                 )  extends Ordered[User] {
  def compare(that: User) = {
    val c = this.firstName.compare(that.firstName)
    if (c != 0) {
      c
    } else {
      this.lastName.compare(that.lastName)
    }
  }

  def imageUrl(size: Int = 80): String = {
    imageUrl.replaceAll("\\{size\\}", size.toString)
  }
}

object User {

  import scala.language.postfixOps

  val NOT_CHANGED_PASSWORD = "Y2j1EsDUvc6V" // just a random string

  val parser = {
    get[Pk[Long]]("id") ~
      get[String]("first_name") ~
      get[String]("last_name") ~
      get[String]("email") ~
      get[String]("phone") ~
      get[String]("comment") ~
      get[String]("permission") ~
      get[String]("pwd")  ~
      get[String]("image_url") map {
      case id ~ firstName ~ lastName ~ email ~ phone ~ comment ~ permission ~ pwd ~ image_url =>
        User(
          id = id,
          firstName = firstName,
          lastName = lastName,
          email = email.toLowerCase,
          phone = phone,
          comment = comment,
          permission = Permission.withName(permission),
          password = pwd,
          imageUrl = image_url
        )
    }
  }

  def find(id: Long): User = {
    DB.withTransaction {
      implicit connection =>
        SQL("select * from users where id={id}").on('id -> id).as(User.parser single)
    }
  }


  def findByEmail(email: String): Option[User] = {
    DB.withTransaction {
      implicit connection =>
        SQL("select * from users where lower(email)={email}").on('email -> email.toLowerCase).as(User.parser singleOpt)
    }
  }

  def findUnregisteredMembers(event: Event): Seq[User] = {
    DB.withTransaction {
      implicit connection =>
        SQL(findUnregisteredMembersQueryString).on('event_id -> event.id, 'group_id -> event.group.id).as(parser *).sorted
    }
  }

  val findUnregisteredMembersQueryString =
    """
SELECT u.*
FROM users u, memberships m
WHERE m.userx = u.id
  AND m.groupx = {group_id}
  AND u.id NOT IN (SELECT p.userx FROM participations p WHERE p.event = {event_id})
ORDER BY u.first_name, u.last_name
    """

  def findNonMembers(groupId: Long): Seq[User] = {
    DB.withTransaction {
      implicit connection =>
        SQL(findNonMembersQueryString).on('group_id -> groupId).as(parser *).sorted
    }
  }

  val findNonMembersQueryString =
    """
SELECT u.*
FROM users u
WHERE u.id NOT IN (SELECT m.userx FROM memberships m WHERE m.groupx = {group_id})
    """

  def findNonGuests(eventId: Long): Seq[User] = {
    DB.withTransaction {
      implicit connection =>
        SQL(findNonGuestsQueryString).on('eventId -> eventId).as(parser *).sorted
    }
  }

  val findNonGuestsQueryString =
    """
SELECT u.*
FROM users u
WHERE u.id NOT IN ((SELECT m.userx FROM memberships m, events e WHERE m.groupx = e.groupx AND e.id = {eventId})
                   UNION (SELECT p.userx FROM participations p WHERE p.event = {eventId}))
    """

  def findAll(): Seq[User] = {
    DB.withTransaction {
      implicit connection =>
        SQL("select * from users u ORDER BY u.first_name, u.last_name").as(User.parser *).sorted
    }
  }

  def create(user: User): Long = {
    DB.withTransaction {
      implicit connection =>
        val password = user.password match {
          case User.NOT_CHANGED_PASSWORD => "*"
          case _ => AuthHelper.calculateHash(user.password)
        }
        SQL(insertQueryString).on(
          'firstName -> user.firstName,
          'lastName -> user.lastName,
          'email -> user.email.toLowerCase,
          'phone -> user.phone,
          'comment -> user.comment,
          'permission -> user.permission.toString,
          'pwd -> password,
          'imageUrl -> user.imageUrl
        ).executeInsert()
    } match {
      case Some(primaryKey: Long) => primaryKey
      case _ => throw new RuntimeException("Could not insert into database, no PK returned")
    }
  }

  val insertQueryString =
    """
INSERT INTO users (
      first_name,
      last_name,
      email,
      phone,
      comment,
      permission,
      pwd,
      image_url
    )
    values (
      {firstName},
      {lastName},
      {email},
      {phone},
      {comment},
      {permission},
      {pwd},
      {imageUrl}
    )
    """

  def update(id: Long, user: User) {
    if(user.password == NOT_CHANGED_PASSWORD) {
      updateWithoutPassword(id, user)
    } else {
      updateWithPassword(id, user)
    }
  }

  private def updateWithoutPassword(id: Long, user: User) {
    DB.withTransaction {
      implicit connection =>
        SQL(updateWithoutPasswordQueryString).on(
          'id -> id,
          'firstName -> user.firstName,
          'lastName -> user.lastName,
          'email -> user.email.toLowerCase,
          'phone -> user.phone,
          'comment -> user.comment,
          'permission -> user.permission.toString,
          'imageUrl -> user.imageUrl
        ).executeUpdate()
    }
  }

  val updateWithoutPasswordQueryString =
    """
UPDATE users
SET first_name = {firstName},
    last_name = {lastName},
    email = {email},
    phone = {phone},
    comment = {comment},
    permission = {permission},
    image_url = {imageUrl}
WHERE id = {id}
    """

  private def updateWithPassword(id: Long, user: User) {
    DB.withTransaction {
      implicit connection =>
        SQL(updateWithPasswordQueryString).on(
          'id -> id,
          'firstName -> user.firstName,
          'lastName -> user.lastName,
          'email -> user.email.toLowerCase,
          'phone -> user.phone,
          'comment -> user.comment,
          'permission -> user.permission.toString,
          'pwd -> AuthHelper.calculateHash(user.password),
          'imageUrl -> user.imageUrl
        ).executeUpdate()
    }
  }

  val updateWithPasswordQueryString =
    """
UPDATE users
SET first_name = {firstName},
    last_name = {lastName},
    email = {email},
    phone = {phone},
    comment = {comment},
    permission = {permission},
    pwd = {pwd},
    image_url = {imageUrl}
WHERE id = {id}
    """

  def updateImageUrl(id: Long, imageUrl: String) = {
    DB.withTransaction {
      implicit connection =>
        SQL("UPDATE users SET image_url = {imageUrl} WHERE id = {id}").on(
          'id -> id,
          'imageUrl -> imageUrl
        ).executeUpdate()
    }
  }

  def delete(id: Long) {
    DB.withTransaction {
      implicit connection => {
        SQL("DELETE FROM participations p WHERE p.userx={id}").on('id -> id).executeUpdate()
        SQL("DELETE FROM memberships m WHERE m.userx={id}").on('id -> id).executeUpdate()
        SQL("DELETE FROM users u WHERE u.id={id}").on('id -> id).executeUpdate()
      }
    }
  }

  def verifyUniqueEmail(userToVerify: User): Boolean = {
    val userInDb = User.findByEmail(userToVerify.email)
    userInDb.isEmpty || userToVerify.id.isDefined && (userInDb.get.id == userToVerify.id)
  }
}

