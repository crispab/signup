package controllers

import com.nimbusds.jose.JWSObject
import jp.t2v.lab.play2.auth.{LoginLogout, OptionalAuthElement}
import models.User
import org.apache.commons.codec.digest.DigestUtils
import play.api.libs.json.Json
import play.api.libs.ws.WS
import play.api.mvc.{Action, Controller}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, ExecutionContext, Future}


// TODO: Gör alla @crisp.se till Admininstrtor i produktionsdatabasen

// TODO lägg till env var i herokuconf.sh + heroku.md

object GoogleAuth extends Controller with LoginLogout with OptionalAuthElement with AuthConfigImpl {

  private val GOOGLE_AUTHENTICATION_URL = "https://accounts.google.com/o/oauth2/auth"
  private val GOOGLE_TOKEN_URL = "https://accounts.google.com/o/oauth2/token"

  import play.api.Play.current
  private lazy val GOOGLE_CLIENT_ID = play.api.Play.configuration.getString("google.client.id")
  private lazy val GOOGLE_CLIENT_SECRET = play.api.Play.configuration.getString("google.client.secret")

  def isConfigured = GOOGLE_CLIENT_ID.isDefined && GOOGLE_CLIENT_SECRET.isDefined

  def authenticate = Action { implicit request =>
    val randomString = DigestUtils.md5Hex(Math.random().toString)

    import com.netaporter.uri.dsl._
    val requestAuthenticationTokenURL = GOOGLE_AUTHENTICATION_URL.addParams(
      "response_type" -> "code" ::
      "client_id" -> GOOGLE_CLIENT_ID.get ::
      "redirect_uri" -> "http://localhost:9000/google/callback" ::
      "state" -> randomString ::
      "scope" -> "openid email" :: Nil
    )

    Redirect(requestAuthenticationTokenURL)
  }

  def callback(error: Option[String], state: String, code: Option[String]) = Action.async { implicit request =>
    if(code.isDefined) {
      val callToGoogle = WS.url(GOOGLE_TOKEN_URL).withHeaders("Accept" -> "application/json").post(Map(
        "code" -> Seq(code.get),
        "client_id" -> Seq(GOOGLE_CLIENT_ID.get),
        "client_secret" -> Seq(GOOGLE_CLIENT_SECRET.get),
        "redirect_uri" -> Seq("http://localhost:9000/google/callback"),
        "grant_type" -> Seq("authorization_code")
      )).map { response =>
        val idTokenJwt = (response.json \ "id_token").as[String]
        val idToken = Json.parse(JWSObject.parse(idTokenJwt).getPayload.toString)
        val email = (idToken \ "email").as[String]
        email
      }

      // TODO: try - catch
      import scala.concurrent.duration._
      import scala.language.postfixOps
      val email = Await.result(callToGoogle, 60 seconds)
      val user = User.findByEmail(email)
      if(user.isDefined) {
        gotoLoginSucceeded(user.get.id.get)
      } else {
        val errorMessage = "Det finns ingen användare med epostadressen " + email
        Future.successful(
          Redirect(routes.Application.loginForm()).flashing(("error" , errorMessage))
        )
      }
    }
    else {
      val errorMessage = "Det gick inte att logga in via Google: " + error.getOrElse("okänt fel")
      Future.successful(
        Redirect(routes.Application.loginForm()).flashing(("error" , errorMessage))
      )
    }
  }

}
