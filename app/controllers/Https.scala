package controllers

import jp.t2v.lab.play2.auth.Auth
import play.api.mvc._


trait Https {
  self: Auth =>

  def httpsAction(f: Request[AnyContent] => Result): Action[AnyContent] = Action { request =>
    request.headers.get("x-forwarded-proto") match {
      case Some(header)=> if ("https"==header) {
        f(request) match {
          case res:PlainResult=> res.withHeaders(("Strict-Transport-Security", "max-age=31536000")) // or "max-age=31536000; includeSubDomains"
          case res:Result=> res
        }
      } else Results.MovedPermanently("https://"+request.host+request.uri)
      case None=> f(request)
    }
  }
}
