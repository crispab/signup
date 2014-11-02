package services

import java.util.Date

import akka.actor.Actor
import models.{Event, Reminder}
import play.api.Logger

class EventReminderActor extends Actor {

  def receive = {
    case EventReminderActor.CHECK_EVENTS => checkEvents()
  }

  def checkEvents() {
    Logger.debug("Oh! It's time to check events!")

    val reminders = Reminder.findDueReminders(new Date())
    val events = (reminders map {
      _.event
    }).distinct

    sendRemindersForEvents(events)
    cleanUpReminders(reminders)
  }

  def sendRemindersForEvents(events: Seq[Event]) {
    events map { event =>
      MailReminder.remindParticipants(event)
      SlackReminder.remindListeners(event)
    }
  }

  def cleanUpReminders(reminders: Seq[Reminder]) {
    reminders map {reminder =>
      Reminder.delete(reminder.id.get)
    }
  }
}


object EventReminderActor {
  val CHECK_EVENTS = "checkEvents"
}