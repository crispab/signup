package se.crisp.signup4.controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.exceptions.ProviderException
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.impl.exceptions.IdentityNotFoundException
import com.mohiva.play.silhouette.impl.providers._
import play.api.Logger
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.libs.concurrent.Execution.Implicits._
import play.api.mvc.{Action, AnyContent, Controller}
import se.crisp.signup4.models.dao.UserDAO
import se.crisp.signup4.silhouette.{DefaultEnv, UserService}

import scala.concurrent.Future

class SocialLogin @Inject()(val messagesApi: MessagesApi,
                            val silhouette: Silhouette[DefaultEnv],
                            userService: UserService,
                            val authInfoRepository: AuthInfoRepository,
                            val socialProviderRegistry: SocialProviderRegistry,
                            val userDAO: UserDAO
                                     )
  extends Controller with I18nSupport {

  def authenticate(provider: String): Action[AnyContent] = Action.async { implicit request =>
    (socialProviderRegistry.get[SocialProvider](provider) match {
      case Some(p: SocialProvider with CommonSocialProfileBuilder) =>
        p.authenticate().flatMap {
          case Left(result) => Future.successful(result)
          case Right(authInfo) => for {
            profile <- p.retrieveProfile(authInfo)
            user <- userService.save(profile)
            _ <- authInfoRepository.save(profile.loginInfo, authInfo)
            authenticator <- silhouette.env.authenticatorService.create(profile.loginInfo)
            value <- silhouette.env.authenticatorService.init(authenticator)

            // TODO: go to desired page (check request.session)
            result <- silhouette.env.authenticatorService.embed(value, Redirect(routes.Application.index()))
          } yield {
            silhouette.env.eventBus.publish(LoginEvent(user, request))
            result
          }
        }
      case _ => Future.failed(new ProviderException(s"Cannot authenticate with unexpected social provider $provider"))
    }).recover {
      case e: IdentityNotFoundException =>
        Redirect(routes.Application.showLoginForm()).flashing("error" -> Messages("login.nonexistentemail", e.getLocalizedMessage))
      case e: ProviderException =>
        Logger.error("Unexpected provider error", e)
        Redirect(routes.Application.showLoginForm()).flashing("error" -> Messages("login.socialfail", provider, e.getLocalizedMessage))
    }
  }
}
