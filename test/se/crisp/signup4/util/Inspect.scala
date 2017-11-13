package se.crisp.signup4.util

import javax.inject.{Inject, Singleton}

import se.crisp.signup4.models.dao.{EventDAO, GroupDAO, ParticipationDAO, UserDAO}
import se.crisp.signup4.models.{Event, Group, User}

@Singleton
class Inspect @Inject() (val participationDAO: ParticipationDAO,
                         val userDAO: UserDAO,
                         groupDAO: GroupDAO,
                         eventDAO: EventDAO) {

  def getNoOfOn(event: Event): Long = {
    val members = participationDAO.findMembers(event)
    val guests = participationDAO.findGuests(event)
    members.numberOn + guests.numberOn
  }

  def getStatus(member: User, event: Event): String = {
    participationDAO.findStatus(member, event).toString
  }

  def getComment(member: User, event: Event): String = {
    participationDAO.findByEventAndUser(eventId = event.id.get, userId = member.id.get).get.comment
  }

  def getUser(userName: String): User = {
    userDAO.findByFirstName(userName).head
  }

  def getGroup(groupName: String): Group = {
    groupDAO.findByName(groupName).get
  }

  def getEvent(eventName: String, groupName: String): Event = {
    val group = getGroup(groupName)
    val events = eventDAO.findAllEventsByGroup(group)
    events.find(event => event.name == eventName).get
  }

  def isEventAvailableForGroup(eventName: String, groupName: String): Boolean = {
    val group = getGroup(groupName)
    val events = eventDAO.findAllEventsByGroup(group)
    events.exists(event => event.name == eventName)
  }
}
