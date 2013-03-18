package controllers

import jp.t2v.lab.play2.auth.Auth
import play.api.mvc._
import play.api.mvc.Results._


trait Https {
  self: Auth =>

  /*
   * Code credit to: computerpunc, https://groups.google.com/d/msg/play-framework/11zbMtNI3A8/9nyrFM7Q0TEJ
   * Heroku sets x-forwarded-proto when https is used
   */
  def httpsAction(f: Request[AnyContent] => Result): Action[AnyContent] = Action {
    request =>
      request.headers.get("x-forwarded-proto") match {
        case Some(protocol) =>
          if ("https" == protocol) {
            f(request) match {
              case plainResult: PlainResult => plainResult.withHeaders(("Strict-Transport-Security", "max-age=31536000")) // or "max-age=31536000; includeSubDomains"
              case result: Result => result
            }
          } else {
            MovedPermanently("https://" + request.host + request.uri)
          }
        case None => f(request)
      }
  }
}
