package se.crisp.signup4.web.pages

import org.openqa.selenium.{By, WebDriver}

class LoginPage(baseUrl: String, driver: WebDriver) extends Page(baseUrl, driver) {

  def navigateTo() {
    driver.navigate.to(baseUrl + "/login")
    waitForCompletePageLoad()
  }

  def isViewing: Boolean = "Logga in" == driver.findElement(By.id("page_name")).getText

  def loginUsingPw(email: String, password: String) {
    driver.findElement(By.id("email")).sendKeys(email)
    driver.findElement(By.id("password")).sendKeys(password)
    driver.findElement(By.tagName("form")).submit()
  }

  def ensureLoggedOut() {
    driver.manage.deleteAllCookies()
  }

  def isLoggedIn: Boolean = {
    val cookie = driver.manage.getCookieNamed("PLAY2AUTH_SESS_ID")
    cookie != null
  }

}
