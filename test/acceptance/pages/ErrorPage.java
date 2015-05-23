package acceptance.pages;

import acceptance.PlayContainer;
import acceptance.SharedDriver;
import org.openqa.selenium.By;

public class ErrorPage {
  private final SharedDriver driver;

  public ErrorPage(SharedDriver driver) {
    this.driver = driver;
  }

  public boolean isViewing() {
    return driver.findElement(By.tagName("h2")).getText().trim().toLowerCase().startsWith("oj!");
  }

  public ErrorPage navigateTo() {
    driver.navigate().to(PlayContainer.getBaseUrl() + "/ERRORR");
    return this;
  }
}
