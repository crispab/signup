package services

import models.{Event, Participation, User}
import play.api.templates.Html
import play.api.Logger
import models.Status._


object EventNotifier {
  private def findReceivers(event: Event): Seq[String] = {
    val unregisteredMembers = User.findUnregisteredMembers(event) map {_.email}
    val unregisteredGuests = Participation.findGuests(event).filter {participation => participation.status == Unregistered} map {_.user.email}
    unregisteredMembers union unregisteredGuests
  }

  private def createEmailMessage(event: Event) : Html = {
    views.html.events.email(event)
  }

  private def sendEmail(subject: String, receivers: Seq[String], email: Html) {
    import com.typesafe.plugin._
    import play.api.Play.current
    val mail = use[MailerPlugin].email
    mail.setSubject(subject)
    receivers.foreach {receiver => mail.addRecipient(receiver)}
    mail.addFrom("SignUp4 <noreply@crisp.se>")
    mail.setReplyTo("SignUp4 <noreply@crisp.se>")
    mail.sendHtml(email.toString())
  }

  def notifyParticipants(event: Event) {
    val receivers = findReceivers(event)
    val email = createEmailMessage(event)

    Logger.debug("    *-*-*-* Sending notification email for " + event.name + " to " + receivers)
    sendEmail(event.group.name + ": " + event.name, receivers, email)
  }
}
