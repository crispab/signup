package controllers

import play.api.mvc._

object Application extends Controller {

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