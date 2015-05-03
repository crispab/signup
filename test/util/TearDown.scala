package util

import models.{User, Membership, Group}

object TearDown {

  def removeGroupAndMembers(groupName: String) = {
    val group = Group.findByName(groupName)
    if(group.isDefined) {
      Membership.findMembers(group = group.get) foreach {membership => User.delete(membership.user.id.get)}
      Group.delete(group.get.id.get)
    }
  }
}
