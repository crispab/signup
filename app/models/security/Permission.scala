package models.security

sealed trait Permission
case object Administrator extends Permission
case object NormalUser extends Permission

object Permission {
  def withName(name:String):Permission = {
    name match {
      case "Administrator" => Administrator
      case "NormalUser" => NormalUser
      case _ => throw new IllegalArgumentException("Unknown Permission type: " + name)
    }
  }
}
