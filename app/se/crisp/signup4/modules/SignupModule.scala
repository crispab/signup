package se.crisp.signup4.modules

import com.google.inject.AbstractModule
import play.api.libs.concurrent.AkkaGuiceSupport
import se.crisp.signup4.services.EventReminderActor

class SignupModule extends AbstractModule with AkkaGuiceSupport {
  override def configure(): Unit = {
    bindActor[EventReminderActor]("event-reminder-actor")
  }
}