package controllers

import play.api.mvc._
import play.api.data.Forms.{mapping, ignored, nonEmptyText, text}
import models.{Participation, Membership, User}
import anorm.{Pk, NotAssigned}
import play.api.data.Form
import jp.t2v.lab.play20.auth.Auth
import models.security.{NormalUser, Administrator}

object Users extends Controller with Auth with AuthConfigImpl {

  def list = optionalUserAction { implicit user => implicit request =>
    val usersToList = User.findAll()
    Ok(views.html.users.list(usersToList))
  }

  def show(id : Long) = optionalUserAction { implicit user => implicit request =>
    val userToShow = User.find(id)
    Ok(views.html.users.show(userToShow))
  }

  def createForm = authorizedAction(Administrator) { user => implicit request =>
    implicit val loggedInUser = Option(user)
    Ok(views.html.users.edit(userForm))
  }

  def createMemberForm(groupId: Long) = authorizedAction(Administrator) { user => implicit request =>
    implicit val loggedInUser = Option(user)
    Ok(views.html.users.edit(userForm, groupId = Option(groupId)))
  }

  def createGuestForm(eventId: Long) = authorizedAction(Administrator) { user => implicit request =>
    implicit val loggedInUser = Option(user)
    Ok(views.html.users.edit(userForm, eventId = Option(eventId)))
  }


  def updateForm(id: Long) = authorizedAction(Administrator) { user => implicit request =>
    implicit val loggedInUser = Option(user)
    val userToUpdate = User.find(id)
    Ok(views.html.users.edit(userForm.fill(userToUpdate), idToUpdate = Option(id)))
  }

  def create = authorizedAction(Administrator) { user => implicit request =>
    implicit val loggedInUser = Option(user)
      userForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.users.edit(formWithErrors)),
        user => {
          User.create(user)
          Redirect(routes.Users.list())
        }
      )
  }

  def createMember(groupId: Long) = authorizedAction(Administrator) { user => implicit request =>
    implicit val loggedInUser = Option(user)
      userForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.users.edit(formWithErrors)),
        user => {
          Membership.create(groupId = groupId, userId = User.create(user))
          Redirect(routes.Groups.show(groupId))
        }
      )
  }

  def createGuest(eventId: Long) = authorizedAction(Administrator) { user => implicit request =>
    implicit val loggedInUser = Option(user)
      userForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.users.edit(formWithErrors)),
        user => {
          Participation.createGuest(eventId = eventId, userId = User.create(user))
          Redirect(routes.Events.show(eventId))
        }
      )
  }

  def update(id: Long) = authorizedAction(Administrator) { user => implicit request =>
    implicit val loggedInUser = Option(user)
      userForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.users.edit(formWithErrors, Option(id))),
        user => {
          User.update(id, user)
          Redirect(routes.Users.show(id))
        }
      )
  }

  def delete(id: Long) = authorizedAction(Administrator) { user => implicit request =>
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

