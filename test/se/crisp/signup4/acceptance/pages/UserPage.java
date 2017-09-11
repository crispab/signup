package se.crisp.signup4.acceptance.pages;

import se.crisp.signup4.acceptance.SharedDriver;
import org.openqa.selenium.By;

public class UserPage {
  private final SharedDriver driver;

  public UserPage(SharedDriver driver) {
    this.driver = driver;
  }

  public boolean isViewing(String userName) {
    return driver.findElement(By.id("page_name")).getText().startsWith(userName);
  }
}
