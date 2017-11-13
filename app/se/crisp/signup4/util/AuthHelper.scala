package se.crisp.signup4.util

import java.security.MessageDigest
import javax.inject.{Inject, Singleton}

import play.api.Configuration
import se.crisp.signup4.models.security.{Administrator, Permission}
import se.crisp.signup4.models.User

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

@Singleton
class AuthHelper @Inject() (configuration: Configuration) {

  import ExecutionContext.Implicits.global
  def hasPermission(permission: Permission)(loggedInUser: User): Future[Boolean]
    = Future(authorize(loggedInUser, permission))

  def hasPermissionOrSelf(permission: Permission, userId: Long)(loggedInUser: User): Future[Boolean]
    = Future(authorize(loggedInUser, permission) || (userId == loggedInUser.id.get))

  def hasPermissionOrSelf(permission: Permission, userId: Option[Long])(loggedInUser: User): Future[Boolean]
    = Future(authorize(loggedInUser, permission) || (userId.isDefined && (userId.get == loggedInUser.id.get)))

  def isAdmin(user: Option[User]):Boolean = {
    if (user.isDefined) {
      authorize(user.get, Administrator)
    } else {
      false
    }
  }

  def isAdminOrSelf(loggedInUser: Option[User], viewedUser: User):Boolean = {
    if (loggedInUser.isDefined) {
      authorize(loggedInUser.get, Administrator) || loggedInUser.get.id.get == viewedUser.id.get
    } else {
      false
    }
  }

  def isLoggedIn(user: Option[User]): Boolean = {
    user.isDefined
  }

  def authorize(user: User, permission: Permission): Boolean = {
    user.permission match {
      case Administrator => true
      case _ => user.permission == permission
    }
  }

  def checkPassword(user : Option[User], enteredPassword: String): Option[User] = {
    user.filter(usr => usr.password.toLowerCase == calculateHash(enteredPassword))
  }


  def calculateHash(password: String): String = {
    val hash = MessageDigest.getInstance("MD5").digest((password + salt).getBytes).map("%02X".format(_)).mkString.toLowerCase
    hash
  }

  private def salt = configuration.getString("password.salt").getOrElse("")

  def randomPassword: String = Random.alphanumeric.take(12).mkString

}
