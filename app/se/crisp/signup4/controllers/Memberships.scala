package se.crisp.signup4.controllers

import javax.inject.Inject

import jp.t2v.lab.play2.auth.AuthElement
import se.crisp.signup4.models._
import se.crisp.signup4.models.security.Administrator
import play.api.data.Form
import play.api.data.Forms.{ignored, longNumber, mapping}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import se.crisp.signup4.services.ImageUrl
import se.crisp.signup4.util.{AuthHelper, FormHelper, LocaleHelper, ThemeHelper}

class Memberships @Inject() (val messagesApi: MessagesApi,
                             implicit val authHelper: AuthHelper,
                             implicit val localeHelper: LocaleHelper,
                             implicit val themeHelper: ThemeHelper,
                             implicit val formHelper: FormHelper,
                             val userDAO: UserDAO,
                             val membershipDAO: MembershipDAO,
                             implicit val imageUrl: ImageUrl) extends Controller with AuthElement with AuthConfigImpl with I18nSupport{

  def createForm(groupId: Long): Action[AnyContent] = StackAction(AuthorityKey -> authHelper.hasPermission(Administrator)) { implicit request =>
    implicit val loggedInUser: Option[User] = Option(loggedIn)
    Ok(se.crisp.signup4.views.html.memberships.edit(membershipForm, Group.find(groupId), userDAO.findNonMembers(groupId)))
  }

  def create: Action[AnyContent] = StackAction(AuthorityKey -> authHelper.hasPermission(Administrator)) { implicit request =>
    implicit val loggedInUser: Option[User] = Option(loggedIn)
      membershipForm.bindFromRequest.fold(
        formWithErrors => {
          val group = Group.find(formWithErrors("groupId").value.get.toLong)
          val nonMembers = userDAO.findNonMembers(group.id.get)
          BadRequest(se.crisp.signup4.views.html.memberships.edit(formWithErrors, group, nonMembers))
        },
        membership => {
          membershipDAO.create(membership)
          Redirect(routes.Groups.show(membership.group.id.get))
        }
      )
  }

  def delete(id: Long): Action[AnyContent] = StackAction(AuthorityKey -> authHelper.hasPermission(Administrator)) { implicit request =>
    val membership = membershipDAO.find(id)
    membershipDAO.delete(id)
    Redirect(routes.Groups.show(membership.group.id.get))
  }

  def toMembership(id: Option[Long], groupId: Long, userId: Long): Membership = {
    Membership(
      id = id,
      group = Group.find(groupId),
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

