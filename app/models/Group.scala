package models

import anorm.SqlParser._
import play.api.db.DB
import anorm._
import play.api.Play.current


case class Group(
                  id: Pk[Long] = NotAssigned,
                  name: String,
                  description: String = "",
                  mailFrom: String = "",
                  mailSubjectPrefix: String = ""
                  )

object Group {
  import scala.language.postfixOps
  val parser = {
    get[Pk[Long]]("id") ~
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
        SQL("SELECT * FROM groups ORDER BY name ASC").as(Group.parser *)
    }
  }

  def find(id: Long): Group = {
    DB.withTransaction {
      implicit connection =>
        SQL("SELECT * FROM groups g WHERE g.id={id}").on('id -> id).as(Group.parser single)
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
      |mail_subject_prefix
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
}