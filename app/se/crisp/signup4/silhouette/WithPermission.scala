package se.crisp.signup4.silhouette

import com.mohiva.play.silhouette.api.Authorization
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import play.api.mvc.Request
import se.crisp.signup4.models.User
import se.crisp.signup4.models.security.Permission

import scala.concurrent.Future
import scala.concurrent.Future.successful

case class WithPermission(permission: Permission) extends Authorization[User, CookieAuthenticator] {
  def isAuthorized[A](user: User, authenticator: CookieAuthenticator)(implicit r: Request[A]): Future[Boolean] = successful {user.permission == permission}
}
