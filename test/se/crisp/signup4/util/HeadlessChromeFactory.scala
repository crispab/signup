package se.crisp.signup4.util

import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.{ChromeDriver, ChromeOptions}
import org.scalatestplus.play.BrowserFactory.UnavailableDriver
import org.scalatestplus.play.{ChromeFactory, Resources}

trait HeadlessChromeFactory extends ChromeFactory {
  override def createWebDriver(): WebDriver =
    try {
      val opts = new ChromeOptions
      opts.addArguments("--headless")
      new ChromeDriver(opts)
    }
    catch {
      case ex: Throwable => UnavailableDriver(Some(ex), "Can't create Headlessd ChromeDriver:" + ex.getMessage)
    }
}
