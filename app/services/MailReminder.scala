package services

import com.typesafe.plugin._
import models.Status._
import models._
import play.api.Logger
import play.twirl.api.Html
import util.ThemeHelper._


object MailReminder {
  def sendMessages(event: Event, receivers: Seq[User], createMessage: (Event, User) => Html)(implicit loggedIn: User) {
    Logger.debug("Sending messages for: " + event.name)
    receivers foreach { receiver =>
      sendMessage(event, receiver, createMessage)
    }
    LogEntry.create(event, loggedIn.name + " har skickat påminnelse till " + receivers.size + " medlem(mar)")
  }

  private def sendMessage(event: Event, receiver: User, createMessage: (Event, User) => Html) {
    import play.api.Play.current
    val mailer = use[MailerPlugin].email
    mailer.setRecipient(receiver.email)
    mailer.setSubject(event.group.mailSubjectPrefix + ": " + event.name)
    mailer.setReplyTo(event.group.mailFrom)
    mailer.setFrom(event.group.mailFrom)

    val emailMessage = createMessage(event, receiver)
    try {
      Logger.debug("Sending email for " + event.name + " to " + receiver)
      mailer.sendHtml(emailMessage.toString())
      Logger.info("DONE sending email for " + event.name + " to " + receiver)
    } catch {
      case ex: Exception =>
        Logger.error("FAILED sending email for " + event.name + " to " + receiver, ex)
        LogEntry.create(event, "Misslyckades att skicka påminnelse till " + receiver.email + ". " + ex.getClass.getSimpleName + ": " + ex.getMessage)
    }
  }

  private def findReceiversToRemind(event: Event): Seq[User] = {
    val unregisteredMembers = User.findUnregisteredMembers(event)
    val unregisteredGuests = Participation.findGuestsByStatus(Unregistered, event) map {_.user}
    val maybeMembers = Participation.findMembersByStatus(Maybe, event) map {_.user}
    val maybeGuests = Participation.findGuestsByStatus(Maybe, event) map {_.user}

    unregisteredMembers union unregisteredGuests union maybeMembers union maybeGuests
  }

  private def createReminderMessage(event: Event, user: User) : Html = {
    import play.api.Play.current
    val baseUrl = play.api.Play.configuration.getString("application.base.url").getOrElse("")

    // TODO: get rid of this by using SendGrid mail templates instead
    if(THEME == "b73") {
      views.html.events.b73.emailnotificationmessage(event, user, baseUrl)
    } else {
      views.html.events.crisp.emailnotificationmessage(event, user, baseUrl)
    }
  }


  def sendReminderMessages(event: Event)(implicit loggedIn: User) {
    sendMessages(event, findReceiversToRemind(event), createReminderMessage)
  }

  def sendReminderMessage(event: Event, user: User)(implicit loggedIn: User) {
    sendMessage(event, user, createReminderMessage)
    LogEntry.create(event, loggedIn.name + " har skickat påminnelse till " + user.firstName + " " + user.lastName)
  }

  private def findReceiversToCancel(event: Event): Seq[User] = {
    val guestLists = Participation.findGuests(event)
    val guests = (guestLists.on map {_.user}) union (guestLists.maybe map {_.user}) union (guestLists.off map {_.user}) union (guestLists.unregistered map {_.user})
    val members = Membership.findMembers(event.group) map {_.user}
    members union guests
  }

  private def createCancellationMessage(event: Event, user: User) : Html = {
    import play.api.Play.current
    val baseUrl = play.api.Play.configuration.getString("application.base.url").getOrElse("")

    // TODO: get rid of this by using SendGrid mail templates instead
    if(THEME == "b73") {
      views.html.events.b73.emailcancellationmessage(event, user, baseUrl)
    } else {
      views.html.events.crisp.emailcancellationmessage(event, user, baseUrl)
    }
  }

  def sendCancellationMessage(event: Event)(implicit loggedIn: User) = {
    sendMessages(event, findReceiversToCancel(event), createCancellationMessage)
  }
}
