package se.crisp.signup4.controllers

import javax.inject.{Inject, Named, Singleton}

import akka.actor.{ActorRef, ActorSystem}
import com.mohiva.play.silhouette.api.exceptions.ProviderException
import com.mohiva.play.silhouette.api.util.Credentials
import com.mohiva.play.silhouette.api.{LoginEvent, Silhouette}
import com.mohiva.play.silhouette.impl.exceptions.IdentityNotFoundException
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import play.api.Configuration
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc._
import se.crisp.signup4.models.dao.UserDAO
import se.crisp.signup4.services.ImageUrl
import se.crisp.signup4.silhouette.{DefaultEnv, SignInForm, UserService}
import se.crisp.signup4.util.{AuthHelper, LocaleHelper, ThemeHelper}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class SilhouetteApplication @Inject()(silhouette: Silhouette[DefaultEnv],
                                      val messagesApi: MessagesApi,
                                      val actorSystem: ActorSystem,
                                      @Named("event-reminder-actor") eventReminderActor: ActorRef,
                                      val configuration: Configuration,
                                      implicit val authHelper: AuthHelper,
                                      implicit val localeHelper: LocaleHelper,
                                      implicit val themeHelper: ThemeHelper,
                                      val credentialsProvider: CredentialsProvider,
                                      val userService: UserService,
                                      val userDAO: UserDAO,
                                      implicit val imageUrl: ImageUrl) extends Controller with AuthConfigImpl with I18nSupport {


  def index: Action[AnyContent] = silhouette.UserAwareAction { implicit request =>
    val user = request.identity match {
      case Some(identity) => Some(identity.name)
      case None => None
    }
    Ok(se.crisp.signup4.views.html.indexSilhouette(user))
  }

  def view: Action[AnyContent] = silhouette.UserAwareAction.async { implicit request =>
    Future.successful(Ok(se.crisp.signup4.views.html.loginSilhouette(SignInForm.form)))
  }


  def submit: Action[AnyContent] = silhouette.UserAwareAction.async { implicit request =>
    SignInForm.form.bindFromRequest.fold(
      form => Future.successful(BadRequest(se.crisp.signup4.views.html.loginSilhouette(form))),
      data => {
        val credentials = Credentials(data.email, data.password)
        credentialsProvider.authenticate(credentials).flatMap { loginInfo =>
          val result = Redirect(routes.SilhouetteApplication.index())
          userService.retrieve(loginInfo).flatMap {
            case Some(user) =>
              val c = configuration.underlying
              silhouette.env.authenticatorService.create(loginInfo).flatMap { authenticator =>
                silhouette.env.eventBus.publish(LoginEvent(user, request))
                silhouette.env.authenticatorService.init(authenticator).flatMap { v =>
                  silhouette.env.authenticatorService.embed(v, result)
                }
              }
            case None => Future.failed(new IdentityNotFoundException("Couldn't find user"))
          }
        }.recover {
          case e: ProviderException =>
            Redirect(routes.SilhouetteApplication.view()).flashing("error" -> Messages("invalid.credentials"))
        }
      }
    )
  }

}