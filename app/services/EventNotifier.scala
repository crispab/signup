package services

import models.{Group, Event, Participation, User}
import play.api.templates.Html
import play.api.Logger
import models.Status._
import com.typesafe.plugin.{MockMailer, CommonsMailer, MailerAPI}


object EventNotifier {
  private def findReceivers(event: Event): Seq[String] = {
    val unregisteredMembers = User.findUnregisteredMembers(event) map {_.email}
    val unregisteredGuests = Participation.findGuests(event).filter {participation => participation.status == Unregistered} map {_.user.email}
    unregisteredMembers union unregisteredGuests
  }

  private def createEmailMessage(event: Event) : Html = {
    import play.api.Play.current
    val baseUrl = play.api.Play.configuration.getString("email.notification.base.url").getOrElse("")
    views.html.events.email(event, baseUrl)
  }


  def createMailer(group: Group): MailerAPI = {
    import play.api.Play.current
    val useMock = play.api.Play.configuration.getBoolean("smtp.mock").getOrElse(true)
    if (useMock) {
      Logger.info("Using MockMailer! Property smtp.mock is NOT set to 'false' in application.conf")
      MockMailer
    } else {
      createSmtpMailer(group)
    }
  }

  def createSmtpMailer(group: Group): MailerAPI = {
    val smtp_user = {if (group.smtp_user == null || group.smtp_user.length == 0) None else Option(group.smtp_user)}
    val smtp_password = {if (group.smtp_password == null || group.smtp_password.length == 0) None else Option(group.smtp_password)}
    val mailer = new CommonsMailer(smtpHost = group.smtp_host,
                                   smtpPort = group.smtp_port,
                                   smtpSsl = group.smtp_useSsl,
                                   smtpUser = smtp_user,
                                   smtpPass = smtp_password)
    mailer.setReplyTo(group.smtp_from)
    mailer.addFrom(group.smtp_from)
    mailer
  }

  def notifyParticipants(event: Event) {
    val receivers = findReceivers(event)
    if (!receivers.isEmpty) {
      val emailMessage = createEmailMessage(event)
      val emailSubject = event.group.name + ": " + event.name
      val mailer = createMailer(event.group)

      receivers.foreach {receiver => mailer.addRecipient(receiver)}
      mailer.setSubject(emailSubject)

      Logger.debug("    *-*-*-* Sending notification email for " + event.name + " to " + receivers)
      mailer.sendHtml(emailMessage.toString())
    } else {
      Logger.debug("    *-*-*-* No participants need to be notifies about " + event.name)
    }
  }
}
