package controllers

import jp.t2v.lab.play2.auth.{AuthElement, OptionalAuthElement}
import models.security.Administrator
import models.{Event, Participation, User}
import models.Status._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import util.AuthHelper._

object Participations extends Controller with OptionalAuthElement with AuthConfigImpl {

  def editForm(eventId: Long, userId: Long) = StackAction { implicit request =>
    val event = Event.find(eventId)
    if(!event.isCancelled) {
      val userToAttend = User.find(userId)
      val participation = Participation.findByEventAndUser(eventId, userId).getOrElse(Participation(status = On, user = userToAttend, event = event))
      Ok(views.html.participations.edit(participationForm.fill(participation), userToAttend, event))
    } else {
      Redirect(routes.Events.show(eventId)).flashing("error" -> "Eventet är inställt. Det går inte att anmäla sig.")
    }
  }

  def createOrUpdate = StackAction { implicit request =>
    participationForm.bindFromRequest.fold(
      formWithErrors => {
        val event = Event.find(formWithErrors("eventId").value.get.toLong)
        val userToAttend = User.find(formWithErrors("userId").value.get.toLong)
        BadRequest(views.html.participations.edit(formWithErrors, userToAttend, event))
      },
      participation => {
        val existingParticipation = Participation.findByEventAndUser(participation.event.id.get, participation.user.id.get)
        if (existingParticipation.isEmpty) {
          Participation.create(participation)
        } else {
          Participation.update(existingParticipation.get.id.get, participation)
        }
        Redirect(routes.Events.show(participation.event.id.get))
      }
    )
  }

  val participationForm:Form[Participation] =
    Form(
      mapping(
        "id" -> ignored(None:Option[Long]),
        "status" -> nonEmptyText(maxLength = 20),
        "number_of_participants" -> number(min = 1),
        "comment" -> text(maxLength = 127),
        "userId" -> longNumber,
        "eventId" -> longNumber
      )(toParticipation)(fromParticipation)
        .verifying("Eventet är inställt. Det går inte att anmäla sig.", participation => !participation.event.isCancelled)
    )

  def toParticipation(
                       id: Option[Long],
                       status: String,
                       numberOfParticipants: Int,
                       comment: String,
                       userId: Long,
                       eventId: Long): Participation = {

    Participation(
      id = id,
      status = models.Status.withName(status),
      numberOfParticipants = numberOfParticipants,
      comment = comment,
      user = User.find(userId),
      event = Event.find(eventId)
    )
  }

  def fromParticipation(participation: Participation) = {
    Option((participation.id, participation.status.toString, participation.numberOfParticipants, participation.comment, participation.user.id.get, participation.event.id.get))
  }
}



object ParticipationsSecured extends Controller with AuthElement with AuthConfigImpl {
  def createGuestForm(eventId: Long) = StackAction(AuthorityKey -> hasPermission(Administrator)_) { implicit request =>
    implicit val loggedInUser = Option(loggedIn)
    val event = Event.find(eventId)
    Ok(views.html.participations.addGuest(Participations.participationForm, event, User.findNonGuests(event.id.get)))
  }

  def createGuest = StackAction(AuthorityKey -> hasPermission(Administrator)_) { implicit request =>
    implicit val loggedInUser = Option(loggedIn)
    Participations.participationForm.bindFromRequest.fold(
        formWithErrors => {
          val event = Event.find(formWithErrors("eventId").value.get.toLong)
          BadRequest(views.html.participations.addGuest(formWithErrors, event, User.findNonGuests(event.id.get)))
        },
        participation => {
          Participation.createGuest(participation.event.id.get, participation.user.id.get)
          Redirect(routes.Participations.editForm(participation.event.id.get, participation.user.id.get))
        }
      )
  }

  def delete(id: Long) = StackAction(AuthorityKey -> hasPermission(Administrator)_) { implicit request =>
    val participation = Participation.find(id)
    val event = participation.event
    Participation.delete(participation.id.get)
    Redirect(routes.Events.show(event.id.get))
  }
}

