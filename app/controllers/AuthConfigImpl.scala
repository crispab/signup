package controllers

import jp.t2v.lab.play2.auth.AuthConfig
import play.api.Logger
import play.api.mvc.Results._
import play.api.mvc._

import scala.concurrent.{Future, ExecutionContext}
import scala.reflect._

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
   * A `ClassManifest` is used to retrieve an id from the Cache API.
   * Use something like this:
   */
  //val idManifest: ClassManifest[Id] = classManifest[Id]
  val idTag: ClassTag[Id] = classTag[Id]


  /**
   * The session timeout in seconds
   */
  val sessionTimeoutInSeconds: Int = 3600

  /**
   * A function that returns a `User` object from an `Id`.
   * You can alter the procedure to suit your application.
   */
  def resolveUser(id: Id)(implicit ctx: ExecutionContext): Future[Option[User]] =  {
    Future(Option(models.User.find(id)))
  }


  /**
   * Where to redirect the user after a successful login.
   */
  def loginSucceeded(request: RequestHeader)(implicit ctx: ExecutionContext): Future[Result] = {
    val uri = request.session.get("access_uri").getOrElse(routes.Application.index().url.toString)
    Logger.debug("Login succeeded. Redirecting to uri " + uri)
    Future.successful(Redirect(uri).withSession(request.session - "access_uri"))
  }

  /**
   * Where to redirect the user after logging out
   */
  def logoutSucceeded(request: RequestHeader)(implicit ctx: ExecutionContext): Future[Result] = {
    Future.successful(Redirect(routes.Application.index()))
  }

  /**
   * If the user is not logged in and tries to access a protected resource then redirct them as follows:
   */
  def authenticationFailed(request: RequestHeader)(implicit ctx: ExecutionContext): Future[Result] = {
    Future.successful(
      Redirect(routes.Application.loginForm())
      .withSession("access_uri" -> request.uri)
      .flashing(("error" , "Nädu, det här får du inte göra utan att logga in som administratör!"))
    )
  }

  /**
   * If authorization failed (usually incorrect password) redirect the user as follows:
   */
  def authorizationFailed(request: RequestHeader)(implicit ctx: ExecutionContext): Future[Result] = {
    Future.successful(Forbidden("Nädu, det här får du inte göra utan att logga in som administratör!"))
  }

  /**
   * A type that is defined by every action for authorization.
   * This sample uses the following trait:
   *
   * sealed trait Permission
   * case object Administrator extends Permission
   * case object NormalUser extends Permission
   */
  type Authority = User => Future[Boolean]


  /**
   * A function that determines what `Authority` a user has.
   * You should alter this procedure to suit your application.
   */
  def authorize(user: User, authorityFunction: Authority)(implicit ctx: ExecutionContext): Future[Boolean] = authorityFunction(user)
}

