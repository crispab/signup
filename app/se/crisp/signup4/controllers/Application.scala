package se.crisp.signup4.controllers

import java.util.{Locale, TimeZone}
import javax.inject.{Inject, Named, Singleton}

import akka.actor.ActorRef
import jp.t2v.lab.play2.auth.{LoginLogout, OptionalAuthElement}
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.libs.concurrent.Akka
import play.api.mvc._
import se.crisp.signup4.models
import se.crisp.signup4.models.User
import se.crisp.signup4.services.CheckEvents
import se.crisp.signup4.util.{AuthHelper, ThemeHelper}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class Application @Inject() (val messagesApi: MessagesApi, @Named("event-reminder-actor") eventReminderActor: ActorRef) extends Controller with LoginLogout with OptionalAuthElement with AuthConfigImpl  with I18nSupport{

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
    val user = User.findByEmail(email.trim)
    AuthHelper.checkPassword(user, password)
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

    Logger.info("Application name = " + ThemeHelper.APPLICATION_NAME)

    startCheckingForRemindersToSend()
  }

  private def setTimeZoneAndLocaleToAppDefault() {
    // not so pretty, but convenient since Heroku servers may run in another time zone and locale
    TimeZone.setDefault(se.crisp.signup4.util.LocaleHelper.getConfiguredTimeZone)
    Locale.setDefault(se.crisp.signup4.util.LocaleHelper.getConfiguredLocale)

    Logger.info("TimeZone = " + TimeZone.getDefault)
    Logger.info("Locale = " + Locale.getDefault)
  }

  private def startCheckingForRemindersToSend() {
    import play.api.Play.current

    import scala.concurrent.ExecutionContext.Implicits.global
    import scala.concurrent.duration._
    Akka.system.scheduler.schedule(firstRun, 24.hours, eventReminderActor, CheckEvents(loggedIn = User.system))
  }

  private def firstRun = {
    import play.api.Play.current
    val sendTimeProperty = play.api.Play.configuration.getString("event.reminder.send.time").getOrElse("01:00")

    var startTime = new org.joda.time.LocalTime(sendTimeProperty).toDateTimeToday
    if(startTime.isBeforeNow) {
      startTime = startTime.plusHours(24)
    }
    Logger.debug("First check for reminders to send happens at: " + startTime)

    val untilFirstRun = new org.joda.time.Duration(org.joda.time.DateTime.now(), startTime)
    scala.concurrent.duration.FiniteDuration(untilFirstRun.getStandardSeconds, java.util.concurrent.TimeUnit.SECONDS)
  }

}