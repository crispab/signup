package services

import akka.actor.Actor
import models.Event

class EventNotifierActor(event: Event) extends Actor {

  protected def receive = {
    case "sendNotifications" => {
      EventNotifier.notifyParticipants(event)
    }
  }
}


object EventNotifierActor {
  def schedule(event: Event) {
    // todo: create actor
    // todo: use akka scheduler to call actor
  }
}