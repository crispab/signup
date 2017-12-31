package se.crisp.signup4.web.pages

import org.openqa.selenium.{By, WebDriver}

class ErrorPage(baseUrl: String, driver: WebDriver) extends Page(baseUrl, driver) {

  def isViewing: Boolean = driver.findElement(By.tagName("h2")).getText.trim.toLowerCase.startsWith("oj!")

  def navigateTo() {
    driver.navigate.to(baseUrl + "/DOESNOTEXIST")
  }
}
