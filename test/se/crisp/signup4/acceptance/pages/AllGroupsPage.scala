package se.crisp.signup4.acceptance.pages

import org.openqa.selenium.{By, WebDriver}

class AllGroupsPage(baseUrl: String, driver: WebDriver) extends Page(baseUrl, driver) {

  def isViewing: Boolean = "Grupper" == driver.findElement(By.id("page_name")).getText

  def verifyGroupListed(groupName: String): Boolean = try {
    driver.findElement(By.xpath("//a[contains(., '" + groupName + "')]"))
    true
  } catch {
    case _: NoSuchElementException =>
      false
  }

  def navigateTo() {
    driver.navigate.to(baseUrl + "/groups")
    waitForCompletePageLoad()
  }

  def selectGroup(groupName: String) {
    driver.findElement(By.xpath("//a[contains(., '" + groupName + "')]")).click()
    waitForCompletePageLoad()
  }

}
