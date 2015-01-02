package util

object ThemeHelper {
  import play.api.Play.current
  val THEME = play.api.Play.configuration.getString("application.theme").getOrElse("crisp")
  val APPLICATION_NAME = play.api.Play.configuration.getString("application.name").getOrElse("SignUp")

  def forTheme(string: String) = {
    string.replaceAll("\\[THEME\\]", THEME)
  }
}
