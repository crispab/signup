package se.crisp.signup4.acceptance.pages
import java.util.concurrent.TimeUnit

import org.openqa.selenium.{By, JavascriptExecutor, WebDriver}
import org.openqa.selenium.support.ui.{ExpectedCondition, WebDriverWait}

abstract class Page(baseUrl: String, driver: WebDriver) {

  protected def waitForCompletePageLoad() {
    val pageLoadCondition = new ExpectedCondition[Boolean]() {
      override def apply(driver: WebDriver): Boolean = driver.asInstanceOf[JavascriptExecutor].executeScript("return document.readyState") == "complete"
    }
    val wait = new WebDriverWait(driver, 30)
    wait.until(pageLoadCondition)
  }

  protected def waitForSeconds(seconds: Int) {
    driver.synchronized {
      try {
        driver.wait(seconds * 1000)
      } catch {
        case _ : InterruptedException => // ignore
      }
    }
  }

}
