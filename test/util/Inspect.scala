package util

import models.{User, Participation, Event}

object Inspect {

  def getNoOfOn(event: Event): Long = {
    val members = Participation.findMembers(event)
    val guests = Participation.findGuests(event)
    members.numberOn + guests.numberOn
  }

  def getStatus(member: User, event: Event): String = {
    Participation.findStatus(member, event).toString
  }

  def getComment(member: User, event: Event): String = {
    Participation.findByEventAndUser(eventId = event.id.get, userId = member.id.get).get.comment
  }

  def getUser(userName: String): User = {
    User.findByFirstName(userName).head
  }
}
