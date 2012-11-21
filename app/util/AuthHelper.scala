package util

import models.User
import play.api.{Play, Logger}
import models.security.{Permission, Administrator, NormalUser}

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
    require (user.email.length > 0)
    val adminEmail = Play.current.configuration.getString("signup.admin.email").getOrElse("")

    (user.email == adminEmail, permission) match {
      case (true, _) => true
      case (_, NormalUser) => true
      case _ => false
    }
  }

  def checkPassword(user : Option[User], password: String): Option[User] = {
    val adminPassword = Play.current.configuration.getString("signup.admin.password")
    val adminEmail = Play.current.configuration.getString("signup.admin.email")

    if (adminPassword.isDefined && adminEmail.isDefined && password == adminPassword.get) {
      user.filter(usr => usr.email == adminEmail.get)
    } else {
      None
    }
  }


}
