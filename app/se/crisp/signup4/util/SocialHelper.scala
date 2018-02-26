package se.crisp.signup4.util

import javax.inject.{Inject, Singleton}

import play.api.Configuration

@Singleton
class SocialHelper @Inject()(val configuration: Configuration) {
  val googleLoginConfigured: Boolean = configuration.getString("silhouette.google.clientID").getOrElse("").trim.length > 0
  val facebookLoginConfigured: Boolean = configuration.getString("silhouette.facebook.clientID").getOrElse("").trim.length > 0
  def socialConfigured: Boolean = googleLoginConfigured || facebookLoginConfigured
}
