package controllers

import play.api.mvc._
import play.api.data.Forms.{mapping, ignored, longNumber}
import models._
import anorm.{Pk, NotAssigned}
import play.api.data.Form

object Memberships extends Controller {

  def create = Action {
    NotImplemented
  }

  def delete(id: Long) = Action {
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
      "groupx" -> longNumber,
      "userx" -> longNumber
    )(toMembership)(fromMembership)
  )

}

