package se.crisp.signup4.util

import javax.inject.{Inject, Singleton}

import play.api.Configuration

@Singleton
class ThemeHelper @Inject() (val configuration: Configuration) {
  val THEME: String = configuration.get[String]("application.theme")
  val APPLICATION_NAME: String = configuration.get[String]("application.name")

  def forTheme(string: String): String = {
    string.replaceAll("\\[THEME\\]", THEME)
  }
}
