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

  public void navigateTo() {
    driver.navigate().to(PlayContainer.getBaseUrl());
    Assert.assertTrue("Not on start page", isViewing());
  }

  public String getLoggedInName() {
    return driver.findElement(By.id("logged_in_user")).getText().trim();
  }

  public void selectLogin() {
    driver.findElement(By.id("logged_in_user")).click();
  }

  public void navigateToBlank() {
    driver.navigate().to("about:blank");
  }

  public boolean isViewing() {
    return driver.findElement(By.tagName("h2")).getText().startsWith("Välkommen till");
  }

  public void selectHome() {
    driver.findElement(By.className("navbar-brand")).click();
  }

  public void selectGroups() {
    driver.findElement(By.xpath("//a[@href='/groups']")).click();
  }
}
