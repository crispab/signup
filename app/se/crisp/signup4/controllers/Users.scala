package se.crisp.signup4.controllers

import javax.inject.{Inject, Named}

import akka.actor.ActorRef
import cloudinary.model.CloudinaryResourceBuilder
import com.cloudinary.parameters.UploadParameters
import jp.t2v.lab.play2.auth.{AuthElement, OptionalAuthElement}
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms.{boolean, ignored, longNumber, mapping, nonEmptyText, optional, text}
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc._
import se.crisp.signup4
import se.crisp.signup4.models.security.{Administrator, NormalUser}
import se.crisp.signup4.models.{Event, Membership, Participation, User}
import se.crisp.signup4.services.{CloudinaryUrl, GravatarUrl, ImageUrl, RemindParticipant}
import se.crisp.signup4.util.AuthHelper._

import scala.concurrent.{ExecutionContext, Future}

class Users @Inject()(val messagesApi: MessagesApi, implicit val imageUrl: ImageUrl) extends Controller with OptionalAuthElement with AuthConfigImpl with I18nSupport{

  def show(id: Long): Action[AnyContent] = StackAction { implicit request =>
    val userToShow = User.find(id)
    Ok(se.crisp.signup4.views.html.users.show(userToShow))
  }
}

class UsersSecured @Inject()(val messagesApi: MessagesApi,
                             cloudinaryResourceBuilder: CloudinaryResourceBuilder,
                             cloudinaryUrl: CloudinaryUrl,
                             implicit val gravatarUrl: GravatarUrl,
                             implicit val imageUrl: ImageUrl,
                             @Named("event-reminder-actor") eventReminderActor: ActorRef) extends Controller with AuthElement with AuthConfigImpl with I18nSupport{

  def list: Action[AnyContent] = StackAction(AuthorityKey -> hasPermission(Administrator))  { implicit request =>
    implicit val loggedInUser: Option[User] = Option(loggedIn)
    val usersToList = User.findAll()
    Ok(se.crisp.signup4.views.html.users.list(usersToList))
  }

  def createForm: Action[AnyContent] = StackAction(AuthorityKey -> hasPermission(Administrator)) { implicit request =>
    implicit val loggedInUser: Option[User] = Option(loggedIn)
    Ok(se.crisp.signup4.views.html.users.edit(userCreateForm))
  }

  def createMemberForm(groupId: Long): Action[AnyContent] = StackAction(AuthorityKey -> hasPermission(Administrator)) { implicit request =>
    implicit val loggedInUser: Option[User] = Option(loggedIn)
    Ok(se.crisp.signup4.views.html.users.edit(userCreateForm, groupId = Option(groupId)))
  }

  def createGuestForm(eventId: Long): Action[AnyContent] = StackAction(AuthorityKey -> hasPermission(Administrator)) { implicit request =>
    implicit val loggedInUser: Option[User] = Option(loggedIn)
    Ok(se.crisp.signup4.views.html.users.edit(userCreateForm, eventId = Option(eventId)))
  }


  def updateForm(id: Long): Action[AnyContent] = StackAction(AuthorityKey -> hasPermissionOrSelf(Administrator, id)) { implicit request =>
    implicit val loggedInUser: Option[User] = Option(loggedIn)
    val userToUpdate = User.find(id)
    Ok(se.crisp.signup4.views.html.users.edit(userUpdateForm.fill(userToUpdate), idToUpdate = Option(id)))
  }

  def create: Action[AnyContent] = StackAction(AuthorityKey -> hasPermission(Administrator)) { implicit request =>
    implicit val loggedInUser: Option[User] = Option(loggedIn)
      userCreateForm.bindFromRequest.fold(
        formWithErrors => BadRequest(se.crisp.signup4.views.html.users.edit(formWithErrors)),
        user => {
          User.create(user)
          Redirect(routes.UsersSecured.list())
        }
      )
  }

  def createMember(groupId: Long): Action[AnyContent] = StackAction(AuthorityKey -> hasPermission(Administrator)) { implicit request =>
    implicit val loggedInUser: Option[User] = Option(loggedIn)
      userCreateForm.bindFromRequest.fold(
        formWithErrors => BadRequest(se.crisp.signup4.views.html.users.edit(formWithErrors)),
        user => {
          Membership.create(groupId = groupId, userId = User.create(user))
          Redirect(routes.Groups.show(groupId))
        }
      )
  }

  def createGuest(eventId: Long): Action[AnyContent] = StackAction(AuthorityKey -> hasPermission(Administrator)) { implicit request =>
    implicit val loggedInUser: Option[User] = Option(loggedIn)
      userCreateForm.bindFromRequest.fold(
        formWithErrors => BadRequest(se.crisp.signup4.views.html.users.edit(formWithErrors)),
        user => {
          Participation.createGuest(eventId = eventId, userId = User.create(user))
          Redirect(routes.Events.show(eventId))
        }
      )
  }

  def remindParticipant(id: Long, eventId: Long): Action[AnyContent] = StackAction(AuthorityKey -> hasPermission(Administrator)) { implicit request =>
    implicit val loggedInUser: Option[User] = Option(loggedIn)
    val user = User.find(id)
    val event = Event.find(eventId)
    if(!event.isCancelled) {
      eventReminderActor ! RemindParticipant(event, user, loggedIn)
      Redirect(routes.Events.show(eventId)).flashing("success" -> Messages("user.reminder", user.name))
    } else {
      Redirect(routes.Events.show(eventId)).flashing("error" -> Messages("event.cancelled.noreminders"))
    }
  }

  def update(id: Long): Action[AnyContent] = StackAction(AuthorityKey -> hasPermissionOrSelf(Administrator, id)) { implicit request =>
    implicit val loggedInUser: Option[User] = Option(loggedIn)
    userUpdateForm.bindFromRequest.fold(
        formWithErrors => BadRequest(se.crisp.signup4.views.html.users.edit(formWithErrors, Option(id))),
        user => {
          if(isAdmin(loggedInUser)) {
            User.updateProperties(id, user)
          } else {
            User.updateProperties(id, preventPermissionToBeChanged(user))
          }
          Redirect(routes.Users.show(id))
        }
      )
  }

  private def preventPermissionToBeChanged(user: User): User = {
    User(permission = NormalUser,
      id = user.id,
      firstName = user.firstName,
      lastName = user.lastName,
      email = user.email,
      phone = user.phone,
      comment = user.comment,
      password = user.password,
      imageProvider = user.imageProvider,
      imageVersion = user.imageVersion
    )
  }

  def updateImageForm(id: Long): Action[AnyContent] = StackAction(AuthorityKey -> hasPermissionOrSelf(Administrator, id)) { implicit request =>
    implicit val loggedInUser: Option[User] = Option(loggedIn)
    val userToUpdate = User.find(id)
    Ok(se.crisp.signup4.views.html.users.updateImage(userToUpdate))
  }

  def resetImage(id: Long): Action[AnyContent] = StackAction(AuthorityKey -> hasPermissionOrSelf(Administrator, id)) { implicit request =>
    implicit val loggedInUser: Option[User] = Option(loggedIn)

    User.updateInfo(id, GravatarUrl.identifier)

    Redirect(routes.Users.show(id))
  }

  def uploadImage(id: Long): Action[AnyContent] = AsyncStack(AuthorityKey -> hasPermissionOrSelf(Administrator, id)) { implicit request =>
    implicit val loggedInUser: Option[User] = Option(loggedIn)
    val userToUpdate = User.find(id)

    import ExecutionContext.Implicits.global

    val body = request.body.asMultipartFormData
    val resourceFile = body.get.file("image")
    if (resourceFile.isEmpty) {
      Future(BadRequest(se.crisp.signup4.views.html.users.updateImage(userToUpdate, Option(Messages("user.upload.nofile")))))
    } else {
      cloudinaryResourceBuilder.upload(resourceFile.get.ref.file, UploadParameters()
                                                            .publicId(cloudinaryUrl.publicId(userToUpdate))
                                                            .format("png")
                                                            .overwrite(value = true)).map {
        cr =>
          val cloudinaryFileVersion = cr.data.get.version
          User.updateInfo(id, CloudinaryUrl.identifier, Some(cloudinaryFileVersion))
          Redirect(routes.Users.show(id))
      } recover {
        case ex: Exception =>
          Logger.error("Failed uploading image to Cloudinary", ex)
          BadRequest(se.crisp.signup4.views.html.users.updateImage(userToUpdate, Option(Messages("user.upload.error"))))
      }
    }
  }

  def delete(id: Long): Action[AnyContent] = StackAction(AuthorityKey -> hasPermission(Administrator)) { implicit request =>
    User.delete(id)
    Redirect(routes.UsersSecured.list())
  }


  val userCreateForm: Form[User] = Form(
    mapping(
      "id" -> ignored(None:Option[Long]),
      "firstName" -> nonEmptyText(maxLength = 127),
      "lastName" -> nonEmptyText(maxLength = 127),
      "email" -> play.api.data.Forms.email.verifying("error.signup.email.alreadyinuse", User.findByEmail(_).isEmpty),
      "phone" -> text(maxLength = 127),
      "comment" -> text(maxLength = 127),
      "administrator" -> boolean,
      "password" -> text(maxLength = 127)
    )(toUser)(fromUser)
      .verifying("error.signup.password.toshort", user => user.password.length >= 8)
  )


  val userUpdateForm: Form[User] = Form(
    mapping(
      "id" -> optional(longNumber),
      "firstName" -> nonEmptyText(maxLength = 127),
      "lastName" -> nonEmptyText(maxLength = 127),
      "email" -> play.api.data.Forms.email,
      "phone" -> text(maxLength = 127),
      "comment" -> text(maxLength = 127),
      "administrator" -> boolean,
      "password" -> text(maxLength = 127)
    )(toUser)(fromUser)
      .verifying("error.signup.email.alreadyinuse", user => User.verifyUniqueEmail(user))
      .verifying("error.signup.password.toshort", user => user.password.length >= 8)
  )

  def toUser(id: Option[Long], firstName: String, lastName: String, email: String, phone: String, comment: String, isAdministrator: Boolean, password: String): User = {
    val permission = if (isAdministrator) {
      Administrator
    } else {
      NormalUser
    }
    User(id=id, firstName=firstName.trim, lastName=lastName.trim, email=email.trim, phone=phone.trim, comment=comment.trim, permission=permission, password=password, imageProvider = GravatarUrl.identifier)
  }

  def fromUser(user: signup4.models.User): Option[(Option[Long], String, String, String, String, String, Boolean, String)] = {
    Option(user.id, user.firstName, user.lastName, user.email, user.phone, user.comment, user.permission==Administrator, User.NOT_CHANGED_PASSWORD)
  }
}

