package controllers

import play.api.mvc._
import play.api.data.Forms.{mapping, ignored, nonEmptyText, text}
import models.User
import anorm.{Pk, NotAssigned}
import play.api.data.Form

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
    Ok(views.html.users.edit(userForm))
  }

  def updateForm(id: Long) = Action {
    val user = User.find(id)
    Ok(views.html.users.edit(userForm.fill(user), Option(id)))
  }

  def create = Action {
    implicit request =>
      userForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.users.edit(formWithErrors)),
        user => {
          User.create(user)
          Redirect(routes.Users.list())
        }
      )
  }

  def update(id: Long) = Action {
    implicit request =>
      userForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.users.edit(formWithErrors, Option(id))),
        user => {
          User.update(id, user)
          Redirect(routes.Users.show(id))
        }
      )
  }

  def delete(id: Long) = Action {
    User.delete(id)
    Redirect(routes.Users.list())
  }

  val userForm: Form[User] = Form(
    mapping(
      "id" -> ignored(NotAssigned:Pk[Long]),
      "firstName" -> nonEmptyText,
      "lastName" -> nonEmptyText,
      "email" -> play.api.data.Forms.email,
      "phone" -> text,
      "comment" -> text
    )(User.apply)(User.unapply)
  )

}

