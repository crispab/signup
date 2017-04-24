package controllers

import jp.t2v.lab.play2.auth.{LoginLogout, OptionalAuthElement}
import models.User
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.Messages
import play.api.mvc._
import util.AuthHelper

import scala.concurrent.{ExecutionContext, Future}
import ExecutionContext.Implicits.global

object Application extends Controller with LoginLogout with OptionalAuthElement with AuthConfigImpl with Https {

  def index = StackAction { implicit request =>
    Ok(views.html.index())
  }

  def loginForm: Action[AnyContent] = httpsAction { implicit request =>
    if (request.session.get("access_uri").isEmpty && request.headers.get(REFERER).isDefined) {
      Logger.debug("Using REFERER URL: " + request.headers.get(REFERER).get)
      Ok(views.html.login(loginDataForm)).withSession("access_uri" -> request.headers.get(REFERER).get)
    } else {
      Ok(views.html.login(loginDataForm))
    }
  }

  def authenticate: Action[AnyContent] = Action.async { implicit request =>
    loginDataForm.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(views.html.login(formWithErrors))),
      user => gotoLoginSucceeded(user.get.id.get)
    )
  }

  val loginDataForm: Form[Option[models.User]] = Form(
    mapping(
      "email" -> nonEmptyText,
      "password" -> nonEmptyText
    )(toUser)(fromUser).verifying(user => user.isDefined)
  )

  def toUser(email: String, password: String): Option[models.User] = {
    val user = User.findByEmail(email.trim)
    AuthHelper.checkPassword(user, password)
  }

  def fromUser(user: Option[models.User]): Option[(String, String)] = {
    if(user.isDefined) {
      Option((user.get.email, ""))
    } else {
      Option.empty
    }
  }

  case class LoginDataForm(email : String, password : String)

  def logout: Action[AnyContent] = Action.async { implicit request =>
    gotoLogoutSucceeded.map(_.flashing(
      "success" -> Messages("application.logout")
    ))
  }
}