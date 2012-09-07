package controllers

import play.api.mvc._
import jp.t2v.lab.play20.auth.LoginLogout

object Application extends Controller  with LoginLogout with AuthConfigImpl {

  def index = Action {
    Ok(views.html.index())
  }

  def loginForm = Action {
    Ok(views.html.login())
  }

  def login = Action {
    Ok(views.html.index("Janne"))
  }
}