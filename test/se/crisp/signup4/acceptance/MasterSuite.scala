package se.crisp.signup4.acceptance

import org.scalatest._
import org.scalatestplus.play._
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import se.crisp.signup4.acceptance.selenium.HeadlessChromeFactory

class MasterSuite extends Suites(
  new SimpleNavigationFeature,
  new LoginFeature,
  new SignUpForAnEventFeature,
  new CreateEventFeature)
  with TestSuite
  with GuiceOneServerPerSuite
  with OneBrowserPerSuite
  with HeadlessChromeFactory
