package controllers

import play.api.mvc._
import play.api.data.Forms.{mapping, ignored, nonEmptyText, text}
import models.{Event, Group}
import anorm.{Pk, NotAssigned}
import play.api.data.Form

object Groups extends Controller {

  def list = Action {
    val groups = Group.findAll()
    Ok(views.html.groups.list(groups))
  }

  def show(id: Long) = Action {
    val group = Group.find(id)
    val events = Event.findByGroup(group)
    Ok(views.html.groups.show(group, events))
  }

  def createForm = Action {
    NotImplemented
  }

  def updateForm(id: Long) = Action {
    val group = Group.find(id)
    NotImplemented
  }

  def create = Action {
    NotImplemented
  }

  def update(id: Long) = Action {
    NotImplemented
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

