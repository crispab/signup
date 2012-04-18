package controllers

import play.api._
import play.api.mvc._
import models.User

object Application extends Controller {
  
  def index = Action {
    val user = new User(null, "Nisse")
    User.create(user)
    Ok(views.html.index("Created user"))
  }
  
  def show = Action {
    val users = User.findAll()
    Ok(views.html.index("Found users: " + users))
  }
  
}