package services

import java.util.Date

import akka.actor.{Props, Actor}
import models.{User, Event, Reminder}
import play.api.Logger
import play.api.libs.concurrent.Akka

case class CheckEvents()
case class NotifyParticipant(event: Event, user: User)
case class NotifyAllParticipants(evet: Event)

class EventReminderActor extends Actor {

  override def preStart() = {Logger.debug("my path is: " + context.self.path)}

  def receive = {
    case CheckEvents() => checkEvents()
    case NotifyParticipant(event, user) => notifyParticipant(event, user)
    case NotifyAllParticipants(event) => notifyParticipants(event)
  }

  private def checkEvents() {
    Logger.debug("Oh! It's time to check events!")

    val reminders = Reminder.findDueReminders(new Date())
    val events = (reminders map {
      _.event
    }).distinct

    notifyParticipantsForEach(events)
    cleanUpReminders(reminders)
  }

  private def notifyParticipantsForEach(events: Seq[Event]) {
    events map { event =>
      notifyParticipants(event)
    }
  }

  private def cleanUpReminders(reminders: Seq[Reminder]) {
    reminders map {reminder =>
      Reminder.delete(reminder.id.get)
    }
  }

  private def notifyParticipant(event: Event, user: User) {
    MailReminder.sendReminderMessage(event, user)
  }

  private def notifyParticipants(event: Event) {
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