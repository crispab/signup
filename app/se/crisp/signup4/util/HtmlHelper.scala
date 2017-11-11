package se.crisp.signup4.util

import play.api.Configuration
import play.api.i18n.Messages
import se.crisp.signup4.models.Event


object HtmlHelper {
  def NON_APLHA_AND_SOME_REGEXP = "[^a-z0-9\\-_]"

  def stripFromHtml(htmlString: String): String = {
    htmlString.replaceAll("\\n", " ")
              .replaceAll("&nbsp;", " ")
              .replaceAll("<[Ll][Ii]>", " * ")
              .replaceAll("\\s+", " ")
              .replaceAll("<[Bb][Rr]>", "\n")
              .replaceAll("<\\/[Pp]>", "\n\n")
              .replaceAll("<\\/[Ll][Ii]>", "\n")
              .replaceAll("(?s)<[^>]*>(\\s*<[^>]*>)*", "")
  }

  def truncatedTextFromHtml(htmlDescription: String, maxlength:Int): String = {
    val description = stripFromHtml(htmlDescription)
    if(description.length > maxlength)
      description.substring(0, maxlength) + "..."
    else
      description
  }

  def calendarDescriptionAsText(event: Event, url: String, maxlength: Int)(implicit messages: Messages): String = {
    import play.api.Play.current
    val baseUrl = play.api.Play.configuration.getString("application.base.url").getOrElse("")
    truncatedTextFromHtml("<p>" + Messages("calendar.event", baseUrl + url) + "</p>" + event.description, maxlength)
  }
}