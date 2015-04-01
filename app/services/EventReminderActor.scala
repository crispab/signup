package services

import java.util.Date

import akka.actor.{Props, Actor}
import models.{User, Event, Reminder}
import play.api.Logger
import play.api.libs.concurrent.Akka

case class CheckEvents(loggedIn: User)
case class RemindParticipant(event: Event, user: User, loggedIn: User)
case class RemindAllParticipants(event: Event, loggedIn: User)

class EventReminderActor extends Actor {

  override def preStart() = {Logger.debug("my path is: " + context.self.path)}

  def receive = {
    case CheckEvents(loggedIn) => checkEvents(loggedIn)
    case RemindParticipant(event, user, loggedIn) => remindParticipant(event, user, loggedIn)
    case RemindAllParticipants(event, loggedIn) => remindParticipants(event, loggedIn)
  }

  private def checkEvents(loggedIn: User) {
    Logger.debug("Oh! It's time to check events!")

    val reminders = Reminder.findDueReminders(new Date())
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
      Reminder.delete(reminder.id.get)
    }
  }

  private def remindParticipant(event: Event, user: User, loggedInUser: User) {
    implicit val loggedIn: User = loggedInUser
    MailReminder.sendReminderMessage(event, user)
  }

  private def remindParticipants(event: Event, loggedInUser: User) {
    implicit val loggedIn: User = loggedInUser
    MailReminder.sendReminderMessages(event)
    SlackReminder.sendReminderMessage(event)
  }

}


object EventReminderActor {

  val ACTOR_NAME = "EventReminder"

  def create() = {
    import play.api.Play.current
    Akka.system.actorOf(Props[EventReminderActor], name = ACTOR_NAME)
  }

  def instance() = {
    import play.api.Play.current
    Akka.system.actorSelection(path = "akka://application/user/" + ACTOR_NAME)
  }
}