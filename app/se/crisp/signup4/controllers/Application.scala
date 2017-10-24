package se.crisp.signup4.controllers

import javax.inject.Inject

import jp.t2v.lab.play2.auth.{LoginLogout, OptionalAuthElement}
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc._
import se.crisp.signup4.models
import se.crisp.signup4.models.User
import se.crisp.signup4.util.AuthHelper

import scala.concurrent.{ExecutionContext, Future}
import ExecutionContext.Implicits.global

class Application @Inject() (val messagesApi: MessagesApi) extends Controller with LoginLogout with OptionalAuthElement with AuthConfigImpl  with I18nSupport{

  def index: Action[AnyContent] = StackAction { implicit request =>
    Ok(se.crisp.signup4.views.html.index())
  }

  def loginForm: Action[AnyContent] = Action { implicit request =>
    if (request.session.get("access_uri").isEmpty && request.headers.get(REFERER).isDefined) {
      Logger.debug("Using REFERER URL: " + request.headers.get(REFERER).get)
      Ok(se.crisp.signup4.views.html.login(loginDataForm)).withSession("access_uri" -> request.headers.get(REFERER).get)
    } else {
      Ok(se.crisp.signup4.views.html.login(loginDataForm))
    }
  }

  def authenticate: Action[AnyContent] = Action.async { implicit request =>
    loginDataForm.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(se.crisp.signup4.views.html.login(formWithErrors))),
      user => gotoLoginSucceeded(user.get.id.get)
    )
  }

  val loginDataForm: Form[Option[User]] = Form(
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

  def logout: Action[AnyContent] = Action.async { implicit request =>
    gotoLogoutSucceeded.map(_.flashing(
      "success" -> Messages("application.logout")
    ))
  }

  def redirectToHttps: Action[AnyContent] = Action { implicit request =>
    MovedPermanently("https://" + request.host + request.uri)
  }

}