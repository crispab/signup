package controllers

import play.api.mvc._
import play.api.data.Forms.{mapping, ignored, nonEmptyText, text, number, boolean, email}
import models.{Membership, Event, Group}
import anorm.{Pk, NotAssigned}
import play.api.data.Form
import jp.t2v.lab.play2.auth.Auth
import models.security.Administrator

object Groups extends Controller with Auth with AuthConfigImpl {

  def list = optionalUserAction { implicit user => implicit request =>
    val groups = Group.findAll()
    Ok(views.html.groups.list(groups))
  }

  def show(id: Long, showAll: Boolean) = optionalUserAction { implicit user => implicit request =>
    val group = Group.find(id)
    val members = Membership.findMembers(group)
    if(showAll) {
      Ok(views.html.groups.show(group, Event.findAllEventsByGroup(group) , members, showingAll = true))
    } else {
      Ok(views.html.groups.show(group, Event.findFutureEventsByGroup(group), members))
    }
  }

  def createForm = authorizedAction(Administrator) { user => implicit request =>
    implicit val loggedInUser = Option(user)
    Ok(views.html.groups.edit(groupForm))
  }

  def updateForm(id: Long) = authorizedAction(Administrator) { user => implicit request =>
    implicit val loggedInUser = Option(user)
    val group = Group.find(id)
    Ok(views.html.groups.edit(groupForm.fill(group), Option(id)))
  }

  def create = authorizedAction(Administrator) { user => implicit request =>
    implicit val loggedInUser = Option(user)
      groupForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.groups.edit(formWithErrors)),
        group => {
          Group.create(group)
          Redirect(routes.Groups.list())
        }
      )
  }

  def update(id: Long) = authorizedAction(Administrator) { user => implicit request =>
    implicit val loggedInUser = Option(user)
      groupForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.groups.edit(formWithErrors, Option(id))),
        group => {
          Group.update(id, group)
          Redirect(routes.Groups.show(id))
        }
      )
  }

  def delete(id: Long) = authorizedAction(Administrator) { user => implicit request =>
    implicit val loggedInUser = Option(user)
    NotImplemented
  }

  val groupForm: Form[Group] = Form(
    mapping(
      "id" -> ignored(NotAssigned: Pk[Long]),
      "name" -> nonEmptyText(maxLength = 127),
      "description" -> text(maxLength = 127),
      "mail_from" -> email
    )(Group.apply)(Group.unapply)
  )
}
