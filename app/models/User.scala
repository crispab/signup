package models
 
import play.api.db._
import play.api.Play.current
 
import anorm._
import anorm.SqlParser._
 
case class User(
    id: Pk[Long] = NotAssigned,
    firstName: String,
    nickName: String = "",
    lastName: String = "",
    primaryEmail: String = "",
    secondaryEmail: String = "",
    mobileNr: String = "",
    comment: String = ""
)
 
object User {
 
  val parser = {
    get[Pk[Long]]("id") ~
    get[String]("first_name") ~
    get[String]("nick_name") ~
    get[String]("last_name") ~
    get[String]("primary_email") ~
    get[String]("secondary_email") ~
    get[String]("mobile_nr") ~
    get[String]("comment") map {
      case id~firstName~nickName~lastName~primaryEmail~secondaryEmail~mobileNr~comment => 
        User(
            id = id, 
            firstName = firstName,
            nickName = nickName,
            lastName = lastName,
            primaryEmail = primaryEmail,
            secondaryEmail = secondaryEmail,
            mobileNr = mobileNr,
            comment = comment
            
        )
    }
  }
  
  def find(id : Long): Seq[User] = {
    DB.withConnection { implicit connection =>
      SQL("select * from users where id={id}").on('id -> id).as(User.parser *)
    }
  }
 
  def findAll(): Seq[User] = {
    DB.withConnection { implicit connection =>
      SQL("select * from users").as(User.parser *)
    }
  }
 
  def create(user: User): Unit = {
    DB.withConnection { implicit connection =>
      SQL(insertQueryString).on(
        'firstName -> user.firstName,
        'nickName -> user.nickName,
        'lastName -> user.lastName,
        'primaryEmail -> user.primaryEmail,
        'secondaryEmail -> user.secondaryEmail,
        'mobileNr -> user.mobileNr,
        'comment -> user.comment
      ).executeUpdate()
    }
  }
  
  val insertQueryString =
    """
INSERT INTO users (
	  first_name,
      nick_name,
	  last_name,
      primary_email,
      secondary_email,
	  mobile_nr,
      comment
    )
    values (
      {firstName},
      {nickName},
      {lastName},
      {primaryEmail},
      {secondaryEmail},
      {mobileNr},
      {comment}
    )      
    """
 
}

