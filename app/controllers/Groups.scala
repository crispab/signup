package controllers

import jp.t2v.lab.play2.auth.{OptionalAuthElement, AuthElement}
import models.security.Administrator
import models.{Event, Group, Membership}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import util.AuthHelper._

object Groups extends Controller with OptionalAuthElement with AuthConfigImpl {

  def list = StackAction { implicit request =>
    val groups = Group.findAll()
    Ok(views.html.groups.list(groups))
  }

  def show(id: Long, showAll: Boolean) = StackAction { implicit request =>
    val group = Group.find(id)
    val members = Membership.findMembers(group)
    if(showAll) {
      Ok(views.html.groups.show(group, Event.findAllEventsByGroup(group) , members, showingAll = true))
    } else {
      Ok(views.html.groups.show(group, Event.findFutureEventsByGroup(group), members))
    }
  }
}


object GroupsSecured extends Controller with AuthElement with AuthConfigImpl {


  def createForm = StackAction(AuthorityKey -> hasPermission(Administrator)_) { implicit request =>
    implicit val loggedInUser = Option(loggedIn)
    Ok(views.html.groups.edit(groupForm))
  }

  def updateForm(id: Long) = StackAction(AuthorityKey -> hasPermission(Administrator)_) { implicit request =>
    implicit val loggedInUser = Option(loggedIn)
    val group = Group.find(id)
    Ok(views.html.groups.edit(groupForm.fill(group), Option(id)))
  }

  def create = StackAction(AuthorityKey -> hasPermission(Administrator)_) { implicit request =>
    implicit val loggedInUser = Option(loggedIn)
      groupForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.groups.edit(formWithErrors)),
        group => {
          Group.create(group)
          Redirect(routes.Groups.list())
        }
      )
  }

  def update(id: Long) = StackAction(AuthorityKey -> hasPermission(Administrator)_) { implicit request =>
    implicit val loggedInUser = Option(loggedIn)
      groupForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.groups.edit(formWithErrors, Option(id))),
        group => {
          Group.update(id, group)
          Redirect(routes.Groups.show(id))
        }
      )
  }

  def delete(id: Long) = StackAction(AuthorityKey -> hasPermission(Administrator)_) { implicit request =>
    implicit val loggedInUser = Option(loggedIn)
    Group.delete(id)
    Redirect(routes.Groups.list())

  }

  val groupForm: Form[Group] = Form(
    mapping(
      "id" -> ignored(None: Option[Long]),
      "name" -> nonEmptyText(maxLength = 127),
      "description" -> text(maxLength = 127),
      "mail_from" -> email,
      "mail_subject_prefix" -> text(maxLength = 127)
    )(Group.apply)(Group.unapply)
  )
}
