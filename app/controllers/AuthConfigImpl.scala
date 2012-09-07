package controllers

import jp.t2v.lab.play20.auth.AuthConfig
import models.security.{NormalUser, Administrator, Permission}
import play.api.mvc._
import play.api.mvc.Results._

trait AuthConfigImpl extends AuthConfig {

  /**
   * A type that is used to identify a user.
   */
  type Id = Long

  /**
   * A type that represents a user in your application.
   */
  type User = models.User

  /**
   * A type that is defined by every action for authorization.
   * This sample uses the following trait:
   *
   * sealed trait Permission
   * case object Administrator extends Permission
   * case object NormalUser extends Permission
   */
  type Authority = Permission

  /**
   * A `ClassManifest` is used to retrieve an id from the Cache API.
   * Use something like this:
   */
  val idManifest: ClassManifest[Id] = classManifest[Id]

  /**
   * The session timeout in seconds
   */
  val sessionTimeoutInSeconds: Int = 3600

  /**
   * A function that returns a `User` object from an `Id`.
   * You can alter the procedure to suit your application.
   */
  def resolveUser(id: Id): Option[User] = Option(models.User.find(id))

  /**
   * Where to redirect the user after a successful login.
   */
  def loginSucceeded[A](request: Request[A]): PlainResult = Redirect(routes.Application.index)

  /**
   * Where to redirect the user after logging out
   */
  def logoutSucceeded[A](request: Request[A]): PlainResult = Redirect(routes.Application.index)

  /**
   * If the user is not logged in and tries to access a protected resource then redirct them as follows:
   */
  def authenticationFailed[A](request: Request[A]): PlainResult = Redirect(routes.Application.loginForm)

  /**
   * If authorization failed (usually incorrect password) redirect the user as follows:
   */
  def authorizationFailed[A](request: Request[A]): PlainResult = Forbidden("no permission")

  /**
   * A function that determines what `Authority` a user has.
   * You should alter this procedure to suit your application.
   */
  def authorize(user: User, authority: Authority): Boolean =
    (user.email == "john@doe.net", authority) match {   // TODO: Replace hard coded email
      case (true, _) => true
      case (false, NormalUser) => true
      case (false, Administrator) => false
      case _ => false
    }
}

