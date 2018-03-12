package se.crisp.signup4.services

import java.util.Date
import javax.inject.Inject

import akka.actor.Actor
import play.api.{Configuration, Logger}
import play.api.i18n.{I18nSupport, Langs, MessagesApi}
import se.crisp.signup4.models.dao.ReminderDAO
import se.crisp.signup4.models.{Event, Reminder, User}

case class CheckEvents(loggedIn: User)
case class RemindParticipant(event: Event, user: User, loggedIn: User)
case class RemindAllParticipants(event: Event, loggedIn: User)

class EventReminderActor @Inject()(val reminderDAO: ReminderDAO,
                                   val slackReminder: SlackReminder,
                                   val mailReminder:MailReminder,
                                   val configuration: Configuration,
                                   val langs: Langs,
                                   implicit val messagesApi: MessagesApi) extends Actor with I18nSupport{

  override def preStart() {Logger.debug("my path is: " + context.self.path)}

  def receive: PartialFunction[Any, Unit] = {
    case CheckEvents(loggedIn) => checkEvents(loggedIn)
    case RemindParticipant(event, user, loggedIn) => remindParticipant(event, user, loggedIn)
    case RemindAllParticipants(event, loggedIn) => remindParticipants(event, loggedIn)
  }

  private def checkEvents(loggedIn: User) {
    Logger.debug("Oh! It's time to check events!")

    val reminders = reminderDAO.findDueReminders(new Date())
    val events = (reminders map {
      _.event
    }).distinct

    remindParticipantsForEach(events, loggedIn)
    cleanUpReminders(reminders)
  }

  private def remindParticipantsForEach(events: Seq[Event], loggedIn: User) {
    events foreach { event =>
      remindParticipants(event, loggedIn)
    }
  }

  private def cleanUpReminders(reminders: Seq[Reminder]) {
    reminders foreach {reminder =>
      reminderDAO.delete(reminder.id.get)
    }
  }

  private def remindParticipant(event: Event, user: User, loggedInUser: User) {
    implicit val loggedIn: User = loggedInUser


    mailReminder.sendReminderMessage(event, user)(loggedIn, messagesApi.preferred(langs.availables))
  }

  private def remindParticipants(event: Event, loggedInUser: User) {
    implicit val loggedIn: User = loggedInUser
    mailReminder.sendReminderMessages(event)(loggedIn, messagesApi.preferred(langs.availables))
    slackReminder.sendReminderMessage(event)(loggedIn, messagesApi.preferred(langs.availables))
  }

}
