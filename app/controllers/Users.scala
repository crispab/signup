package controllers

import play.api.mvc._
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms.{tuple, nonEmptyText, text, optional}
import models.User
import anorm.NotAssigned

object Users extends Controller {

  def list = Action {
    val users = User.findAll()
    Ok(views.html.users.list(users))
  }
  
  def show(id : Long) = Action {
    val user = User.find(id)
    Ok(views.html.users.show(user))
  }

  def createForm = Action {
    val user = new User(id = NotAssigned,
                        firstName = "",
                        lastName = "",
                        email = "")
    Ok(views.html.users.edit(user, creating = true))
  }

  def updateForm(id: Long) = Action {
    val user = User.find(id)
    Ok(views.html.users.edit(user, creating = false))
  }

  def create = Action {
    implicit request =>
      userForm.bindFromRequest.fold(
      failingForm => {
        Logger.info("Errors: " + failingForm.errors)
        Redirect(routes.Users.createForm())
      }, {
        case (firstName, lastName, email, phone, comment) => {
          User.create(User(
            firstName = firstName,
            lastName = lastName,
            email = email,
            phone = phone.getOrElse(""),
            comment = comment.getOrElse("")
          ))
          Redirect(routes.Users.list())
        }
      }
    )
  }

  def update(id: Long) = Action {
    implicit request =>
      userForm.bindFromRequest.fold(
      failingForm => {
        Logger.info("Errors: " + failingForm.errors)
        Redirect(routes.Users.createForm())
      }, {
        case (firstName, lastName, email, phone, comment) => {
          User.update(id, User(
            firstName = firstName,
            lastName = lastName,
            email = email,
            phone = phone.getOrElse(""),
            comment = comment.getOrElse("")
          ))
          Redirect(routes.Users.show(id))
        }
      }
    )
  }

  def delete(id: Long) = Action {
    User.delete(id)
    Redirect(routes.Users.list())
  }

  val userForm = Form(
    tuple(
      "firstName" -> nonEmptyText,
      "lastName" -> nonEmptyText,
      "email" -> play.api.data.Forms.email,
      "phone" -> optional(text),
      "comment" -> optional(text)
    )
  )
}

