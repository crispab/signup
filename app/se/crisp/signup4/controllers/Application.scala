package se.crisp.signup4.controllers

import java.util.{Locale, TimeZone}
import javax.inject.{Inject, Named, Singleton}

import akka.actor.{ActorRef, ActorSystem}
import jp.t2v.lab.play2.auth.{LoginLogout, OptionalAuthElement}
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc._
import play.api.{Configuration, Logger}
import se.crisp.signup4.models
import se.crisp.signup4.models.dao.UserDAO
import se.crisp.signup4.services.{CheckEvents, ImageUrl}
import se.crisp.signup4.util.{AuthHelper, LocaleHelper, ThemeHelper}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class Application @Inject() (val messagesApi: MessagesApi,
                             val actorSystem: ActorSystem,
                             @Named("event-reminder-actor") eventReminderActor: ActorRef,
                             val configuration: Configuration,
                             implicit val authHelper: AuthHelper,
                             implicit val localeHelper: LocaleHelper,
                             implicit val themeHelper: ThemeHelper,
                             val userDAO: UserDAO,
                             implicit val imageUrl: ImageUrl) extends Controller with LoginLogout with OptionalAuthElement with AuthConfigImpl  with I18nSupport{

  initialize()

  val loginDataForm: Form[Option[User]] = Form(
    mapping(
      "email" -> nonEmptyText,
      "password" -> nonEmptyText
    )(toUser)(fromUser).verifying(user => user.isDefined)
  )

  def index: Action[AnyContent] = StackAction { implicit request =>
    Ok(se.crisp.signup4.views.html.index())
  }

  def loginForm: Action[AnyContent] = Action { implicit request =>
    if (request.session.get("access_uri").isEmpty && request.headers.get(REFERER).isDefined) {
      Logger.debug("Using REFERER URL: " + request.headers.get(REFERER).get)
      Ok(se.crisp.signup4.views.html.login(loginDataForm)).withSession("access_uri" -> request.headers.get(REFERER).get)
    } else {
      Ok(se.crisp.signup4.views.html.login(loginDataForm))
    }
  }

  def authenticate: Action[AnyContent] = Action.async { implicit request =>
    loginDataForm.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(se.crisp.signup4.views.html.login(formWithErrors))),
      user => gotoLoginSucceeded(user.get.id.get)
    )
  }

  def toUser(email: String, password: String): Option[models.User] = {
    val user = userDAO.findByEmail(email.trim)
    authHelper.checkPassword(user, password)
  }

  def fromUser(user: Option[models.User]): Option[(String, String)] = {
    if(user.isDefined) {
      Option((user.get.email, ""))
    } else {
      Option.empty
    }
  }

  def logout: Action[AnyContent] = Action.async { implicit request =>
    gotoLogoutSucceeded.map(_.flashing(
      "success" -> Messages("application.logout")
    ))
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