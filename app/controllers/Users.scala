package controllers

import play.api.mvc._
import play.api.data.Forms.{mapping, ignored, nonEmptyText, text}
import models.{Membership, User}
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

  def createForm(memberGroupId: Option[Long]) = Action {
    Ok(views.html.users.edit(userForm, memberGroupId = memberGroupId))
  }

  def updateForm(id: Long) = Action {
    val user = User.find(id)
    Ok(views.html.users.edit(userForm.fill(user), idToUpdate = Option(id)))
  }

  def create = Action {
    implicit request =>
      userForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.users.edit(formWithErrors)),
        user => {
          val userId = User.create(user)
          val memberGroupid = request.body.asFormUrlEncoded.get("memberGroupId").head
          if (memberGroupid.isEmpty) {
            Redirect(routes.Users.list())
          } else {
            Membership.create(groupId = memberGroupid.toLong, userId = userId)
            Redirect(routes.Groups.show(memberGroupid.toLong))
          }
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

