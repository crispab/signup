package controllers

import play.api._
import play.api.mvc._
import models.User
import anorm.NotAssigned

object Application extends Controller {
  
  def index = Action {
    Ok(views.html.index("SignUp"))
  }
}