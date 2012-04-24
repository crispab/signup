package controllers

import play.api._
import play.api.mvc._
import models.User
import anorm.NotAssigned

object Users extends Controller {
  
  def create = Action {
    val user = new User(NotAssigned, "Nisse")
    User.create(user)
    Ok(views.html.index("Created user"))
  }
  
  def list = Action {
    val users = User.findAll()
    Ok(views.html.users_list("Found users: " + users))
  }
  
  def createForm = Action {
    val user = new User(NotAssigned, "")
    Ok(views.html.users_editForm(user))
  }
  

  
}