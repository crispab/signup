package se.crisp.signup4.web

import org.scalatest._
import org.scalatestplus.play.ConfiguredApp

@DoNotDiscover class SimpleSpec extends FeatureSpec with MustMatchers with OptionValues with ConfiguredApp with GivenWhenThen {
  info("As a TV set owner")
  info("I want to be able to turn the TV on and off")
  info("So I can watch TV when I want")
  info("And save energy when I'm not watching TV")

  feature("TV power button") {
    scenario("User presses power button when TV is off") {

      Given("a TV set that is switched off")
      assert(1 == 1)

      When("the power button is pressed")
      assert(1 == 1)


      Then("the TV should switch on")
      assert(1 == 1)
    }
  }
}
