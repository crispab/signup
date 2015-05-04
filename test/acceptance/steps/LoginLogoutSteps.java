package acceptance.steps;

import acceptance.pages.IndexPage;
import acceptance.pages.LoginPage;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import models.User;
import org.junit.Assert;
import util.Inspect;

public class LoginLogoutSteps {

  public final String BASE_URL = "http://localhost:9000";
  private final IndexPage indexPage;
  private final LoginPage loginPage;
  private User user = null;

  public LoginLogoutSteps(IndexPage indexPage, LoginPage loginPage) {
    this.indexPage = indexPage;
    this.loginPage = loginPage;
    indexPage.setBaseUrl(BASE_URL);
    loginPage.setBaseUrl(BASE_URL);
  }

  @Given("^the (\\S+) user exist$")
  public void verifyUserExists(String userName) throws Throwable {
    User user = Inspect.getUser(userName);
    Assert.assertNotNull(user);
    this.user = user;
  }

  @Given("^is not logged in$")
  public void verifyNotLoggedIn() throws Throwable {
    indexPage.navigateTo();
    Assert.assertEquals("Logga in", indexPage.getLoggedInName());
  }

  @When("^(\\S+) logs in$")
  public void login(String userName) throws Throwable {
    indexPage.selectLogin();
    Assert.assertTrue("Not on login page!", loginPage.isViewing());
    loginPage.loginUsingPw(user.email(), userName.toLowerCase());
  }

  @Then("^the user name should be visible on the screen$")
  public void verifyUserName() throws Throwable {
    Assert.assertTrue("Not logged in!", indexPage.getLoggedInName().startsWith(user.firstName()));
  }
}
