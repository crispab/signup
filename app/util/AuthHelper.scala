package util

import models.User
import play.api.Play
import play.api.Logger
import models.security.{Permission, Administrator, NormalUser}
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

  def checkPassword(user : Option[User], password: String): Option[User] = {
    user.filter(usr => usr.password.toLowerCase == md5(password))
  }


  def md5(s: String): String = {
    val hash = MessageDigest.getInstance("MD5").digest(s.getBytes).map("%02X".format(_)).mkString.toLowerCase
    Logger.debug("MD5 of '" + s + "' is '" + hash + "'")
    hash
  }

}
