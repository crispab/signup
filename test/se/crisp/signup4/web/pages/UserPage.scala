package se.crisp.signup4.web.pages

import org.openqa.selenium.{By, WebDriver}

class UserPage(baseUrl: String, driver:WebDriver) extends Page(baseUrl, driver) {
  def isViewing(userName: String): Boolean = driver.findElement(By.id("page_name")).getText.startsWith(userName)
}
