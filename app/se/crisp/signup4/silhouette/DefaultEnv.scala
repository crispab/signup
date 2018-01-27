package se.crisp.signup4.silhouette

import com.mohiva.play.silhouette.api.Env
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import se.crisp.signup4.models.User

trait DefaultEnv extends Env {
  type I = User
  type A = CookieAuthenticator
}
