package util

object ThemeHelper {
  import play.api.Play.current
  val THEME = play.api.Play.configuration.getString("application.theme").getOrElse("crisp")

  def forTheme(string: String) = {
    string.replaceAll("\\[THEME\\]", THEME)
  }
}
