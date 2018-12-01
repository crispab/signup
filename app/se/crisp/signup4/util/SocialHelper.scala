package se.crisp.signup4.util

import javax.inject.{Inject, Singleton}

import play.api.Configuration

@Singleton
class SocialHelper @Inject()(val configuration: Configuration) {
  val googleLoginConfigured: Boolean = configuration.has("silhouette.google.clientID")
  val facebookLoginConfigured: Boolean = configuration.has("silhouette.facebook.clientID")
  def socialConfigured: Boolean = googleLoginConfigured || facebookLoginConfigured
}
