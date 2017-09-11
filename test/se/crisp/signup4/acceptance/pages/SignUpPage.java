package se.crisp.signup4.acceptance.pages;

import se.crisp.signup4.acceptance.PlayContainer;
import se.crisp.signup4.acceptance.SharedDriver;
import org.junit.Assert;
import org.openqa.selenium.By;

public class SignUpPage {

  private final SharedDriver driver;

  public SignUpPage(SharedDriver driver) {
    this.driver = driver;
  }

  public SignUpPage navigateTo(long userId, long eventId) {
    driver.navigate().to(PlayContainer.getBaseUrl() + "/participations/edit?eventId=" + eventId + "&userId=" + userId);
    Assert.assertEquals("Anmälan", driver.findElement(By.id("page_name")).getText());
    return this;
  }

  public SignUpPage setStatus(String status) {
    driver.findElement(By.id(status.toLowerCase())).click();
    return this;
  }

  public SignUpPage save() {
    driver.findElement(By.id("action")).submit();
    return this;
  }

  public SignUpPage addComment(String comment) {
    driver.findElement(By.id("comment")).sendKeys(comment);
    return this;
  }
}
