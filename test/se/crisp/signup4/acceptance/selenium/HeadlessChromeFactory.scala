package se.crisp.signup4.acceptance.selenium

import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.{ChromeDriver, ChromeOptions}
import org.scalatestplus.play.BrowserFactory.UnavailableDriver
import org.scalatestplus.play.ChromeFactory

trait HeadlessChromeFactory extends ChromeFactory {
  override def createWebDriver(): WebDriver =
    try {
      // System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver")
      val opts = new ChromeOptions
      // opts.setBinary("/path/to/google-chrome-stable")
      opts.addArguments("--headless")
      opts.addArguments("--disable-gpu")
      new ChromeDriver(opts)
    }
    catch {
      case ex: Throwable => UnavailableDriver(Some(ex), "Can't create Headlessd ChromeDriver:" + ex.getMessage)
    }
}
