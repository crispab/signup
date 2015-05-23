package acceptance.pages;

import acceptance.PlayContainer;
import acceptance.SharedDriver;
import org.junit.Assert;
import org.openqa.selenium.By;

public class StartPage {

  private final SharedDriver driver;

  public StartPage(SharedDriver driver) {
    this.driver = driver;
  }

  public StartPage navigateTo() {
    driver.navigate().to(PlayContainer.getBaseUrl());
    Assert.assertTrue("Not on start page", isViewing());
    return this;
  }

  public String getLoggedInName() {
    return driver.findElement(By.id("logged_in_user")).getText().trim();
  }

  public StartPage selectLogin() {
    driver.findElement(By.id("logged_in_user")).click();
    return this;
  }

  public StartPage navigateToBlank() {
    driver.navigate().to("about:blank");
    return this;
  }

  public boolean isViewing() {
    return driver.findElement(By.tagName("h2")).getText().startsWith("Välkommen till");
  }

  public StartPage selectHome() {
    driver.findElement(By.className("navbar-brand")).click();
    return this;
  }

  public StartPage selectGroups() {
    driver.findElement(By.xpath("//a[@href='/groups']")).click();
    return this;
  }
}
