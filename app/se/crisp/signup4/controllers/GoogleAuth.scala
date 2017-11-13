package se.crisp.signup4.controllers

import javax.inject.Inject

import com.nimbusds.jose.JWSObject
import jp.t2v.lab.play2.auth.{LoginLogout, OptionalAuthElement}
import org.apache.commons.codec.digest.DigestUtils
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.libs.json.Json
import play.api.libs.ws.WS
import play.api.mvc.{Action, AnyContent, Controller}
import se.crisp.signup4.models.UserDAO
import se.crisp.signup4.util.ThemeHelper
import se.crisp.signup4.util.WsHelper._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.language.postfixOps


class GoogleAuth @Inject()(val messagesApi: MessagesApi,
                           val themeHelper: ThemeHelper,
                           val userDAO: UserDAO) extends Controller with LoginLogout with OptionalAuthElement with AuthConfigImpl with I18nSupport {

  private val GOOGLE_AUTHENTICATION_URL = "https://accounts.google.com/o/oauth2/auth"
  private val GOOGLE_TOKEN_URL = "https://accounts.google.com/o/oauth2/token"

  import play.api.Play.current


  def authenticate = Action { implicit request =>
    val randomString = DigestUtils.md5Hex(Math.random().toString)

    val callbackUrl = routes.GoogleAuth.callback().absoluteURL()
    import com.netaporter.uri.dsl._
    val requestAuthenticationTokenUrl = GOOGLE_AUTHENTICATION_URL.addParams(
      "response_type" -> "code" ::
        "client_id" -> GoogleAuth.GOOGLE_CLIENT_ID.get ::
        "redirect_uri" -> callbackUrl ::
        "state" -> randomString ::
        "prompt" -> "select_account" ::
        "scope" -> "openid email" :: Nil
    )

    Redirect(requestAuthenticationTokenUrl)
  }

  def callback(error: Option[String] = None, state: Option[String] = None, code: Option[String] = None): Action[AnyContent] = Action.async { implicit request =>
    if (code.isDefined) {
      val callbackUrl = routes.GoogleAuth.callback().absoluteURL()
      val callToGoogle = WS.url(GOOGLE_TOKEN_URL).withHeaders("Accept" -> "application/json").post(Map(
        "code" -> Seq(code.get),
        "client_id" -> Seq(GoogleAuth.GOOGLE_CLIENT_ID.get),
        "client_secret" -> Seq(GoogleAuth.GOOGLE_CLIENT_SECRET.get),
        "redirect_uri" -> Seq(callbackUrl),
        "grant_type" -> Seq("authorization_code")
      )).map { response =>
        onOkResponse(response) {
          val idTokenJwt = (response.json \ "id_token").as[String]
          val idToken = Json.parse(JWSObject.parse(idTokenJwt).getPayload.toString)
          val email = (idToken \ "email").as[String]
          email
        }
      }

      val email = Await.result(callToGoogle, 60 seconds)
      val user = userDAO.findByEmail(email)
      if (user.isDefined) {
        gotoLoginSucceeded(user.get.id.get)
      } else {
        val errorMessage = Messages("login.nonexistentemail", email, themeHelper.APPLICATION_NAME)
        Future.successful(
          Redirect(routes.Application.loginForm()).flashing(("error", errorMessage))
        )
      }
    } else {
      val cause = error.getOrElse(Messages("login.unknownerror"))
      val errorMessage = Messages("login.googlefail", cause)
      Future.successful(
        Redirect(routes.Application.loginForm()).flashing(("error", errorMessage))
      )
    }
  }

}

object GoogleAuth {
  import play.api.Play.current
  private lazy val GOOGLE_CLIENT_ID = play.api.Play.configuration.getString("google.client.id")
  private lazy val GOOGLE_CLIENT_SECRET = play.api.Play.configuration.getString("google.client.secret")

  def isConfigured: Boolean = GOOGLE_CLIENT_ID.isDefined && GOOGLE_CLIENT_SECRET.isDefined

}
