package se.crisp.signup4.controllers

import jp.t2v.lab.play2.auth.AuthElement
import se.crisp.signup4.models._
import se.crisp.signup4.models.security.Administrator
import play.api.data.Form
import play.api.data.Forms.{ignored, longNumber, mapping}
import play.api.mvc._
import se.crisp.signup4.util.AuthHelper._

object Memberships extends Controller with AuthElement with AuthConfigImpl {

  def createForm(groupId: Long): Action[AnyContent] = StackAction(AuthorityKey -> hasPermission(Administrator)) { implicit request =>
    implicit val loggedInUser: Option[User] = Option(loggedIn)
    Ok(se.crisp.signup4.views.html.memberships.edit(membershipForm, Group.find(groupId), User.findNonMembers(groupId)))
  }

  def create: Action[AnyContent] = StackAction(AuthorityKey -> hasPermission(Administrator)) { implicit request =>
    implicit val loggedInUser: Option[User] = Option(loggedIn)
      membershipForm.bindFromRequest.fold(
        formWithErrors => {
          val group = Group.find(formWithErrors("groupId").value.get.toLong)
          val nonMembers = User.findNonMembers(group.id.get)
          BadRequest(se.crisp.signup4.views.html.memberships.edit(formWithErrors, group, nonMembers))
        },
        membership => {
          Membership.create(membership)
          Redirect(routes.Groups.show(membership.group.id.get))
        }
      )
  }

  def delete(id: Long): Action[AnyContent] = StackAction(AuthorityKey -> hasPermission(Administrator)) { implicit request =>
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

  def fromMembership(membership: Membership): Option[(Option[Long], Long, Long)] = {
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

