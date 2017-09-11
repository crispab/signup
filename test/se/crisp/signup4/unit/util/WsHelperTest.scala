package se.crisp.signup4.unit.util

import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.mvc.Http.Status
import se.crisp.signup4.util.WsHelper
import play.api.libs.ws.WSResponse


class WsHelperTest extends PlaySpec with MockitoSugar {

  "WsHelper.onOkResponse" must {

    "throw exception on invalid response" in {
      val response = mock[WSResponse]
      when(response.status) thenReturn Status.INTERNAL_SERVER_ERROR

      an [IllegalStateException] must be thrownBy {
        WsHelper.onOkResponse(response) { throw new RuntimeException("We should never get here :-(") }
      }
    }

    "execute code block on OK (200) response" in {
      val response = mock[WSResponse]
      when(response.status) thenReturn Status.OK
      when(response.body) thenReturn "this is my body"

      WsHelper.onOkResponse(response) { response.body } must equal ("this is my body")
    }
  }
}
