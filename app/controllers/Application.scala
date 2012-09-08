package controllers

import play.api.mvc._
import jp.t2v.lab.play20.auth.LoginLogout
import play.api.data.Form
import models.{Membership, User, Group}
import play.api.data.Forms._
import anorm.{Pk, NotAssigned}

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
      user => gotoLoginSucceeded(user.id.getOrElse(0L))
    )
  }

  val loginDataForm: Form[models.User] = Form(
    mapping(
      "email" -> nonEmptyText,
      "password" -> nonEmptyText
    )(toUser)(fromUser)
  )

  def toUser(email: String, password: String): models.User = {
    User.find(-5L) // TODO: Replace hard coded id of John Doe
  }

  def fromUser(user: models.User) = {
    Option((user.email, ""))
  }

  case class LoginDataForm(email : String, password : String)
}