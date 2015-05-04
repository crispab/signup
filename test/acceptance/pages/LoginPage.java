package acceptance.pages;

import org.junit.Assert;
import org.openqa.selenium.By;

public class LoginPage {

  private SharedDriver driver;
  private String baseUrl;

  public LoginPage(SharedDriver driver) {
    this.driver = driver;
  }

  public void setBaseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  public void navigateTo() {
    driver.navigate().to(baseUrl + "/login");
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
