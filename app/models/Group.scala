package models

import anorm.SqlParser._
import play.api.db.DB
import anorm._
import play.api.Play.current


case class Group(
                  id: Pk[Long] = NotAssigned,
                  name: String,
                  description: String = "",
                  smtp_host: String = "",
                  smtp_port: Int = 25,
                  smtp_useSsl: Boolean = false,
                  smtp_useTls: Boolean = false,
                  smtp_user: String = "",
                  smtp_password: String = "",
                  smtp_from: String = ""
                  )

object Group {
  val parser = {
    get[Pk[Long]]("id") ~
    get[String]("name") ~
    get[String]("description") ~
    get[String]("smtp_host") ~
    get[Int]("smtp_port") ~
    get[Boolean]("smtp_useSsl") ~
    get[Boolean]("smtp_useTls") ~
    get[String]("smtp_user") ~
    get[String]("smtp_password") ~
    get[String]("smtp_from") map {
    case id ~ name ~ description ~ smtp_host ~ smtp_port ~ smtp_useSsl ~ smtp_useTls ~ smtp_user ~ smtp_password ~ smtp_from =>
      Group(
        id = id,
        name = name,
        description = description,
        smtp_host = smtp_host,
        smtp_port = smtp_port,
        smtp_useSsl = smtp_useSsl,
        smtp_useTls = smtp_useTls,
        smtp_user = smtp_user,
        smtp_password = smtp_password,
        smtp_from = smtp_from
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
          'smtp_host -> group.smtp_host,
          'smtp_port -> group.smtp_port,
          'smtp_useSsl -> group.smtp_useSsl,
          'smtp_useTls -> group.smtp_useTls,
          'smtp_user -> group.smtp_user,
          'smtp_password -> group.smtp_password,
          'smtp_from -> group.smtp_from
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
      smtp_host,
      smtp_port,
      smtp_useSsl,
      smtp_useTls,
      smtp_user,
      smtp_password,
      smtp_from
    )
    values (
      {name},
      {description},
      {smtp_host},
      {smtp_port},
      {smtp_useSsl},
      {smtp_useTls},
      {smtp_user},
      {smtp_password},
      {smtp_from}
    )
    """


  def update(id: Long, group: Group) {
    DB.withConnection {
      implicit connection =>
        SQL(updateQueryString).on(
          'id -> id,
          'name -> group.name,
          'description -> group.description,
          'smtp_host -> group.smtp_host,
          'smtp_port -> group.smtp_port,
          'smtp_useSsl -> group.smtp_useSsl,
          'smtp_useTls -> group.smtp_useTls,
          'smtp_user -> group.smtp_user,
          'smtp_password -> group.smtp_password,
          'smtp_from -> group.smtp_from
        ).executeUpdate()
    }
  }

  val updateQueryString =
    """
UPDATE groups
SET name = {name},
    description = {description},
    smtp_host = {smtp_host},
    smtp_port = {smtp_port},
    smtp_useSsl = {smtp_useSsl},
    smtp_useTls = {smtp_useTls},
    smtp_user = {smtp_user},
    smtp_password = {smtp_password},
    smtp_from = {smtp_from}
WHERE id = {id}
    """
}