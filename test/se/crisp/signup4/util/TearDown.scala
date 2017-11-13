package se.crisp.signup4.util

import javax.inject.{Inject, Singleton}

import se.crisp.signup4.models.dao.{GroupDAO, MembershipDAO, UserDAO}

@Singleton
class TearDown @Inject() (val groupDAO: GroupDAO,
                          val membershipDAO: MembershipDAO,
                          val userDAO: UserDAO) {

  def removeGroupAndMembers(groupName: String): Unit = {
    val group = groupDAO.findByName(groupName)
    if(group.isDefined) {
      membershipDAO.findMembers(group = group.get) foreach {membership => userDAO.delete(membership.user.id.get)}
      groupDAO.delete(group.get.id.get)
    }
  }
}
