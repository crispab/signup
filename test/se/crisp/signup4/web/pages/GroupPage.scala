package se.crisp.signup4.web.pages

import java.util.concurrent.TimeUnit

import org.openqa.selenium.{By, WebDriver}
import se.crisp.signup4.util.Inspect



class GroupPage(baseUrl: String, driver: WebDriver, inspect: Inspect) extends Page(baseUrl, driver) {

  def isViewing(groupName: String): Boolean = groupName == driver.findElement(By.id("page_name")).getText

  def verifyMemberListed(memberName: String): Boolean = try {
    driver.findElement(By.xpath("//a[contains(., '" + memberName + "')]"))
    true
  } catch {
    case ex: NoSuchElementException =>
      false
  }

  def navigateTo(groupName: String) {
    val group = inspect.getGroup(groupName)
    driver.navigate.to(baseUrl + "/groups/" + group.id.get)
    waitForCompletePageLoad()
  }

  def selectMember(memberName: String) {
    driver.findElement(By.xpath("//a[contains(., '" + memberName + "')]")).click()
  }

  def toggleMemberList() {
    driver.findElement(By.id("toggle_icon")).click()
    waitForAnimationToComplete()
  }

  private def waitForAnimationToComplete() {
    waitForSeconds(1)
  }

}
