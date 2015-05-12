package acceptance.pages;

import acceptance.PlayContainer;
import acceptance.SharedDriver;
import models.Group;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import util.Inspect;

public class UserPage {
  private final SharedDriver driver;

  public UserPage(SharedDriver driver) {
    this.driver = driver;
  }

  public boolean isViewing(String userName) {
    return driver.findElement(By.id("page_name")).getText().startsWith(userName);
  }
}
