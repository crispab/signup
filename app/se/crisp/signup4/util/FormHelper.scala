package se.crisp.signup4.util

import play.api.data.Form
import play.api.i18n.Messages

object FormHelper {
  def errors[T](form: Form[T])(implicit i18messages: Messages): Seq[String] = {
    val messages:Seq[String] = form.globalErrors.map(e => e.message) union form.errors.map(e => e.message)
    messages.distinct.map(m => LocaleHelper.errMsg(m))
  }
}