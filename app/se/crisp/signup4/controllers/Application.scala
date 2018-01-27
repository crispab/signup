package se.crisp.signup4.controllers

import java.util.{Locale, TimeZone}
import javax.inject.{Inject, Named, Singleton}

import akka.actor.{ActorRef, ActorSystem}
import com.mohiva.play.silhouette.api.exceptions.ProviderException
import com.mohiva.play.silhouette.api.util.Credentials
import com.mohiva.play.silhouette.api.{LoginEvent, LogoutEvent, Silhouette}
import com.mohiva.play.silhouette.impl.exceptions.IdentityNotFoundException
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc._
import play.api.{Configuration, Logger}
import se.crisp.signup4.models.User
import se.crisp.signup4.models.dao.UserDAO
import se.crisp.signup4.services.{CheckEvents, ImageUrl}
import se.crisp.signup4.silhouette.{DefaultEnv, UserService}
import se.crisp.signup4.util.{AuthHelper, LocaleHelper, ThemeHelper}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class Application @Inject()(val silhouette: Silhouette[DefaultEnv],
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
                            implicit val imageUrl: ImageUrl) extends Controller  with I18nSupport {

  initialize()

  def index: Action[AnyContent] = silhouette.UserAwareAction { implicit request =>
    implicit val user: Option[User] = request.identity match {
      case Some(identity) => Some(identity)
      case None => None
    }
    Ok(se.crisp.signup4.views.html.index())
  }

  def loginForm: Action[AnyContent] = silhouette.UserAwareAction.async { implicit request =>
    Future.successful(Ok(se.crisp.signup4.views.html.login(SignInForm.form)))
  }

  def authenticate: Action[AnyContent] = silhouette.UserAwareAction.async { implicit request =>
    SignInForm.form.bindFromRequest.fold(
      form => Future.successful(BadRequest(se.crisp.signup4.views.html.login(form))),
      data => {
        val credentials = Credentials(data.email, data.password)
        credentialsProvider.authenticate(credentials).flatMap { loginInfo =>
          val result = Redirect(routes.Application.index())
          userService.retrieve(loginInfo).flatMap {
            case Some(user) =>
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
            Redirect(routes.Application.loginForm()).flashing("error" -> Messages("invalid.credentials"))
        }
      }
    )
  }

  def logout: Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    val result = Redirect(routes.Application.index())
    silhouette.env.eventBus.publish(LogoutEvent(request.identity, request))
    silhouette.env.authenticatorService.discard(request.authenticator, result)
  }

  private def initialize(): Unit = {
    Logger.debug("initialize called")

    setTimeZoneAndLocaleToAppDefault()

    Logger.info("Application name = " + themeHelper.APPLICATION_NAME)

    startCheckingForRemindersToSend()
  }

  private def setTimeZoneAndLocaleToAppDefault() {
    // not so pretty, but convenient since Heroku servers may run in another time zone and locale
    TimeZone.setDefault(localeHelper.getConfiguredTimeZone)
    Locale.setDefault(localeHelper.getConfiguredLocale)

    Logger.info("TimeZone = " + TimeZone.getDefault)
    Logger.info("Locale = " + Locale.getDefault)
  }

  private def startCheckingForRemindersToSend() {
    import scala.concurrent.duration._
    actorSystem.scheduler.schedule(firstRun, 24.hours, eventReminderActor, CheckEvents(loggedIn = userDAO.system))
  }

  private def firstRun = {
    val sendTimeProperty = configuration.getString("event.reminder.send.time").getOrElse("01:00")

    var startTime = new org.joda.time.LocalTime(sendTimeProperty).toDateTimeToday
    if(startTime.isBeforeNow) {
      startTime = startTime.plusHours(24)
    }
    Logger.debug("First check for reminders to send happens at: " + startTime)

    val untilFirstRun = new org.joda.time.Duration(org.joda.time.DateTime.now(), startTime)
    scala.concurrent.duration.FiniteDuration(untilFirstRun.getStandardSeconds, java.util.concurrent.TimeUnit.SECONDS)
  }

}