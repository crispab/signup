package se.crisp.signup4.silhouette

import javax.inject.{Inject, Provider, Singleton}

import com.mohiva.play.silhouette.api.actions.{SecuredErrorHandler, UnsecuredErrorHandler}
import org.apache.commons.lang3.exception.ExceptionUtils
import play.api._
import play.api.http.DefaultHttpErrorHandler
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc.{RequestHeader, Result, Results}
import play.api.mvc.Results._
import play.api.routing.Router
import se.crisp.signup4.services.ImageUrl
import se.crisp.signup4.util.{AuthHelper, LocaleHelper, ThemeHelper}

import scala.concurrent.Future

@Singleton
class ErrorHandler @Inject()(val env: Environment,
                             val config: Configuration,
                             val sourceMapper: OptionalSourceMapper,
                             val router: Provider[Router],
                             val messagesApi: MessagesApi,
                             val localeHelper: LocaleHelper,
                             implicit val imageUrl: ImageUrl,
                             implicit val themeHelper: ThemeHelper,
                             implicit val authHelper: AuthHelper)
  extends DefaultHttpErrorHandler(env, config, sourceMapper, router)
    with SecuredErrorHandler
    with UnsecuredErrorHandler
    with I18nSupport {

  override def onNotAuthorized(implicit request: RequestHeader): Future[Result] = {
    val lang = localeHelper.getLang(request)
    Logger.info("Not authorized to access: " + request.uri)
    Future.successful(Redirect(se.crisp.signup4.controllers.routes.Application.showLoginForm()).flashing("error" -> Messages("error.application.authfail")).withSession("on_my_way_to" -> request.uri))
  }

  override def onNotAuthenticated(implicit request: RequestHeader): Future[Result] = {
    onNotAuthorized(request)
  }

  override protected def onForbidden(request: RequestHeader, message: String): Future[Result] = {
    onNotAuthorized(request)
  }

  override protected def onBadRequest(request: RequestHeader, message: String): Future[Result] = {
    val lang = localeHelper.getLang(request)
    implicit val mess: Messages = request2Messages(request)
    Future.successful(BadRequest(se.crisp.signup4.views.html.error(Messages("http.badrequest"), message)))
  }

  override protected def onNotFound(request: RequestHeader, message: String): Future[Result] = {
    val lang = localeHelper.getLang(request)
    implicit val mess: Messages = request2Messages(request)
    Future.successful(NotFound(se.crisp.signup4.views.html.error(Messages("http.notfound"), message)))
  }

  override def onServerError(request: RequestHeader, exception: Throwable): Future[Result] = {
    val cause = Option(exception.getCause).getOrElse(exception)
    val stackTrace = ExceptionUtils.getStackTrace(cause)
    val lang = localeHelper.getLang(request)
    implicit val mess: Messages = request2Messages(request)
    Future.successful(InternalServerError(se.crisp.signup4.views.html.error(Messages("http.error"), cause.getLocalizedMessage + "\n" + stackTrace)))
  }

  override protected def onProdServerError(request: RequestHeader, exception: UsefulException): Future[Result] = {
    val stackTrace = ExceptionUtils.getStackTrace(exception.getCause)
    implicit val mess: Messages = request2Messages(request)
    Future.successful(InternalServerError(se.crisp.signup4.views.html.error(Messages("http.error"),
      exception.title + "\n" + exception.description +  "\n" + exception.getLocalizedMessage + "\n" + stackTrace)))
  }

  override protected def onDevServerError(request: RequestHeader, exception: UsefulException): Future[Result] = {
    onProdServerError(request, exception)
  }

  override protected def onOtherClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] = {
    implicit val mess: Messages = request2Messages(request)
    Future.successful(Results.Status(statusCode)(se.crisp.signup4.views.html.error(Messages("http.badrequest"), message)))
  }

}
