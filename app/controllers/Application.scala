package controllers

import play.api.mvc._
import jp.t2v.lab.play20.auth.LoginLogout
import play.api.data.Form
import models.User
import play.api.data.Forms._

object Application extends Controller with LoginLogout with AuthConfigImpl {

  def index = Action {
    Ok(views.html.index())
  }

  def loginForm = Action {
    Ok(views.html.login(loginDataForm))
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
    checkPassword(user, password)
  }

  def checkPassword(user : Option[models.User], password: String): Option[models.User] = {
     user.filter { usr => password == "123" }
  }


  def fromUser(user: Option[models.User]): Option[(String, String)] = {
    if(user.isDefined) {
      Option((user.get.email, ""))
    } else {
      Option.empty
    }
  }

  case class LoginDataForm(email : String, password : String)
}