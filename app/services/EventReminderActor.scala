package services

import java.util.Date

import akka.actor.{Props, Actor}
import models.{User, Event, Reminder}
import play.api.Logger
import play.api.libs.concurrent.Akka

case class CheckEvents(loggedIn: User)
case class NotifyParticipant(event: Event, user: User, loggedIn: User)
case class NotifyAllParticipants(event: Event, loggedIn: User)

class EventReminderActor extends Actor {

  override def preStart() = {Logger.debug("my path is: " + context.self.path)}

  def receive = {
    case CheckEvents(loggedIn) => checkEvents(loggedIn)
    case NotifyParticipant(event, user, loggedIn) => notifyParticipant(event, user, loggedIn)
    case NotifyAllParticipants(event, loggedIn) => notifyParticipants(event, loggedIn)
  }

  private def checkEvents(loggedIn: User) {
    Logger.debug("Oh! It's time to check events!")

    val reminders = Reminder.findDueReminders(new Date())
    val events = (reminders map {
      _.event
    }).distinct

    notifyParticipantsForEach(events, loggedIn)
    cleanUpReminders(reminders)
  }

  private def notifyParticipantsForEach(events: Seq[Event], loggedIn: User) {
    events foreach { event =>
      notifyParticipants(event, loggedIn)
    }
  }

  private def cleanUpReminders(reminders: Seq[Reminder]) {
    reminders foreach {reminder =>
      Reminder.delete(reminder.id.get)
    }
  }

  private def notifyParticipant(event: Event, user: User, loggedInUser: User) {
    implicit val loggedIn: User = loggedInUser
    MailReminder.sendReminderMessage(event, user)
  }

  private def notifyParticipants(event: Event, loggedInUser: User) {
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