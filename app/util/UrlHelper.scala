package util

object UrlHelper {
  def NON_APLHA_AND_SOME_REGEXP = "[^a-z0-9\\-_]"

  def toPathElement(s: String): String = {
    s.toLowerCase.replace('å', 'a').replace('ä', 'a').replace('ö', 'o').replaceAll(NON_APLHA_AND_SOME_REGEXP, "")
  }

}