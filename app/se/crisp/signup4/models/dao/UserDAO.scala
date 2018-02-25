package se.crisp.signup4.models.dao

import javax.inject.{Inject, Singleton}

import anorm.SqlParser.get
import anorm.{RowParser, SQL, ~}
import play.api.db.Database
import se.crisp.signup4.models.security.Permission
import se.crisp.signup4.models.{Event, User}
import se.crisp.signup4.util.AuthHelper

@Singleton
class UserDAO @Inject() (val database: Database,
                         val authHelper: AuthHelper){

  def system = User(firstName = "Systemet", lastName = "", email = "")

  import scala.language.postfixOps

  val NOT_CHANGED_PASSWORD = "Y2j1EsDUvc6V" // just a random string

  val parser: RowParser[User] = {
    get[Option[Long]]("id") ~
      get[String]("first_name") ~
      get[String]("last_name") ~
      get[String]("email") ~
      get[String]("phone") ~
      get[String]("comment") ~
      get[String]("permission") ~
      get[String]("pwd") ~
      get[String]("image_provider") ~
      get[Option[String]]("image_version") ~
      get[Option[String]]("provider_key") ~
      get[Option[String]]("auth_info") map {
      case id ~ firstName ~ lastName ~ email ~ phone ~ comment ~ permission ~ pwd ~ image_provider ~ image_version ~ provider_key ~ auth_info =>
        User(
          id = id,
          firstName = firstName,
          lastName = lastName,
          email = email.toLowerCase,
          phone = phone,
          comment = comment,
          permission = Permission.withName(permission),
          password = pwd,
          imageProvider = image_provider,
          imageVersion = image_version,
          providerKey = provider_key,
          authInfo = auth_info
        )
    }
  }

  def find(id: Long): User = {
    database.withTransaction {
      implicit connection =>
        SQL("select * from users where id={id}").on('id -> id).as(parser single)
    }
  }


  def findByEmail(email: String): Option[User] = {
    database.withTransaction {
      implicit connection =>
        SQL("select * from users where lower(email)={email}").on('email -> email.toLowerCase).as(parser singleOpt)
    }
  }

  def findByProviderKey(providerKey: String) : Option[User] = {
    database.withTransaction {
      implicit connection =>
        SQL("select * from users where provider_key={providerKey}").on('providerKey -> providerKey).as(parser singleOpt)
    }
  }

  def findByFirstName(firstName: String): Seq[User] = {
    database.withTransaction {
      implicit connection =>
        SQL("select * from users where first_name={first_name}").on('first_name -> firstName).as(parser *)
    }
  }


  def findUnregisteredMembers(event: Event): Seq[User] = {
    database.withTransaction {
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
    database.withTransaction {
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
    database.withTransaction {
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
    database.withTransaction {
      implicit connection =>
        SQL("select * from users u ORDER BY u.first_name, u.last_name").as(parser *).sorted
    }
  }

  def create(user: User): Long = {
    database.withTransaction {
      implicit connection =>
        val password = user.password match {
          case NOT_CHANGED_PASSWORD => "*"
          case "*" => "*"
          case _ => authHelper.calculateHash(user.password)
        }
        SQL(insertQueryString).on(
          'firstName -> user.firstName,
          'lastName -> user.lastName,
          'email -> user.email.toLowerCase,
          'phone -> user.phone,
          'comment -> user.comment,
          'permission -> user.permission.toString,
          'pwd -> password,
          'imageProvider -> user.imageProvider,
          'imageVersion -> user.imageVersion
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
      image_provider,
      image_version
    )
    values (
      {firstName},
      {lastName},
      {email},
      {phone},
      {comment},
      {permission},
      {pwd},
      {imageProvider},
      {imageVersion}
    )
    """

  def updateProperties(id: Long, user: User) {
    if(user.password == NOT_CHANGED_PASSWORD) {
      updatePropertiesWithoutPassword(id, user)
    } else {
      updatePropertiesWithPassword(id, user)
    }
  }

  private def updatePropertiesWithoutPassword(id: Long, user: User) {
    database.withTransaction {
      implicit connection =>
        SQL(updateWithoutPasswordQueryString).on(
          'id -> id,
          'firstName -> user.firstName,
          'lastName -> user.lastName,
          'email -> user.email.toLowerCase,
          'phone -> user.phone,
          'comment -> user.comment,
          'permission -> user.permission.toString
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
    permission = {permission}
WHERE id = {id}
    """

  private def updatePropertiesWithPassword(id: Long, user: User) {
    database.withTransaction {
      implicit connection =>
        SQL(updateWithPasswordQueryString).on(
          'id -> id,
          'firstName -> user.firstName,
          'lastName -> user.lastName,
          'email -> user.email.toLowerCase,
          'phone -> user.phone,
          'comment -> user.comment,
          'permission -> user.permission.toString,
          'pwd -> authHelper.calculateHash(user.password)
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
    pwd = {pwd}
WHERE id = {id}
    """

  def updateInfo(id: Long, imageProvider: String, imageVersion: Option[String] = None): Int = {
    database.withTransaction {
      implicit connection =>
        SQL("UPDATE users SET image_provider = {imageProvider}, image_version = {imageVersion} WHERE id = {id}").on(
          'id -> id,
          'imageProvider -> imageProvider,
          'imageVersion -> imageVersion
        ).executeUpdate()
    }
  }

  def updateAuthInfo(providerKey: String, authInfo: String): Int = {
    database.withTransaction {
      implicit connection =>
        SQL("UPDATE users SET auth_info = {authInfo} WHERE provider_key = {providerKey}").on(
          'providerKey -> providerKey,
          'authInfo -> authInfo
        ).executeUpdate()
    }
  }

  def updateProviderKey(email:String, providerKey: String): Int = {
    database.withTransaction {
      implicit connection =>
        SQL("UPDATE users SET provider_key = {providerKey} WHERE email = {email}").on(
          'providerKey -> providerKey,
          'email -> email
        ).executeUpdate()
    }
  }


  def delete(id: Long) {
    database.withTransaction {
      implicit connection => {
        SQL("DELETE FROM participations p WHERE p.userx={id}").on('id -> id).executeUpdate()
        SQL("DELETE FROM memberships m WHERE m.userx={id}").on('id -> id).executeUpdate()
        SQL("DELETE FROM users u WHERE u.id={id}").on('id -> id).executeUpdate()
      }
    }
  }

  def verifyUniqueEmail(userToVerify: User): Boolean = {
    val userInDb = findByEmail(userToVerify.email)
    userInDb.isEmpty || userToVerify.id.isDefined && (userInDb.get.id == userToVerify.id)
  }
}
