package se.crisp.signup4.http

import javax.inject.{Inject, Singleton}

import play.api.http._
import play.api.mvc.Results.MovedPermanently
import play.api.mvc.{Action, Handler, RequestHeader}
import play.api.routing.Router

@Singleton
class SignupHttpRequestHandler @Inject()(router: Router, errorHandler: HttpErrorHandler,
                                         configuration: HttpConfiguration, filters: HttpFilters
                                        ) extends DefaultHttpRequestHandler(router, errorHandler, configuration, filters) {

  override def routeRequest(request: RequestHeader): Option[Handler] = {
    (request.method, request.headers.get("X-Forwarded-Proto")) match {
      case ("GET", Some(protocol)) if protocol != "https" => Some(Action {
        MovedPermanently("https://" + request.host + request.uri)
      })
      case (_, _) => super.routeRequest(request)
    }
  }

}
