package se.crisp.signup4.models

import java.util

import org.joda.time.DateTime

import se.crisp.signup4.models.EventStatus._

case class Event(
                  id: Option[Long] = None,
                  group: Group,
                  name: String,
                  description: String = "",
                  startTime: util.Date,
                  endTime: util.Date,
                  lastSignUpDate: util.Date,
                  venue: String = "",
                  allowExtraFriends: Boolean = false,
                  eventStatus: EventStatus = Created,
                  maxParticipants: Option[Int] = None,
                  cancellationReason: Option[String] = None
                ) {

  def lastSignupDatePassed(): Boolean = {
    val today = new DateTime().withTimeAtStartOfDay
    val lastSignUpDay = new DateTime(lastSignUpDate).withTimeAtStartOfDay
    today.isAfter(lastSignUpDay)
  }

  def isCancelled: Boolean = eventStatus == Cancelled
}
