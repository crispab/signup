package models
 
import play.api.db._
import play.api.Play.current
 
import anorm._
import anorm.SqlParser._
 
case class User(id: Pk[Long], name: String)
 
object User {
 
  val simple = {
    get[Pk[Long]]("id") ~
    get[String]("name") map {
      case id~name => User(id, name)
    }
  }
 
  def findAll(): Seq[User] = {
    DB.withConnection { implicit connection =>
      SQL("select * from users").as(User.simple *)
    }
  }
 
  def create(user: User): Unit = {
    DB.withConnection { implicit connection =>
      SQL("insert into users(name) values ({name})").on(
        'name -> user.name
      ).executeUpdate()
    }
  }
 
}

