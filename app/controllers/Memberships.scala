package controllers

import anorm.{NotAssigned, Pk}
import jp.t2v.lab.play2.auth.Auth
import models._
import models.security.Administrator
import play.api.data.Form
import play.api.data.Forms.{ignored, longNumber, mapping}
import play.api.mvc._

object Memberships extends Controller with Auth with AuthConfigImpl {

  def createForm(groupId: Long) = authorizedAction(Administrator) { user => implicit request =>
    implicit val loggedInUser = Option(user)
    Ok(views.html.memberships.edit(membershipForm, Group.find(groupId), User.findNonMembers(groupId)))
  }

  def create = authorizedAction(Administrator) { user => implicit request =>
    implicit val loggedInUser = Option(user)
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

  def delete(id: Long) = authorizedAction(Administrator) { user => implicit request =>
    val membership = Membership.find(id)
    Membership.delete(id)
    Redirect(routes.Groups.show(membership.group.id.get))
  }

  def toMembership(id: Pk[Long], groupId: Long, userId: Long): Membership = {
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
      "id" -> ignored(NotAssigned: Pk[Long]),
      "groupId" -> longNumber,
      "userId" -> longNumber
    )(toMembership)(fromMembership)
  )

}

