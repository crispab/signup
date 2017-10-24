package se.crisp.signup4.controllers

import javax.inject.Inject

import jp.t2v.lab.play2.auth.{LoginLogout, OptionalAuthElement}
import org.apache.commons.codec.digest.DigestUtils
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.libs.ws.WS
import play.api.mvc.{Action, AnyContent, Controller}
import se.crisp.signup4.models.User
import se.crisp.signup4.util.ThemeHelper._
import se.crisp.signup4.util.WsHelper._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.language.postfixOps


class FacebookAuth @Inject()( val messagesApi: MessagesApi) extends Controller with LoginLogout with OptionalAuthElement with AuthConfigImpl with I18nSupport{

  private val FACEBOOK_AUTHENTICATION_URL = "https://graph.facebook.com/oauth/authorize"
  private val FACEBOOK_TOKEN_URL = "https://graph.facebook.com/v2.8/oauth/access_token"
  private val FACEBOOK_GET_EMAIL_URL = "https://graph.facebook.com/me"

  import play.api.Play.current


  def authenticate = Action { implicit request =>
    val randomString = DigestUtils.md5Hex(Math.random().toString)

    val callbackUrl = routes.FacebookAuth.callback().absoluteURL()
    import com.netaporter.uri.dsl._
    val requestAuthenticationTokenUrl = FACEBOOK_AUTHENTICATION_URL.addParams(
      "response_type" -> "code" ::
        "client_id" -> FacebookAuth.FACEBOOK_CLIENT_ID.get ::
        "redirect_uri" -> callbackUrl ::
        "state" -> randomString ::
        "scope" -> "email" :: Nil
    )

    Redirect(requestAuthenticationTokenUrl)
  }

  def callback(error: Option[String] = None, state: Option[String] = None, code: Option[String] = None): Action[AnyContent] = Action.async { implicit request =>
    if (code.isDefined) {
      val callbackUrl = routes.FacebookAuth.callback().absoluteURL()
      val access_token = requestAccessToken(code.get, callbackUrl)

      val email = getEmailAddress(access_token)

      val user = User.findByEmail(email)
      if (user.isDefined) {
        gotoLoginSucceeded(user.get.id.get)
      } else {
        val errorMessage = Messages("login.nonexistentemail", email, APPLICATION_NAME)
        Future.successful(
          Redirect(routes.Application.loginForm()).flashing(("error", errorMessage))
        )
      }
    } else {
      val cause = error.getOrElse(Messages("login.unknownerror"))
      val errorMessage = Messages("login.facebookfail", cause)
      Future.successful(
        Redirect(routes.Application.loginForm()).flashing(("error", errorMessage))
      )
    }
  }

  private def requestAccessToken(code: String, callbackUrl: String): String = {
    val callToFacebook = WS.url(FACEBOOK_TOKEN_URL).withHeaders("Accept" -> "application/json").post(Map(
      "code" -> Seq(code),
      "client_id" -> Seq(FacebookAuth.FACEBOOK_CLIENT_ID.get),
      "client_secret" -> Seq(FacebookAuth.FACEBOOK_CLIENT_SECRET.get),
      "redirect_uri" -> Seq(callbackUrl),
      "grant_type" -> Seq("authorization_code"),
      "scope" -> Seq("email")
    )).map { response =>
      onOkResponse(response) {
        (response.json \ "access_token").as[String]
      }
    }

    Await.result(callToFacebook, 60 seconds)
  }

  private def getEmailAddress(access_token: String): String = {
    val callToFacebook = WS.url(FACEBOOK_GET_EMAIL_URL).withHeaders("Accept" -> "application/json").withQueryString(
      "fields" -> "email",
      "return_ssl_resources" -> "",
      "access_token" -> access_token
    ).get().map { response =>
      onOkResponse(response) {
        (response.json \ "email").as[String]
      }
    }

    Await.result(callToFacebook, 60 seconds)
  }
}

object FacebookAuth {
  import play.api.Play.current

  private lazy val FACEBOOK_CLIENT_ID = play.api.Play.configuration.getString("facebook.client.id")
  private lazy val FACEBOOK_CLIENT_SECRET = play.api.Play.configuration.getString("facebook.client.secret")

  def isConfigured: Boolean = FACEBOOK_CLIENT_ID.isDefined && FACEBOOK_CLIENT_SECRET.isDefined

}
