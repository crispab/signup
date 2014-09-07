package util

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

  def escapeNewlines(string: String) = string.replaceAll("\\n", "\\\\n")

}