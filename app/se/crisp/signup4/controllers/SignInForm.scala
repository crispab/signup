package se.crisp.signup4.controllers

import play.api.data.Form
import play.api.data.Forms._

object SignInForm {

  val form = Form(
    mapping(
      "email" -> email,
      "password" -> nonEmptyText,
      "rememberMe" -> boolean
    )(Data.apply)(Data.unapply)
  )

  case class Data(
    email: String,
    password: String,
    rememberMe: Boolean)
}
