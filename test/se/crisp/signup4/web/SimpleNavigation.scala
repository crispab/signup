package se.crisp.signup4.web

import org.scalatest._
import org.scalatestplus.play.{ConfiguredBrowser, ConfiguredServer}

@DoNotDiscover class SimpleNavigation extends FeatureSpec with Matchers with OptionValues with ConfiguredServer with ConfiguredBrowser with GivenWhenThen {
  feature("Simple navigation") {
    scenario("The user requests the start page") {

      Given("the user has a blank web browser")
      go to s"habout:blank"

      When("pointing the browser to the start page")
      go to s"http://localhost:19001/"

      Then("the start page should display")
      pageTitle should be ("SignUp")
    }
  }
}
