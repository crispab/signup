package util

import models.Status
import play.api.i18n.{Lang, Messages}

object StatusHelper {

  def asMessage(status: models.Status.Value)(implicit lang: Lang): String = {
    status match {
      case Status.On => Messages("status.on")
      case Status.Maybe => Messages("status.maybe")
      case Status.Off => Messages("status.off")
      case Status.Unregistered => Messages("status.unregistered")
    }
  }

  def asCssClass(status: models.Status.Value): String = {
    status match {
      case Status.On => "participation-on"
      case Status.Maybe => "participation-maybe"
      case Status.Off => "participation-off"
      case Status.Unregistered => "participation-unregistered"
    }
  }

}
