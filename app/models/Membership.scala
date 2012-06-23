package models

import anorm.SqlParser._
import play.api.db.DB
import anorm._
import play.api.Play.current

case class Membership(id: Pk[Long] = NotAssigned,
                      group: Group,
                      user: User)

object Membership {

  def create(membership: Membership) {
    DB.withConnection {
      implicit connection =>
        SQL(insertQueryString).on(
          'group -> membership.group.id,
          'user -> membership.user.id
        ).executeUpdate()
    }
  }

  val insertQueryString =
    """
INSERT INTO memberships (
  group,
  userx
) VALUES (
  {group},
  {user}
)
    """

  val parser = {
    get[Pk[Long]]("id") ~
    get[Long]("group") ~
    get[Long]("userx") map {
    case id ~ group ~ userx  =>
      Membership(
        id = id,
        group = Group.find(group),
        user = User.find(userx)
      )
    }
  }

  def find(id: Long): Membership = {
    DB.withConnection {
      implicit connection =>
        SQL("SELECT * FROM memberships WHERE id={id}").on('id -> id).as(Membership.parser *).head
    }
  }

  def findAll(): Seq[Membership] = {
    DB.withConnection {
      implicit connection =>
        SQL("SELECT * FROM memberships").as(parser *)
    }
  }
}
