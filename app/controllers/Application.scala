package controllers

import play.api.mvc._
import jp.t2v.lab.play2.auth.{Auth, LoginLogout}
import play.api.data.Form
import models.User
import play.api.data.Forms._
import play.api.Logger
import util.AuthHelper

object Application extends Controller with LoginLogout with Auth with AuthConfigImpl with Https {

  def index = optionalUserAction { implicit user => implicit request =>
    Ok(views.html.index())
  }

  def loginForm = httpsAction { implicit request =>
    if (request.session.get("access_uri").isEmpty && request.headers.get(REFERER).isDefined) {
      Logger.debug("Using REFERER URL: " + request.headers.get(REFERER).get)
      Ok(views.html.login(loginDataForm)).withSession("access_uri" -> request.headers.get(REFERER).get)
    } else {
      Ok(views.html.login(loginDataForm))
    }
  }

  def authenticate = Action { implicit request =>
    loginDataForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.login(formWithErrors)),
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

  def logout = Action { implicit request =>
    gotoLogoutSucceeded
  }
}