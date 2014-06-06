package util

object HtmlHelper {
  def NON_APLHA_AND_SOME_REGEXP = "[^a-z0-9\\-_]"

  def stripFromHtml(htmlString: String): String = {
    htmlString.replaceAll("<[Bb][Rr]>", "\n")
              .replaceAll("<\\/[Pp]>", "\n")
              .replaceAll("<\\/[Ll][Ii]>", "\n")
              .replaceAll("<[Ll][Ii]>", " * ")
              .replaceAll("(?s)<[^>]*>(\\s*<[^>]*>)*", "")
  }

}