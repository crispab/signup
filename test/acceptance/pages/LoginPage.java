package acceptance.pages;

import acceptance.PlayContainer;
import acceptance.SharedDriver;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;

public class LoginPage {

  private final SharedDriver driver;

  public LoginPage(SharedDriver driver) {
    this.driver = driver;
  }

  public void navigateTo() {
      driver.navigate().to(PlayContainer.getBaseUrl() + "/login");
      Assert.assertTrue("Not viewing login page", isViewing());
  }

  public boolean isViewing() {
    return "Logga in".equals(driver.findElement(By.id("page_name")).getText());
  }

  public void loginUsingPw(String email, String password) {
    driver.findElement(By.id("email")).sendKeys(email);
    driver.findElement(By.id("password")).sendKeys(password);
    driver.findElement(By.tagName("form")).submit();
  }

  public void ensureLoggedOut() {
    driver.manage().deleteAllCookies();
    Assert.assertFalse("Still loggen in!", isLoggedIn());
  }

  public boolean isLoggedIn() {
    Cookie cookie = driver.manage().getCookieNamed("PLAY2AUTH_SESS_ID");
    return (cookie != null);
  }
}
