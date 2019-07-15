package se.crisp.signup4.controllers

import java.util.{Locale, TimeZone}
import javax.inject.{Inject, Named, Singleton}

import akka.actor.{ActorRef, ActorSystem}
import com.mohiva.play.silhouette.api.exceptions.ProviderException
import com.mohiva.play.silhouette.api.util.Credentials
import com.mohiva.play.silhouette.api.{LoginEvent, LogoutEvent, Silhouette}
import com.mohiva.play.silhouette.impl.exceptions.IdentityNotFoundException
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc._
import play.api.{Configuration, Logger}
import se.crisp.signup4.models.User
import se.crisp.signup4.models.dao.UserDAO
import se.crisp.signup4.services.CheckEvents
import se.crisp.signup4.silhouette.{DefaultEnv, UserService}
import se.crisp.signup4.util.{LocaleHelper, ThemeHelper}
import scala.concurrent.ExecutionContext
import scala.concurrent.Future


@Singleton
class Application @Inject()(val silhouette: Silhouette[DefaultEnv],
                            val actorSystem: ActorSystem,
                            @Named("event-reminder-actor") val eventReminderActor: ActorRef,
                            val configuration: Configuration,
                            val indexView: se.crisp.signup4.views.html.index,
                            val loginView: se.crisp.signup4.views.html.login,
                            val localeHelper: LocaleHelper,
                            val themeHelper: ThemeHelper,
                            val credentialsProvider: CredentialsProvider,
                            val userService: UserService,
                            val userDAO: UserDAO,
                            implicit val ec: ExecutionContext) extends InjectedController  with I18nSupport {

  initialize()

  def index: Action[AnyContent] = silhouette.UserAwareAction.async { implicit request =>
    implicit val user: Option[User] = request.identity
    Future.successful(Ok(indexView()))
  }

  val loginForm = Form(
    mapping(
      "email" -> email,
      "password" -> nonEmptyText
    )(Application.LoginFields.apply)(Application.LoginFields.unapply)
  )

  def showLoginForm: Action[AnyContent] = silhouette.UserAwareAction.async { implicit request =>
    Future.successful(Ok(loginView(loginForm)))
  }

  def authenticate: Action[AnyContent] = silhouette.UserAwareAction.async { implicit request =>
    loginForm.bindFromRequest.fold(
      form => Future.successful(BadRequest(loginView(form))),
      data => {
        val credentials = Credentials(data.email, data.password)
        credentialsProvider.authenticate(credentials).flatMap { loginInfo =>
          userService.retrieve(loginInfo).flatMap {
            case Some(user) =>
              val onMyWayTo = request.session.get("on_my_way_to").getOrElse(routes.Application.index().url.toString)
              val redirect = Redirect(onMyWayTo).withSession(request.session - "on_my_way_to")
              Logger.debug("Login succeeded. Redirecting to uri " + onMyWayTo)
              silhouette.env.authenticatorService.create(loginInfo).flatMap { authenticator =>
                silhouette.env.eventBus.publish(LoginEvent(user, request))
                silhouette.env.authenticatorService.init(authenticator).flatMap { v =>
                  silhouette.env.authenticatorService.embed(v, redirect)
                }
              }
            case None => Future.failed(new IdentityNotFoundException("Couldn't find user"))
          }
        }.recover {
          case _: ProviderException =>
            Redirect(routes.Application.showLoginForm()).flashing("error" -> Messages("login.failed"))
        }
      }
    )
  }

  def logout: Action[AnyContent] = silhouette.UserAwareAction.async { implicit request =>
    val redirectToIndex = Redirect(routes.Application.index())
    if (request.authenticator.isDefined) {
      silhouette.env.eventBus.publish(LogoutEvent(request.identity.get, request))
      silhouette.env.authenticatorService.discard(request.authenticator.get, redirectToIndex)
    } else {
      Future.successful(redirectToIndex)
    }
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
    val sendTimeProperty = configuration.get[String]("event.reminder.send.time")

    var startTime = new org.joda.time.LocalTime(sendTimeProperty).toDateTimeToday
    if(startTime.isBeforeNow) {
      startTime = startTime.plusHours(24)
    }
    Logger.debug("First check for reminders to send happens at: " + startTime)

    val untilFirstRun = new org.joda.time.Duration(org.joda.time.DateTime.now(), startTime)
    scala.concurrent.duration.FiniteDuration(untilFirstRun.getStandardSeconds, java.util.concurrent.TimeUnit.SECONDS)
  }

}

object Application {
  case class LoginFields(email: String, password: String)
}