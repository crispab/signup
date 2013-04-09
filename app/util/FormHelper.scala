package util

import play.api.data.Form

object FormHelper {
  def errors[T](form: Form[T]) : Seq[String] = {
    val messages:Seq[String] = form.globalErrors.map(e => e.message) union  form.errors.map(e => e.message)
    messages.distinct
  }
}