package se.crisp.signup4.util

import javax.inject.{Inject, Singleton}

import play.api.Configuration

@Singleton
class SocialHelper @Inject()(val configuration: Configuration) {
  val googleLoginConfigured: Boolean = configuration.has("silhouette.google.clientID") && !configuration.get[String]("silhouette.google.clientID").isEmpty
  val facebookLoginConfigured: Boolean = configuration.has("silhouette.facebook.clientID") && !configuration.get[String]("silhouette.facebook.clientID").isEmpty
  def socialConfigured: Boolean = googleLoginConfigured || facebookLoginConfigured
}
