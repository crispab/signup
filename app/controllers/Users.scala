package controllers

import cloudinary.model.CloudinaryResource
import com.cloudinary.parameters.UploadParameters
import jp.t2v.lab.play2.auth.{AuthElement, OptionalAuthElement}
import models.security.{Administrator, NormalUser}
import models.{Event, Membership, Participation, User}
import play.api.data.Form
import play.api.data.Forms.{boolean, ignored, longNumber, mapping, nonEmptyText, optional, text}
import play.api.mvc._
import services.{RemindParticipant, EventReminderActor, GravatarUrl, CloudinaryUrl}
import util.AuthHelper._

import scala.concurrent.{Future, ExecutionContext}

object Users extends Controller with OptionalAuthElement with AuthConfigImpl {

  def show(id: Long) = StackAction { implicit request =>
    val userToShow = User.find(id)
    Ok(views.html.users.show(userToShow))
  }
}

object UsersSecured extends Controller with AuthElement with AuthConfigImpl {

  def list = StackAction(AuthorityKey -> hasPermission(Administrator))  { implicit request =>
    implicit val loggedInUser = Option(loggedIn)
    val usersToList = User.findAll()
    Ok(views.html.users.list(usersToList))
  }

  def createForm = StackAction(AuthorityKey -> hasPermission(Administrator)) { implicit request =>
    implicit val loggedInUser = Option(loggedIn)
    Ok(views.html.users.edit(userCreateForm))
  }

  def createMemberForm(groupId: Long) = StackAction(AuthorityKey -> hasPermission(Administrator)) { implicit request =>
    implicit val loggedInUser = Option(loggedIn)
    Ok(views.html.users.edit(userCreateForm, groupId = Option(groupId)))
  }

  def createGuestForm(eventId: Long) = StackAction(AuthorityKey -> hasPermission(Administrator)) { implicit request =>
    implicit val loggedInUser = Option(loggedIn)
    Ok(views.html.users.edit(userCreateForm, eventId = Option(eventId)))
  }


  def updateForm(id: Long) = StackAction(AuthorityKey -> hasPermissionOrSelf(Administrator, id)) { implicit request =>
    implicit val loggedInUser = Option(loggedIn)
    val userToUpdate = User.find(id)
    Ok(views.html.users.edit(userUpdateForm.fill(userToUpdate), idToUpdate = Option(id)))
  }

  def create = StackAction(AuthorityKey -> hasPermission(Administrator)) { implicit request =>
    implicit val loggedInUser = Option(loggedIn)
      userCreateForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.users.edit(formWithErrors)),
        user => {
          User.create(user)
          Redirect(routes.UsersSecured.list())
        }
      )
  }

  def createMember(groupId: Long) = StackAction(AuthorityKey -> hasPermission(Administrator)) { implicit request =>
    implicit val loggedInUser = Option(loggedIn)
      userCreateForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.users.edit(formWithErrors)),
        user => {
          Membership.create(groupId = groupId, userId = User.create(user))
          Redirect(routes.Groups.show(groupId))
        }
      )
  }

  def createGuest(eventId: Long) = StackAction(AuthorityKey -> hasPermission(Administrator)) { implicit request =>
    implicit val loggedInUser = Option(loggedIn)
      userCreateForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.users.edit(formWithErrors)),
        user => {
          Participation.createGuest(eventId = eventId, userId = User.create(user))
          Redirect(routes.Events.show(eventId))
        }
      )
  }

  def remindParticipant(id: Long, eventId: Long) = StackAction(AuthorityKey -> hasPermission(Administrator)) { implicit request =>
    implicit val loggedInUser = Option(loggedIn)
    val user = User.find(id)
    val event = Event.find(eventId)
    if(!event.isCancelled) {
      EventReminderActor.instance() ! RemindParticipant(event, user, loggedIn)
      Redirect(routes.Events.show(eventId)).flashing("success" -> ("En påminnelse om sammankomsten kommer att skickas till " + user.firstName + " " + user.lastName))
    } else {
      Redirect(routes.Events.show(eventId)).flashing("error" -> "Sammankomsten är inställd. Det går inte att skicka påminnelser.")
    }
  }

  def update(id: Long) = StackAction(AuthorityKey -> hasPermissionOrSelf(Administrator, id)) { implicit request =>
    implicit val loggedInUser = Option(loggedIn)
    userUpdateForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.users.edit(formWithErrors, Option(id))),
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

  def updateImageForm(id: Long) = StackAction(AuthorityKey -> hasPermissionOrSelf(Administrator, id)) { implicit request =>
    implicit val loggedInUser = Option(loggedIn)
    val userToUpdate = User.find(id)
    Ok(views.html.users.updateImage(userToUpdate))
  }

  def resetImage(id: Long) = StackAction(AuthorityKey -> hasPermissionOrSelf(Administrator, id)) { implicit request =>
    implicit val loggedInUser = Option(loggedIn)
    val userToUpdate = User.find(id)

    User.updateInfo(id, GravatarUrl.identifier)

    Redirect(routes.Users.show(id))
  }

  def uploadImage(id: Long) = AsyncStack(AuthorityKey -> hasPermissionOrSelf(Administrator, id)) { implicit request =>
    implicit val loggedInUser = Option(loggedIn)
    val userToUpdate = User.find(id)

    import ExecutionContext.Implicits.global

    val body = request.body.asMultipartFormData
    val resourceFile = body.get.file("image")
    if (resourceFile.isEmpty) {
      Future(BadRequest(views.html.users.updateImage(userToUpdate, Option("Du måste välja en bild från datorn"))))
    } else {
      CloudinaryResource.upload(resourceFile.get.ref.file, UploadParameters()
                                                            .publicId(CloudinaryUrl.publicId(userToUpdate))
                                                            .format("png")
                                                            .overwrite(value = true)).map {
        cr =>
          val cloudinaryFileVersion = cr.data.get.version
          User.updateInfo(id, CloudinaryUrl.identifier, Some(cloudinaryFileVersion))
          Redirect(routes.Users.show(id))
      } recover {
        case ex: Exception =>
          val msg = ex.getMessage
          BadRequest(views.html.users.updateImage(userToUpdate, Option("Något gick snett vid inläsningen. Prova en annan bild.")))
      }
    }
  }

  def delete(id: Long) = StackAction(AuthorityKey -> hasPermission(Administrator)) { implicit request =>
    User.delete(id)
    Redirect(routes.UsersSecured.list())
  }


  val userCreateForm: Form[User] = Form(
    mapping(
      "id" -> ignored(None:Option[Long]),
      "firstName" -> nonEmptyText(maxLength = 127),
      "lastName" -> nonEmptyText(maxLength = 127),
      "email" -> play.api.data.Forms.email.verifying("Epostadressen används av någon annan", User.findByEmail(_).isEmpty),
      "phone" -> text(maxLength = 127),
      "comment" -> text(maxLength = 127),
      "administrator" -> boolean,
      "password" -> text(maxLength = 127)
    )(toUser)(fromUser)
      .verifying("Lösenordet måste vara minst 8 tecken", user => user.password.length >= 8)
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
      .verifying("Epostadressen används av någon annan", user => User.verifyUniqueEmail(user))
      .verifying("Lösenordet måste vara minst 8 tecken", user => user.password.length >= 8)
  )

  def toUser(id: Option[Long], firstName: String, lastName: String, email: String, phone: String, comment: String, isAdministrator: Boolean, password: String): User = {
    val permission = isAdministrator match {
      case true => Administrator
      case _ => NormalUser
    }
    User(id=id, firstName=firstName.trim, lastName=lastName.trim, email=email.trim, phone=phone.trim, comment=comment.trim, permission=permission, password=password, imageProvider = GravatarUrl.identifier)
  }

  def fromUser(user: models.User) = {
    Option(user.id, user.firstName, user.lastName, user.email, user.phone, user.comment, user.permission==Administrator, User.NOT_CHANGED_PASSWORD)
  }
}

