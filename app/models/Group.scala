package models

import anorm.SqlParser._
import play.api.db.DB
import anorm._
import play.api.Play.current


case class Group(
                  id: Pk[Long] = NotAssigned,
                  name: String,
                  description: String = "",
                  mail_from: String = ""
                  )

object Group {
  val parser = {
    get[Pk[Long]]("id") ~
    get[String]("name") ~
    get[String]("description") ~
    get[String]("mail_from") map {
    case id ~ name ~ description ~ mail_from =>
      Group(
        id = id,
        name = name,
        description = description,
        mail_from = mail_from
      )
    }
  }

  def findAll(): Seq[Group] = {
    DB.withConnection {
      implicit connection =>
        SQL("SELECT * FROM groups ORDER BY name ASC").as(Group.parser *)
    }
  }

  def find(id: Long): Group = {
    DB.withConnection {
      implicit connection =>
        SQL("SELECT * FROM groups g WHERE g.id={id}").on('id -> id).as(Group.parser single)
    }
  }


  def create(group: Group): Long = {
    DB.withConnection {
      implicit connection =>
        SQL(insertQueryString).on(
          'name -> group.name,
          'description -> group.description,
          'mail_from -> group.mail_from
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
      mail_from
    )
    values (
      {name},
      {description},
      {mail_from}
    )
    """


  def update(id: Long, group: Group) {
    DB.withConnection {
      implicit connection =>
        SQL(updateQueryString).on(
          'id -> id,
          'name -> group.name,
          'description -> group.description,
          'mail_from -> group.mail_from
        ).executeUpdate()
    }
  }

  val updateQueryString =
    """
UPDATE groups
SET name = {name},
    description = {description},
    mail_from = {mail_from}
WHERE id = {id}
    """
}