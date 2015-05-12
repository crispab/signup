package acceptance.pages;

import acceptance.PlayContainer;
import acceptance.SharedDriver;
import org.junit.Assert;
import org.openqa.selenium.By;

public class LoginPage {

  private SharedDriver driver;

  public LoginPage(SharedDriver driver) {
    this.driver = driver;
  }


  public void navigateTo() {
    driver.navigate().to(PlayContainer.getBaseUrl() + "/login");
    Assert.assertEquals("Logga in", driver.findElement(By.id("page_name")).getText());
  }

  public boolean isViewing() {
    return "Logga in".equals(driver.findElement(By.id("page_name")).getText());
  }

  public void loginUsingPw(String email, String password) {
    driver.findElement(By.id("email")).sendKeys(email);
    driver.findElement(By.id("password")).sendKeys(password);
    driver.findElement(By.tagName("form")).submit();
  }
}
