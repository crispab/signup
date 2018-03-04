package se.crisp.signup4.silhouette

import java.security.MessageDigest
import javax.inject.{Inject, Singleton}

import com.mohiva.play.silhouette.api.util.{PasswordHasher, PasswordInfo}
import play.api.Configuration

@Singleton
class MD5PasswordHasher @Inject() (configuration: Configuration) extends PasswordHasher {
  override def id: String = "md5"

  override def hash(plainPassword: String): PasswordInfo =
    PasswordInfo(id, calculateHash(plainPassword), Option(salt))

  override def matches(passwordInfo: PasswordInfo, suppliedPassword: String): Boolean = passwordInfo.password.equals(calculateHash(suppliedPassword))

  override def isDeprecated(passwordInfo: PasswordInfo): Option[Boolean] = Some(false)

  def calculateHash(password: String): String = {
   MessageDigest.getInstance("MD5").digest((password + salt).getBytes).map("%02X".format(_)).mkString.toLowerCase
  }

  private lazy val salt = configuration.get[String]("password.salt")
}
