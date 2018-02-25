package se.crisp.signup4.models

import com.mohiva.play.silhouette.api.Identity
import se.crisp.signup4.models.security.{NormalUser, Permission}

case class User(
   id: Option[Long] = None,
   firstName: String,
   lastName: String,
   email: String,
   phone: String = "",
   comment: String = "",
   permission: Permission = NormalUser,
   password: String = "*",
   imageProvider: String = "",
   imageVersion: Option[String] = None,
   providerKey:Option[String] = None,
   authInfo:Option[String] = None) extends Identity with Ordered[User] {

  def compare(that: User): Int = {
    val c = this.firstName.compare(that.firstName)
    if (c != 0) {
      c
    } else {
      this.lastName.compare(that.lastName)
    }
  }

  def name: String = firstName + " " + lastName
}



