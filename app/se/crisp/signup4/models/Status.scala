package se.crisp.signup4.models

object Status extends Enumeration {
  type Status = Value
  val On, Maybe, Off, Unregistered = Value
}
