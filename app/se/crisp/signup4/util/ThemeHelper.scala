package se.crisp.signup4.util

import javax.inject.{Inject, Singleton}

import play.api.Configuration

@Singleton
class ThemeHelper @Inject() (val configuration: Configuration) {
  val THEME: String = configuration.getString("application.theme").getOrElse("crisp")
  val APPLICATION_NAME: String = configuration.getString("application.name").getOrElse("SignUp")

  def forTheme(string: String): String = {
    string.replaceAll("\\[THEME\\]", THEME)
  }
}
