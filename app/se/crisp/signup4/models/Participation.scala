package se.crisp.signup4.models

import java.util.Date

import org.joda.time.DateTime
import se.crisp.signup4.models.Status._

case class Participation(id: Option[Long] = None,
                         status: Status = On,
                         numberOfParticipants: Int = 1,
                         comment: String = "",
                         user: User,
                         event: Event,
                         signUpTime: Option[Date]) extends Ordered[Participation] {

  def this(id: Option[Long],
            status: Status,
            numberOfParticipants: Int,
            comment: String,
            user: User,
            event: Event) = this(id, status, numberOfParticipants, comment, user, event, signUpTime = Some(new Date()))

  def this(status: Status, user: User, event: Event) = this(None, status, 1, "", user, event, Some(new Date()))

  def compare(that: Participation): Int = this.user.compare(that.user)

  def participantsComing: Int = {
    status match {
      case Status.On => numberOfParticipants
      case Status.Maybe => numberOfParticipants
      case _ => 0
    }
  }

  def isLateSignUp: Boolean = {
    if(status != On || signUpTime.isEmpty) {
      false
    } else {
      val lastSignUpDate = new DateTime(event.lastSignUpDate).withTimeAtStartOfDay()
      val signUpDate = new DateTime(signUpTime.get).withTimeAtStartOfDay()
      signUpDate.isAfter(lastSignUpDate)
    }
  }
}
