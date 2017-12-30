package se.crisp.signup4.web.pages

import org.openqa.selenium.{By, WebDriver}

class StartPage(baseUrl: String, driver:WebDriver) extends SignUpPage(baseUrl, driver) {

  def navigateTo() {
    driver.navigate.to(baseUrl)
    waitForCompletePageLoad()
  }

  def getLoggedInName: String = driver.findElement(By.id("logged_in_user")).getText.trim

  def selectLogin() {
    driver.findElement(By.id("logged_in_user")).click()
  }

  def navigateToBlank() {
    driver.navigate.to("about:blank")
  }

  def isViewing: Boolean = driver.findElement(By.tagName("h2")).getText.startsWith("Välkommen till")

  def selectHome() {
    driver.findElement(By.className("navbar-brand")).click()
    waitForCompletePageLoad()
  }

  def selectGroups() {
    driver.findElement(By.xpath("//a[@href='/groups']")).click()
    waitForCompletePageLoad()
  }

}
