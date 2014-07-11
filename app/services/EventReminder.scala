package services

import com.typesafe.plugin._
import models.Status._
import models._
import play.api.Logger
import play.api.templates.Html


object EventReminder {

  private def findReceivers(event: Event): Seq[User] = {
    val unregisteredMembers = User.findUnregisteredMembers(event)
    val unregisteredGuests = Participation.findGuestsByStatus(Unregistered, event) map {_.user}
    unregisteredMembers union unregisteredGuests
  }

  private def createEmailMessage(event: Event, user: User) : Html = {
    import play.api.Play.current
    val baseUrl = play.api.Play.configuration.getString("email.notification.base.url").getOrElse("")
    views.html.events.email(event, user, baseUrl)
  }


  def remindParticipants(event: Event) {
    Logger.debug("Sending reminders for: " + event.name)
    val receivers = findReceivers(event)
    Logger.debug("Found receivers: " + receivers)
    receivers map { receiver =>
      import play.api.Play.current
      val mailer = use[MailerPlugin].email
      mailer.setRecipient(receiver.email)
      mailer.setSubject(event.group.mailSubjectPrefix + ": " + event.name)
      mailer.setReplyTo(event.group.mailFrom)
      mailer.setFrom(event.group.mailFrom)

      val emailMessage = createEmailMessage(event, receiver)
      try {
        Logger.debug("Sending notification email for " + event.name + " to " + receiver)
        mailer.sendHtml(emailMessage.toString())
        Logger.info("DONE sending notification email for " + event.name + " to " + receiver)
      } catch {
        case ex: Exception => {
          Logger.error("FAILED sending notification email for " + event.name + " to " + receiver, ex)
          LogEntry.create(event, "Misslyckades att skicka påminnelse till " + receiver.email + ". " + ex.getClass.getSimpleName + ": " + ex.getMessage)
        }
      }
    }
    LogEntry.create(event, "Skickat påminnelse till " + receivers.size + " medlem(mar)")
  }
}
