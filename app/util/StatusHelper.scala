package util

import models.Status

object StatusHelper {

  def asMessage(status: models.Status.Value): String = {
    status match {
      case Status.On => "Kommer"
      case Status.Maybe => "Kanske"
      case Status.Off => "Kommer inte"
      case Status.Unregistered => "Har inte svarat"
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
