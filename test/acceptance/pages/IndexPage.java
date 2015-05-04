package acceptance.pages;

import org.junit.Assert;
import org.openqa.selenium.By;

public class IndexPage {

  private SharedDriver driver;
  private String baseUrl;

  public IndexPage(SharedDriver driver) {
    this.driver = driver;
  }

  public void setBaseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  public void navigateTo() {
    driver.navigate().to(baseUrl);
    Assert.assertTrue("Not on start page", driver.findElement(By.tagName("h2")).getText().startsWith("Välkommen till"));
  }

  public String getLoggedInName() {
    return driver.findElement(By.id("logged_in_user")).getText().trim();
  }

  public void selectLogin() {
    driver.findElement(By.id("logged_in_user")).click();
  }
}
