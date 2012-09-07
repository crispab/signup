package controllers

import play.api.mvc._
import jp.t2v.lab.play20.auth.LoginLogout
import play.api.data.Form
import models.{User, Group}
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
      form => gotoLoginSucceeded(-5L) // TODO: Replace hard coded id of John Doe
    )
  }

  val loginDataForm: Form[LoginDataForm] = Form(
    mapping(
      "email" -> nonEmptyText,
      "password" -> nonEmptyText
    )(LoginDataForm.apply)(LoginDataForm.unapply)
  )

  case class LoginDataForm(email : String, password : String)
}