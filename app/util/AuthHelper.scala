package util

import models.User
import models.security.{Permission, Administrator}
import java.security.MessageDigest

object AuthHelper {
  def isAdmin(user: Option[User]):Boolean = {
    if (user.isDefined) {
      authorize(user.get, Administrator)
    } else {
      false
    }
  }

  def isLoggedIn(user: Option[User]): Boolean = {
    user.isDefined
  }

  def authorize(user: User, permission: Permission): Boolean = {
    user.permission == permission
  }

  def checkPassword(user : Option[User], enteredPassword: String): Option[User] = {
    user.filter(usr => usr.password.toLowerCase == calculateHash(enteredPassword))
  }


  def calculateHash(password: String): String = {
    val hash = MessageDigest.getInstance("MD5").digest((password + salt).getBytes).map("%02X".format(_)).mkString.toLowerCase
    hash
  }

  import play.api.Play.current
  private def salt = play.api.Play.configuration.getString("password.salt").getOrElse("")

}
