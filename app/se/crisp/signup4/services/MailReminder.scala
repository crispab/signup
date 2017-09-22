package se.crisp.signup4.services

import play.api.libs.mailer._
import se.crisp.signup4.models.Status._
import se.crisp.signup4.models._
import play.api.Logger
import play.api.i18n.Messages
import play.twirl.api.Html
import se.crisp.signup4.models.User
import se.crisp.signup4.util.ThemeHelper._


object MailReminder {
  def sendMessages(event: Event, receivers: Seq[User], createMessage: (Event, User) => Html)(implicit loggedIn: User) {
    Logger.debug("Sending messages for: " + event.name)
    receivers foreach { receiver =>
      sendMessage(event, receiver, createMessage)
    }
    LogEntry.create(event, Messages("mail.sentreminders", loggedIn.name, receivers.size))
  }

  private def sendMessage(event: Event, receiver: User, createMessage: (Event, User) => Html) {
    val emailMessage = createMessage(event, receiver)
    val email = Email(
      subject = event.group.mailSubjectPrefix + ": " + event.name,
      from = event.group.mailFrom,
      to = Seq(receiver.email),
      bodyHtml = Some(emailMessage.toString()),
      replyTo = Some(event.group.mailFrom)
    )

    try {
      Logger.debug("Sending email for " + event.name + " to " + receiver)
      import play.api.Play.current
      MailerPlugin.send(email)
      Logger.info("DONE sending email for " + event.name + " to " + receiver)
    } catch {
      case ex: Exception =>
        Logger.error("FAILED sending email for " + event.name + " to " + receiver, ex)
        LogEntry.create(event, Messages("mail.failedreminder", receiver.email, ex.getClass.getSimpleName, ex.getMessage))
    }
  }

  private def findReceiversToRemind(event: Event): Seq[User] = {
    val unregisteredMembers = User.findUnregisteredMembers(event)
    val unregisteredGuests = Participation.findGuestsByStatus(Unregistered, event) map {_.user}
    val maybeMembers = Participation.findMembersByStatus(Maybe, event) map {_.user}
    val maybeGuests = Participation.findGuestsByStatus(Maybe, event) map {_.user}

    unregisteredMembers union unregisteredGuests union maybeMembers union maybeGuests
  }

  private def createReminderMessage(event: Event, user: User): Html = {
    import play.api.Play.current
    val baseUrl = play.api.Play.configuration.getString("application.base.url").getOrElse("")

    // TODO: get rid of this by using SendGrid mail templates instead
    if (THEME == "b73") {
      se.crisp.signup4.views.html.events.b73.emailremindermessage(event, user, baseUrl)
    } else {
      se.crisp.signup4.views.html.events.crisp.emailremindermessage(event, user, baseUrl)
    }
  }


  def sendReminderMessages(event: Event)(implicit loggedIn: User) {
    sendMessages(event, findReceiversToRemind(event), createReminderMessage)
  }

  def sendReminderMessage(event: Event, user: User)(implicit loggedIn: User) {
    sendMessage(event, user, createReminderMessage)
    LogEntry.create(event, Messages("mail.sentonereminder", loggedIn.name, user.name))
  }

  private def findReceiversToCancel(event: Event): Seq[User] = {
    val guestLists = Participation.findGuests(event)
    val guests = (guestLists.on map {_.user}) union (guestLists.maybe map {_.user}) union (guestLists.off map {_.user}) union (guestLists.unregistered map {_.user})
    val members = Membership.findMembers(event.group) map {_.user}
    members union guests
  }

  private def createCancellationMessage(event: Event, user: User): Html = {
    import play.api.Play.current
    val baseUrl = play.api.Play.configuration.getString("application.base.url").getOrElse("")

    // TODO: get rid of this by using SendGrid mail templates instead
    if (THEME == "b73") {
      se.crisp.signup4.views.html.events.b73.emailcancellationmessage(event, user, baseUrl)
    } else {
      se.crisp.signup4.views.html.events.crisp.emailcancellationmessage(event, user, baseUrl)
    }
  }

  def sendCancellationMessage(event: Event)(implicit loggedIn: User) {
    sendMessages(event, findReceiversToCancel(event), createCancellationMessage)
  }
}
