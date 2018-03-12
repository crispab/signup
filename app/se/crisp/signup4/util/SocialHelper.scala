package se.crisp.signup4.util

import javax.inject.{Inject, Singleton}

import play.api.Configuration

@Singleton
class SocialHelper @Inject()(val configuration: Configuration) {
  val googleLoginConfigured: Boolean = configuration.get[String]("silhouette.google.clientID").trim.length > 0
  val facebookLoginConfigured: Boolean = configuration.get[String]("silhouette.facebook.clientID").trim.length > 0
  def socialConfigured: Boolean = googleLoginConfigured || facebookLoginConfigured
}
