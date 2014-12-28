package controllers

import jp.t2v.lab.play2.auth.AuthElement
import models._
import models.security.Administrator
import play.api.data.Form
import play.api.data.Forms.{ignored, longNumber, mapping}
import play.api.mvc._
import util.AuthHelper._

object Memberships extends Controller with AuthElement with AuthConfigImpl {

  def createForm(groupId: Long) = StackAction(AuthorityKey -> hasPermission(Administrator)_) { implicit request =>
    implicit val loggedInUser = Option(loggedIn)
    Ok(views.html.memberships.edit(membershipForm, Group.find(groupId), User.findNonMembers(groupId)))
  }

  def create = StackAction(AuthorityKey -> hasPermission(Administrator)_) { implicit request =>
    implicit val loggedInUser = Option(loggedIn)
      membershipForm.bindFromRequest.fold(
        formWithErrors => {
          val group = Group.find(formWithErrors("groupId").value.get.toLong)
          val nonMembers = User.findNonMembers(group.id.get)
          BadRequest(views.html.memberships.edit(formWithErrors, group, nonMembers))
        },
        membership => {
          Membership.create(membership)
          Redirect(routes.Groups.show(membership.group.id.get))
        }
      )
  }

  def delete(id: Long) = StackAction(AuthorityKey -> hasPermission(Administrator)_) { implicit request =>
    val membership = Membership.find(id)
    Membership.delete(id)
    Redirect(routes.Groups.show(membership.group.id.get))
  }

  def toMembership(id: Option[Long], groupId: Long, userId: Long): Membership = {
    Membership(
      id = id,
      group = Group.find(groupId),
      user = User.find(userId)
    )
  }

  def fromMembership(membership: Membership) = {
    Option((membership.id, membership.group.id.get, membership.user.id.get))
  }

  val membershipForm: Form[Membership] = Form(
    mapping(
      "id" -> ignored(None: Option[Long]),
      "groupId" -> longNumber,
      "userId" -> longNumber
    )(toMembership)(fromMembership)
  )

}

