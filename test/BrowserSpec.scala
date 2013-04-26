
import org.specs2.mutable._

import play.api.libs.ws.WS
import play.api.test._
import play.api.test.Helpers._

class BrowserSpec extends Specification {
  "Application" should {

    "run in a server" in new WithServer {

      val response = await(WS.url("http://localhost:" + port).get)
      response.status must equalTo(OK)
      response.body must contain("SignUp")

      await(WS.url("http://localhost:" + port + "/users/-1").get).body must contain("Fredrik")
    }
  }
}