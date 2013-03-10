package controllers

import play.api.mvc._
import play.api.data.Forms.{mapping, ignored, nonEmptyText, text, optional, longNumber}
import models.{Participation, Membership, User}
import anorm.{Id, Pk, NotAssigned}
import play.api.data.Form
import jp.t2v.lab.play2.auth.Auth
import models.security.{NormalUser, Permission, Administrator}

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
    Ok(views.html.users.edit(userCreateForm))
  }

  def createMemberForm(groupId: Long) = authorizedAction(Administrator) { user => implicit request =>
    implicit val loggedInUser = Option(user)
    Ok(views.html.users.edit(userCreateForm, groupId = Option(groupId)))
  }

  def createGuestForm(eventId: Long) = authorizedAction(Administrator) { user => implicit request =>
    implicit val loggedInUser = Option(user)
    Ok(views.html.users.edit(userCreateForm, eventId = Option(eventId)))
  }


  def updateForm(id: Long) = authorizedAction(Administrator) { user => implicit request =>
    implicit val loggedInUser = Option(user)
    val userToUpdate = User.find(id)
    Ok(views.html.users.edit(userUpdateForm.fill(userToUpdate), idToUpdate = Option(id)))
  }

  def create = authorizedAction(Administrator) { user => implicit request =>
    implicit val loggedInUser = Option(user)
      userCreateForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.users.edit(formWithErrors)),
        user => {
          User.create(user)
          Redirect(routes.Users.list())
        }
      )
  }

  def createMember(groupId: Long) = authorizedAction(Administrator) { user => implicit request =>
    implicit val loggedInUser = Option(user)
      userCreateForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.users.edit(formWithErrors)),
        user => {
          Membership.create(groupId = groupId, userId = User.create(user))
          Redirect(routes.Groups.show(groupId))
        }
      )
  }

  def createGuest(eventId: Long) = authorizedAction(Administrator) { user => implicit request =>
    implicit val loggedInUser = Option(user)
      userCreateForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.users.edit(formWithErrors)),
        user => {
          Participation.createGuest(eventId = eventId, userId = User.create(user))
          Redirect(routes.Events.show(eventId))
        }
      )
  }

  def update(id: Long) = authorizedAction(Administrator) { user => implicit request =>
    implicit val loggedInUser = Option(user)
      userUpdateForm.bindFromRequest.fold(
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

  val userCreateForm: Form[User] = Form(
    mapping(
      "id" -> ignored(NotAssigned:Pk[Long]),
      "firstName" -> nonEmptyText,
      "lastName" -> nonEmptyText,
      "email" -> play.api.data.Forms.email.verifying("Epostadressen används av någon annan", User.findByEmail(_).isEmpty),
      "phone" -> text,
      "comment" -> text,
      "permission" -> ignored(NormalUser: Permission),
      "password" -> ignored("*")
    )(User.apply)(User.unapply)
  )

  val primaryKey = optional(longNumber).transform(
    (optionLong: Option[Long]) =>
      if (optionLong.isDefined) {
        Id(optionLong.get)
      } else {
        NotAssigned:Pk[Long]
      },
    (pkLong: Pk[Long]) =>
      pkLong.toOption)

  val userUpdateForm: Form[User] = Form(
    mapping(
      "id" -> primaryKey,
      "firstName" -> nonEmptyText,
      "lastName" -> nonEmptyText,
      "email" -> play.api.data.Forms.email,
      "phone" -> text,
      "comment" -> text,
      "permission" -> ignored(NormalUser: Permission),
      "password" -> ignored("*")
    )(User.apply)(User.unapply).verifying("Epostadressen används av någon annan", user => User.verifyUniqueEmail(user))
  )

}

