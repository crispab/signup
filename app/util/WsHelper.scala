package util

import play.api.Logger
import play.api.libs.ws.WSResponse
import play.mvc.Http.Status

object WsHelper {

  def onOkResponse[T](response: WSResponse)(codeBlock: => T): T = {
    response.status match {
      case Status.OK =>
        Logger.logger.debug("response: " + response.body)
        codeBlock
      case _ =>
        throw new IllegalStateException("Unexpected response (" + response.status + "): " + response.body)
    }
  }

}
