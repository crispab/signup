package controllers

import play.api.mvc._
import play.api.data.Forms.{mapping, ignored, nonEmptyText, text}
import models.{Membership, Event, Group}
import anorm.{Pk, NotAssigned}
import play.api.data.Form
import jp.t2v.lab.play20.auth.Auth
import models.security.Administrator

object Groups extends Controller with Auth with AuthConfigImpl {

  def list = Action {
    val groups = Group.findAll()
    Ok(views.html.groups.list(groups))
  }

  def show(id: Long, showAll: Boolean) = Action {
    val group = Group.find(id)
    val members = Membership.findMembers(group)
    if(showAll) {
      Ok(views.html.groups.show(group, Event.findAllEventsByGroup(group) , members, showingAll = true))
    } else {
      Ok(views.html.groups.show(group, Event.findFutureEventsByGroup(group), members))
    }
  }

  def createForm = authorizedAction(Administrator) { user => implicit request =>
    Ok(views.html.groups.edit(groupForm))
  }

  def updateForm(id: Long) = Action {
    val group = Group.find(id)
    Ok(views.html.groups.edit(groupForm.fill(group), Option(id)))
  }

  def create = Action {
    implicit request =>
      groupForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.groups.edit(formWithErrors)),
        group => {
          Group.create(group)
          Redirect(routes.Groups.list())
        }
      )
  }

  def update(id: Long) = Action {
    implicit request =>
      groupForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.groups.edit(formWithErrors, Option(id))),
        group => {
          Group.update(id, group)
          Redirect(routes.Groups.show(id))
        }
      )
  }

  def delete(id: Long) = Action {
    // Group.delete(id)
    NotImplemented
  }

  val groupForm: Form[Group] = Form(
    mapping(
      "id" -> ignored(NotAssigned: Pk[Long]),
      "name" -> nonEmptyText,
      "description" -> text
    )(Group.apply)(Group.unapply)
  )

}

