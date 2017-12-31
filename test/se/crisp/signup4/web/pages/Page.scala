package se.crisp.signup4.web.pages
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
}
