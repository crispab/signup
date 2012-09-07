package models.security

sealed trait Permission
  case object Administrator extends Permission
  case object NormalUser extends Permission
