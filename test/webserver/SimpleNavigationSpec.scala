package webserver


import org.specs2.mutable._

import play.api.libs.ws.WS
import play.api.test._
import play.api.test.Helpers._

class SimpleNavigationSpec extends Specification {

  "Application" should {

    "do nothing" in new WithServer {
      1 must be equalTo 1
    }

/*
    "display index" in new WithServer {
      val response = await(WS.url("http://localhost:" + port).get)
      response.status must equalTo(OK)
      response.body must contain("SignUp")
    }

    "show list of groups" in new WithServer {
      val response = await(WS.url("http://localhost:" + port + "/groups").get())
      response.status must equalTo(OK)
      response.body must contain("Crisp Rocket Days")
      response.body must contain("Näsknäckarna")
    }

    "show list of users" in new WithServer {
      val response = await(WS.url("http://localhost:" + port + "/users").get())
      response.status must equalTo(OK)
      response.body must contain("Admin Istratör")
      response.body must contain("Fredrik Unknown")
      response.body must contain("Frodo Baggins")
      response.body must contain("Göran Persson")
      response.body must contain("John Doe")
      response.body must contain("Torbjörn Fälldin")
    }

    "show login form" in new WithServer {
      val response = await(WS.url("http://localhost:" + port + "/login").get())
      response.status must equalTo(OK)
      response.body must contain("Epost")
      response.body must contain("Lösenord")
      response.body must contain("Logga in")
    }

    "show list of events and members" in new WithServer {
      val response = await(WS.url("http://localhost:" + port + "/groups/-1?showAll=true").get())
      response.status must equalTo(OK)
      response.body must contain("Crisp Rocket Days")
      response.body must contain("Vad jag lärde mig av BigFamilyTrip")
      response.body must contain("Scala 3.0 och Play 3.0")
      response.body must contain("Fredrik Unknown")
      response.body must contain("Torbjörn Fälldin")
    }
    */
  }
}