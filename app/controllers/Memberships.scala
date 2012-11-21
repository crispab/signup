package controllers

import play.api.mvc._
import play.api.data.Forms.{mapping, ignored, longNumber}
import models._
import anorm.{Pk, NotAssigned}
import play.api.data.Form
import jp.t2v.lab.play20.auth.Auth
import models.security.{NormalUser, Administrator}

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

