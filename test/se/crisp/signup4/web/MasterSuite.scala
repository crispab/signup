package se.crisp.signup4.web

import org.scalatest._
import org.scalatestplus.play._
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import se.crisp.signup4.util.HeadlessChromeFactory

class MasterSuite extends Suites(new LoginFeature, new SimpleNavigationFeature)
  with TestSuite
  with GuiceOneServerPerSuite
  with OneBrowserPerSuite
  with HeadlessChromeFactory
