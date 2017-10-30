package se.crisp.signup4.http

import javax.inject.{Inject, Singleton}

import play.api.http._
import play.api.mvc.{Handler, RequestHeader}
import play.api.routing.Router
import se.crisp.signup4.controllers.Application

@Singleton
class SignupHttpRequestHandler @Inject()(router: Router,
                                         errorHandler: HttpErrorHandler,
                                         configuration: HttpConfiguration,
                                         filters: HttpFilters,
                                         application: Application)
  extends DefaultHttpRequestHandler(router, errorHandler, configuration, filters) {

  override def routeRequest(request: RequestHeader): Option[Handler] = {
    super.routeRequest(request)
  }

  private def ensureHttpsOnHeroku(request: RequestHeader) = {
    request.headers.get("x-forwarded-proto") match {
      case Some(protocol) =>
        if (!"https".equals(protocol)) {
          Some(application.redirectToHttps)
        } else {
          super.routeRequest(request)
        }
      case None => super.routeRequest(request)
    }
  }

}
