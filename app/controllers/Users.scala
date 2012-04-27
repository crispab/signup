package controllers

import play.api.mvc._
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms.{tuple, nonEmptyText, text, email, optional}
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
      failingForm => {
        Logger.info("Errors: " + failingForm.errors)
        Redirect(routes.Users.createForm)
      }, {
        case (firstName, nickName, lastName, primaryEmail, secondaryEmail, mobileNr, comment) => {
          User.create(User(
            firstName = firstName,
            nickName = nickName.getOrElse(""),
            lastName = lastName.getOrElse(""),
            primaryEmail = primaryEmail.getOrElse(""),
            secondaryEmail = secondaryEmail.getOrElse(""),
            mobileNr = mobileNr.getOrElse(""),
            comment = comment.getOrElse("")
          ))
          Redirect(routes.Users.list)
        }
      }
    )
  }

  val userForm = Form(
    tuple(
      "firstName" -> nonEmptyText,
      "nickName" -> optional(text),
      "lastName" -> optional(text),
      "primaryEmail" -> optional(email),
      "secondaryEmail" -> optional(email),
      "mobileNr" -> optional(text),
      "comment" -> optional(text)

    )
  )

}

