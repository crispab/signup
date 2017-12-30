package se.crisp.signup4.web

import play.api.test._
import org.scalatest._
import org.scalatestplus.play._
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.{Application, Play}
import play.api.inject.guice._
import play.api.routing._

class MasterSuite extends Suites(new SimpleSpec) with TestSuite with GuiceOneServerPerSuite with OneBrowserPerSuite with ChromeFactory
