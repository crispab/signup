package acceptance.pages;

import acceptance.PlayContainer;
import acceptance.SharedDriver;
import org.junit.Assert;
import org.openqa.selenium.By;

public class SignUpPage {

  private SharedDriver driver;

  public SignUpPage(SharedDriver driver) {
    this.driver = driver;
  }

  public void navigateTo(long userId, long eventId) {
    driver.navigate().to(PlayContainer.getBaseUrl() + "/participations/edit?eventId=" + eventId + "&userId=" + userId);
    Assert.assertEquals("Anmälan", driver.findElement(By.id("page_name")).getText());
  }

  public void setStatus(String status) {
    driver.findElement(By.id(status.toLowerCase())).click();
  }

  public void save() {
    driver.findElement(By.id("action")).submit();
  }

  public void addComment(String comment) {
    driver.findElement(By.id("comment")).sendKeys(comment);
  }
}
