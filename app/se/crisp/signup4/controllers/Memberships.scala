package se.crisp.signup4.controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api.Silhouette
import play.api.data.Form
import play.api.data.Forms.{ignored, longNumber, mapping}
import play.api.i18n.I18nSupport
import play.api.mvc._
import se.crisp.signup4.models._
import se.crisp.signup4.models.dao.{GroupDAO, MembershipDAO, UserDAO}
import se.crisp.signup4.models.security.Administrator
import se.crisp.signup4.services.ImageUrl
import se.crisp.signup4.silhouette.{DefaultEnv, WithPermission}
import se.crisp.signup4.util.{AuthHelper, FormHelper, LocaleHelper, ThemeHelper}

class Memberships @Inject() (val silhouette: Silhouette[DefaultEnv],
                             implicit val authHelper: AuthHelper,
                             implicit val localeHelper: LocaleHelper,
                             implicit val themeHelper: ThemeHelper,
                             implicit val formHelper: FormHelper,
                             val userDAO: UserDAO,
                             val groupDAO: GroupDAO,
                             val membershipDAO: MembershipDAO,
                             implicit val imageUrl: ImageUrl) extends InjectedController with I18nSupport{

  def createForm(groupId: Long): Action[AnyContent] = silhouette.SecuredAction(WithPermission(Administrator)) { implicit request =>
    implicit val loggedInUser: Option[User] = Option(request.identity)
    Ok(se.crisp.signup4.views.html.memberships.edit(membershipForm, groupDAO.find(groupId), userDAO.findNonMembers(groupId)))
  }

  def create: Action[AnyContent] = silhouette.SecuredAction(WithPermission(Administrator)) { implicit request =>
    implicit val loggedInUser: Option[User] = Option(request.identity)
      membershipForm.bindFromRequest.fold(
        formWithErrors => {
          val group = groupDAO.find(formWithErrors("groupId").value.get.toLong)
          val nonMembers = userDAO.findNonMembers(group.id.get)
          BadRequest(se.crisp.signup4.views.html.memberships.edit(formWithErrors, group, nonMembers))
        },
        membership => {
          membershipDAO.create(membership)
          Redirect(routes.Groups.show(membership.group.id.get))
        }
      )
  }

  def delete(id: Long): Action[AnyContent] = silhouette.SecuredAction(WithPermission(Administrator)) { implicit request =>
    val membership = membershipDAO.find(id)
    membershipDAO.delete(id)
    Redirect(routes.Groups.show(membership.group.id.get))
  }

  def toMembership(id: Option[Long], groupId: Long, userId: Long): Membership = {
    Membership(
      id = id,
      group = groupDAO.find(groupId),
      user = userDAO.find(userId)
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

