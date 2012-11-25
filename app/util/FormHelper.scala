package util

import play.api.data.Form
import play.api.data.FormError

object FormHelper {
  def errors[T](form: Form[T]) : Seq[String] = {
    val errors: scala.Seq[FormError] = form.errors union form.globalErrors
    val messages:Seq[String] = errors.map(e => e.message + ": " + e.key)
    messages
  }
}