package util

import se.crisp.signup4.models.{Event, Group, Participation, User}
import se.crisp.signup4.models.User

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

  def getGroup(groupName: String): Group = {
    Group.findByName(groupName).get
  }

  def getEvent(eventName: String, groupName: String): Event = {
    val group = Inspect.getGroup(groupName)
    val events = Event.findAllEventsByGroup(group)
    events.find(event => event.name == eventName).get
  }

  def isEventAvailableForGroup(eventName: String, groupName: String): Boolean = {
    val group = Inspect.getGroup(groupName)
    val events = Event.findAllEventsByGroup(group)
    events.exists(event => event.name == eventName)
  }
}
