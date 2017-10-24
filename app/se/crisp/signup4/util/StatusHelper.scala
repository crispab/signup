package se.crisp.signup4.util

import play.api.i18n.Messages
import se.crisp.signup4.models.Status

object StatusHelper {

  def asMessage(status: se.crisp.signup4.models.Status.Value)(implicit messages: Messages): String = {
    status match {
      case Status.On => Messages("status.on")
      case Status.Maybe => Messages("status.maybe")
      case Status.Off => Messages("status.off")
      case Status.Unregistered => Messages("status.unregistered")
    }
  }

  def asCssClass(status: se.crisp.signup4.models.Status.Value): String = {
    status match {
      case Status.On => "participation-on"
      case Status.Maybe => "participation-maybe"
      case Status.Off => "participation-off"
      case Status.Unregistered => "participation-unregistered"
    }
  }

}
