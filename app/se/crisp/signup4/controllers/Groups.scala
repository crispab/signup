package se.crisp.signup4.controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api.Silhouette
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.I18nSupport
import play.api.mvc._
import se.crisp.signup4.models._
import se.crisp.signup4.models.dao.{EventDAO, GroupDAO, MembershipDAO, UserDAO}
import se.crisp.signup4.models.security.Administrator
import se.crisp.signup4.silhouette.{DefaultEnv, WithPermission}

class Groups @Inject() (val silhouette: Silhouette[DefaultEnv],
                        val listView: se.crisp.signup4.views.html.groups.list,
                        val showView: se.crisp.signup4.views.html.groups.show,
                        val editView: se.crisp.signup4.views.html.groups.edit,
                        val groupDAO: GroupDAO,
                        val eventDAO: EventDAO,
                        val userDAO: UserDAO,
                        val membershipDAO: MembershipDAO) extends InjectedController  with I18nSupport{

  def list: Action[AnyContent] = silhouette.UserAwareAction { implicit request =>
    implicit val user: Option[User] = request.identity
    val groups = groupDAO.findAll()
    Ok(listView(groups))
  }

  def show(id: Long, showAll: Boolean): Action[AnyContent] = silhouette.UserAwareAction { implicit request =>
    implicit val user: Option[User] = request.identity
    val group = groupDAO.find(id)
    val members = membershipDAO.findMembers(group)
    if(showAll) {
      Ok(showView(group, eventDAO.findAllEventsByGroup(group) , members, showingAll = true))
    } else {
      Ok(showView(group, eventDAO.findFutureEventsByGroup(group), members))
    }
  }

  def createForm: Action[AnyContent] = silhouette.SecuredAction(WithPermission(Administrator)) { implicit request =>
    implicit val loggedInUser: Option[User] = Option(request.identity)
    Ok(editView(groupForm))
  }

  def updateForm(id: Long): Action[AnyContent] = silhouette.SecuredAction(WithPermission(Administrator)) { implicit request =>
    implicit val loggedInUser: Option[User] = Option(request.identity)
    val group = groupDAO.find(id)
    Ok(editView(groupForm.fill(group), Option(id)))
  }

  def create: Action[AnyContent] = silhouette.SecuredAction(WithPermission(Administrator)) { implicit request =>
    implicit val loggedInUser: Option[User] = Option(request.identity)
    groupForm.bindFromRequest.fold(
      formWithErrors => BadRequest(editView(formWithErrors)),
      group => {
        val groupId = groupDAO.create(group)
        Redirect(routes.Groups.show(groupId))
      }
    )
  }

  def update(id: Long): Action[AnyContent] = silhouette.SecuredAction(WithPermission(Administrator)) { implicit request =>
    implicit val loggedInUser: Option[User] = Option(request.identity)
    groupForm.bindFromRequest.fold(
      formWithErrors => BadRequest(editView(formWithErrors, Option(id))),
      group => {
        groupDAO.update(id, group)
        Redirect(routes.Groups.show(id))
      }
    )
  }

  def delete(id: Long): Action[AnyContent] = silhouette.SecuredAction(WithPermission(Administrator)) { implicit request =>
    implicit val loggedInUser: Option[User] = Option(request.identity)
    groupDAO.delete(id)
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
