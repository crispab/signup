package services

import models.{Group, Event, Participation, User}
import play.api.templates.Html
import play.api.Logger
import models.Status._
import com.typesafe.plugin.{MockMailer, CommonsMailer, MailerAPI}


object EventNotifier {
  private def findReceivers(event: Event): Seq[User] = {
    val unregisteredMembers = User.findUnregisteredMembers(event)
    val unregisteredGuests = Participation.findGuests(event).filter {participation => participation.status == Unregistered} map {_.user}
    unregisteredMembers union unregisteredGuests
  }

  private def createEmailMessage(event: Event, user: User) : Html = {
    import play.api.Play.current
    val baseUrl = play.api.Play.configuration.getString("email.notification.base.url").getOrElse("")
    views.html.events.email(event, user, baseUrl)
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
    Logger.debug("Found receivers: " + receivers)
    receivers map { receiver =>
      val emailMessage = createEmailMessage(event, receiver)
      val emailSubject = event.group.name + ": " + event.name
      val mailer = createMailer(event.group)

      mailer.addRecipient(receiver.email)
      mailer.setSubject(emailSubject)

      try {
        Logger.debug("Sending notification email for " + event.name + " to " + receiver)
        mailer.sendHtml(emailMessage.toString())
        Logger.info("DONE sending notification email for " + event.name + " to " + receiver)
      } catch {
        case ex: Exception => Logger.error("FAILED sending notification email for " + event.name + " to " + receiver, ex)
      }
    }
  }
}
