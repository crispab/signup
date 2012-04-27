package controllers

import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms.{tuple, nonEmptyText, text, email}
import models.User
import anorm.NotAssigned

object Users extends Controller {

  def list = Action {
    val users = User.findAll()
    Ok(views.html.users.list("Found users: " + users))
  }

  def createForm = Action {
    val user = new User(NotAssigned, "")
    Ok(views.html.users.edit(user, newUser = true))
  }

  def create = Action {
    implicit request =>
      userForm.bindFromRequest.fold(
      errors => BadRequest, {
        case (firstName, nickName, lastName, primaryEmail, secondaryEmail, mobileNr, comment) => {
          User.create(User(
            firstName = firstName,
            nickName = nickName,
            lastName = lastName,
            primaryEmail = primaryEmail,
            secondaryEmail = secondaryEmail,
            mobileNr = mobileNr,
            comment = comment
          ))
          Redirect(routes.Users.list)
        }
      }
      )
  }

  val userForm = Form(
    tuple(
      "firstName" -> nonEmptyText,
      "nickName" -> text,
      "lastName" -> text,
      "primaryEmail" -> email,
      "secondaryEmail" -> email,
      "mobileNr" -> text,
      "comment" -> text

    )
  )

}

