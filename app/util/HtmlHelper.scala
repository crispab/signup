package util

import models.Status

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

  def asCssClass(status: models.Status.Value): String = {
    status match {
      case Status.On => "participation-on"
      case Status.Maybe => "participation-maybe"
      case Status.Off => "participation-off"
      case Status.Unregistered => "participation-unregistered"
      case _ => ""
    }
  }

  def asMessage(status: models.Status.Value): String = {
    status match {
      case Status.On => "Kommer"
      case Status.Maybe => "Kanske"
      case Status.Off => "Kommer inte"
      case Status.Unregistered => "Har inte svarat"
      case _ => ""
    }
  }



}