package models

import anorm.SqlParser._
import play.api.db.DB
import anorm._
import play.api.Play.current


case class Group(
                  id: Pk[Long] = NotAssigned,
                  name: String,
                  description: String = ""
                  )


object Group {
  val parser = {
    get[Pk[Long]]("id") ~
      get[String]("name") ~
      get[String]("description") map {
      case id ~ name ~ description =>
        Group(
          id = id,
          name = name,
          description = description
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
          'description -> group.description
        ).executeInsert()
    } match {
      case Some(primaryKey) => primaryKey
      case _ => throw new RuntimeException("Could not insert into database, no PK returned")
    }
  }

  val insertQueryString =
    """
INSERT INTO groups (
      name,
      description
    )
    values (
      {name},
      {description}
    )
    """

  def update(id: Long, group: Group) {
    DB.withConnection {
      implicit connection =>
        SQL(updateQueryString).on(
          'id -> id,
          'name -> group.name,
          'description -> group.description
        ).executeUpdate()
    }
  }

  val updateQueryString =
    """
UPDATE groups
SET name = {name},
    description = {description}
WHERE id = {id}
    """
}