package se.crisp.signup4.util

object ThemeHelper {
  import play.api.Play.current
  val THEME: String = play.api.Play.configuration.getString("application.theme").getOrElse("crisp")
  val APPLICATION_NAME: String = play.api.Play.configuration.getString("application.name").getOrElse("SignUp")

  def forTheme(string: String): String = {
    string.replaceAll("\\[THEME\\]", THEME)
  }
}
