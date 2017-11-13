package se.crisp.signup4.models.dao

import javax.inject.Singleton

import play.api.Play.current
import anorm.SqlParser.get
import anorm.{RowParser, SQL, ~}
import com.google.inject.Inject
import play.api.db.DB
import se.crisp.signup4.models.Group

@Singleton
class GroupDAO @Inject() () {
  import scala.language.postfixOps
  val parser: RowParser[Group] = {
    get[Option[Long]]("id") ~
    get[String]("name") ~
    get[String]("description") ~
    get[String]("mail_from") ~
    get[String]("mail_subject_prefix") map {
    case id ~ name ~ description ~ mail_from ~ mail_subject_prefix =>
      Group(
        id = id,
        name = name,
        description = description,
        mailFrom = mail_from,
        mailSubjectPrefix = mail_subject_prefix
      )
    }
  }

  def findAll(): Seq[Group] = {
    DB.withTransaction {
      implicit connection =>
        SQL("SELECT * FROM groups ORDER BY name ASC").as(parser *)
    }
  }

  def find(id: Long): Group = {
    DB.withTransaction {
      implicit connection =>
        SQL("SELECT * FROM groups g WHERE g.id={id}").on('id -> id).as(parser single)
    }
  }


  def findByName(name: String): Option[Group] = {
    DB.withTransaction {
      implicit connection =>
        SQL("SELECT * FROM groups g WHERE g.name={name}").on('name -> name).as(parser singleOpt)
    }
  }


  def create(group: Group): Long = {
    DB.withTransaction {
      implicit connection =>
        SQL(insertQueryString).on(
          'name -> group.name,
          'description -> group.description,
          'mail_from -> group.mailFrom,
          'mail_subject_prefix -> group.mailSubjectPrefix
        ).executeInsert()
    } match {
      case Some(primaryKey: Long) => primaryKey
      case _ => throw new RuntimeException("Could not insert into database, no PK returned")
    }
  }

  val insertQueryString =
    """
INSERT INTO groups (
      name,
      description,
      mail_from,
      mail_subject_prefix
    )
    values (
      {name},
      {description},
      {mail_from},
      {mail_subject_prefix}
    )
    """


  def update(id: Long, group: Group) {
    DB.withTransaction {
      implicit connection =>
        SQL(updateQueryString).on(
          'id -> id,
          'name -> group.name,
          'description -> group.description,
          'mail_from -> group.mailFrom,
          'mail_subject_prefix -> group.mailSubjectPrefix
        ).executeUpdate()
    }
  }

  val updateQueryString =
    """
UPDATE groups
SET name = {name},
    description = {description},
    mail_from = {mail_from},
    mail_subject_prefix = {mail_subject_prefix}
WHERE id = {id}
    """


  def delete(id: Long) {
    DB.withTransaction {
      implicit connection => {
        SQL("DELETE FROM memberships m WHERE m.groupx={id}").on('id -> id).executeUpdate()
        SQL("DELETE FROM participations p WHERE p.event IN (SELECT id FROM events WHERE groupx={id})").on('id -> id).executeUpdate()
        SQL("DELETE FROM log_entries l WHERE l.event IN (SELECT e.id FROM events e WHERE e.groupx = {id})").on('id -> id).executeUpdate()
        SQL("DELETE FROM reminders r WHERE r.event IN (SELECT e.id FROM events e WHERE e.groupx = {id})").on('id -> id).executeUpdate()
        SQL("DELETE FROM events e WHERE e.groupx={id}").on('id -> id).executeUpdate()
        SQL("DELETE FROM groups g WHERE g.id={id}").on('id -> id).executeUpdate()
      }
    }
  }

}
