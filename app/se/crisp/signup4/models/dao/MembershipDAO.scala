package se.crisp.signup4.models.dao

import javax.inject.{Inject, Singleton}

import play.api.Play.current
import anorm.SqlParser.get
import anorm.{RowParser, SQL, ~}
import play.api.db.DB
import se.crisp.signup4.models.{Group, Membership}

@Singleton
class MembershipDAO @Inject() (val userDAO: UserDAO,
                               val groupDAO: GroupDAO) {
  import scala.language.postfixOps

  def create(membership: Membership): Long = {
    create(membership.group.id.get, membership.user.id.get)
  }

  def create(groupId: Long, userId: Long): Long = {
    DB.withTransaction {
      implicit connection =>
        SQL(insertQueryString).on(
          'group -> groupId,
          'user -> userId
        ).executeInsert()
    } match {
      case Some(primaryKey: Long) => primaryKey
      case _ => throw new RuntimeException("Could not insert into database, no PK returned")
    }
  }

  val insertQueryString =
    """
INSERT INTO memberships (
  groupx,
  userx
) VALUES (
  {group},
  {user}
)
    """

  val parser: RowParser[Membership] = {
    get[Option[Long]]("id") ~
      get[Long]("groupx") ~
      get[Long]("userx") map {
      case id ~ groupx ~ userx =>
        Membership(
          id = id,
          group = groupDAO.find(groupx),
          user = userDAO.find(userx)
        )
    }
  }

  def find(id: Long): Membership = {
    DB.withTransaction {
      implicit connection =>
        SQL("SELECT * FROM memberships WHERE id={id}").on('id -> id).as(parser single)
    }
  }

  def findAll(): Seq[Membership] = {
    DB.withTransaction {
      implicit connection =>
        SQL("SELECT * FROM memberships").as(parser *).sorted
    }
  }

  def findMembers(group: Group): Seq[Membership] = {
    DB.withTransaction {
      implicit connection =>
        SQL("SELECT m.* FROM memberships m, users u WHERE m.userx=u.id AND m.groupx={groupId}").on('groupId -> group.id.get).as(parser *).sorted
    }
  }

  def delete(id: Long) {
    DB.withTransaction {
      implicit connection => {
        SQL("DELETE FROM memberships m WHERE m.id={id}").on('id -> id).executeUpdate()
      }
    }

  }


}
