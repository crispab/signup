package se.crisp.signup4.acceptance.pages;

import se.crisp.signup4.acceptance.PlayContainer;
import se.crisp.signup4.acceptance.SharedDriver;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;

public class LoginPage {

  private final SharedDriver driver;

  public LoginPage(SharedDriver driver) {
    this.driver = driver;
  }

  public LoginPage navigateTo() {
    driver.navigate().to(PlayContainer.getBaseUrl() + "/login");
    Assert.assertTrue("Not viewing login page", isViewing());
    return this;
  }

  public boolean isViewing() {
    return "Logga in".equals(driver.findElement(By.id("page_name")).getText());
  }

  public LoginPage loginUsingPw(String email, String password) {
    driver.findElement(By.id("email")).sendKeys(email);
    driver.findElement(By.id("password")).sendKeys(password);
    driver.findElement(By.tagName("form")).submit();
    return this;
  }

  public LoginPage ensureLoggedOut() {
    driver.manage().deleteAllCookies();
    Assert.assertFalse("Still loggen in!", isLoggedIn());
    return this;
  }

  public boolean isLoggedIn() {
    Cookie cookie = driver.manage().getCookieNamed("PLAY2AUTH_SESS_ID");
    return (cookie != null);
  }
}
