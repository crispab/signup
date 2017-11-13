package se.crisp.signup4.http

import javax.inject.{Inject, Singleton}

import org.apache.commons.lang3.exception.ExceptionUtils
import play.api.http.HttpErrorHandler
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc.Results.{BadRequest, InternalServerError, NotFound}
import play.api.mvc.{RequestHeader, Result}
import se.crisp.signup4.services.ImageUrl
import se.crisp.signup4.util._

import scala.concurrent.Future
import scala.concurrent.Future.successful

@Singleton
class SignupErrorHandler @Inject() (val messagesApi: MessagesApi,
                                    implicit val authHelper: AuthHelper,
                                    implicit val localeHelper: LocaleHelper,
                                    implicit val themeHelper: ThemeHelper,
                                    implicit val formHelper: FormHelper,
                                    implicit val htmlHelper: HtmlHelper,
                                    implicit val imageUrl: ImageUrl) extends HttpErrorHandler with I18nSupport {

  override def onClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] = {
    val lang = localeHelper.getLang(request)
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
    val cause = Option(exception.getCause).getOrElse(exception)
    val stackTrace = ExceptionUtils.getStackTrace(cause)
    val lang = localeHelper.getLang(request)
    successful(InternalServerError(
      se.crisp.signup4.views.html.error(Messages("http.error"), cause.getLocalizedMessage + "\n" + stackTrace)
    ))
  }

}
