package util

object EmailHelper {
  def abbreviated(email: String, isLoggedIn: Boolean = false): String = (isLoggedIn, email.indexOf('@')) match {
    case (true, _) => email
    case (_, -1) => ""
    case _ => "at " + email.substring(email.indexOf('@')+1)
  }
}
