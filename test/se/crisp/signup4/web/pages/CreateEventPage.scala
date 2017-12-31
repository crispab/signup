package se.crisp.signup4.web.pages

import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import org.openqa.selenium.{By, Keys, WebDriver}

class CreateEventPage(baseUrl: String, driver: WebDriver) extends Page(baseUrl, driver) {

  def navigateTo(groupId: Long) {
    driver.navigate.to(baseUrl + "/events/new?groupId=" + groupId)
    waitForCompletePageLoad()
  }

  def isViewing: Boolean = "Ny" == driver.findElement(By.id("page_name")).getText

  def submitForm(eventName: String, description: String, venue: String, eventDateTime: DateTime) {
    driver.findElement(By.id("name")).sendKeys(eventName)
    driver.findElement(By.id("venue")).sendKeys(venue)

    import org.openqa.selenium.JavascriptExecutor
    val jse = driver.asInstanceOf[JavascriptExecutor]
    jse.executeScript("document.getElementById('start_date').value='" + date(eventDateTime) + "';")
    jse.executeScript("document.getElementById('start_time').value='" + time(eventDateTime) + "';")
    jse.executeScript("document.getElementById('end_time').value='" + time(eventDateTime.plusHours(3)) + "';")

    driver.findElement(By.id("action")).submit()
    waitForCompletePageLoad()
  }

  private def enterDescription(description: String) {
    driver.findElement(By.id("editor")).sendKeys(description)
  }

  private def date(eventDateTime: DateTime) = ISODateTimeFormat.date.print(eventDateTime.getMillis)

  private def time(eventDateTime: DateTime) = ISODateTimeFormat.hourMinute.print(eventDateTime.getMillis)
}
