package se.crisp.signup4.acceptance.pages

import org.openqa.selenium.{By, WebDriver}

class SignUpPage(baseUrl: String, driver: WebDriver) extends Page(baseUrl, driver) {

  def navigateTo(userId: Long, eventId: Long) {
    driver.navigate.to(baseUrl + "/participations/edit?eventId=" + eventId + "&userId=" + userId)
    waitForCompletePageLoad()
  }

  def setStatus(status: String) {
    driver.findElement(By.id(status.toLowerCase)).click()
  }

  def save() {
    driver.findElement(By.id("action")).submit()
    waitForCompletePageLoad()
  }

  def addComment(comment: String) {
    driver.findElement(By.id("comment")).sendKeys(comment)
  }
}
