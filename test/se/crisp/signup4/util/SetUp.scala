package se.crisp.signup4.util

import java.util
import javax.inject.{Inject, Singleton}

import se.crisp.signup4.models.dao.{EventDAO, GroupDAO, MembershipDAO, UserDAO}
import se.crisp.signup4.models.{Event, Group, User}
import se.crisp.signup4.util.TestHelper._

import scala.collection.JavaConverters


@Singleton
class SetUp @Inject() (val userDAO: UserDAO,
                       val groupDAO: GroupDAO,
                       val membershipDAO: MembershipDAO,
                       val eventDAO: EventDAO) {
  def createUsers(userNames: util.List[String]): util.List[User] = {
    JavaConverters.bufferAsJavaList(JavaConverters.asScalaBuffer(userNames) map (name => createUser(name)))
  }

  def createUser(userName: String): User = {
    val user = User(firstName = userName, lastName = userName, email = userName + "@mailinator.com")
    val id = userDAO.create(user = user)
    userDAO.find(id)
  }

  def createGroup(groupName: String): Group = {
    val group = Group(name = groupName)
    val id = groupDAO.create(group = group)
    groupDAO.find(id)
  }

  def addMembers(group: Group, members: util.List[User]): Unit = {
    JavaConverters.asScalaBuffer(members) foreach { member => membershipDAO.create(group.id.get, member.id.get)}
  }

  def createMorningEvent(group: Group, eventName: String): Event = {
    val event = Event(group = group, name = eventName, startTime = morningStart, endTime = morningEnd, lastSignUpDate = morningStart)
    val id = eventDAO.create(event = event)
    eventDAO.find(id)
  }
}
