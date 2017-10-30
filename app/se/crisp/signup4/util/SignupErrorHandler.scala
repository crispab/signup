package se.crisp.signup4.util

import javax.inject.Inject

import org.apache.commons.lang3.exception.ExceptionUtils
import play.api.http.HttpErrorHandler
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc.{RequestHeader, Result}
import play.api.mvc.Results.{BadRequest, InternalServerError, NotFound}

import scala.concurrent.Future
import scala.concurrent.Future.successful

class SignupErrorHandler @Inject() (val messagesApi: MessagesApi) extends HttpErrorHandler with I18nSupport{
  override def onClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] = {
    val lang = LocaleHelper.getLang(request)
    statusCode match {
      case play.api.http.Status.NOT_FOUND => successful(NotFound(
        se.crisp.signup4.views.html.error(Messages("http.notfound"),""
      )))
      case play.api.http.Status.BAD_REQUEST => successful(BadRequest(
        se.crisp.signup4.views.html.error(Messages("http.badrequest"),""
      )))
    }
  }

  override def onServerError(request: RequestHeader, exception: Throwable): Future[Result] =  {
    val cause = ExceptionUtils.getCause(exception)
    val stackTrace = ExceptionUtils.getStackTrace(cause)
    val lang = LocaleHelper.getLang(request)
    successful(InternalServerError(
      se.crisp.signup4.views.html.error(Messages("http.error"), cause.getLocalizedMessage + "\n" + stackTrace)
    ))
  }

}
