package acceptance.steps;

import acceptance.pages.StartPage;
import acceptance.pages.LoginPage;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import models.User;
import org.junit.Assert;
import util.Inspect;

public class LoginLogoutSteps {

  private final StartPage startPage;
  private final LoginPage loginPage;
  private User user = null;

  public LoginLogoutSteps(StartPage startPage, LoginPage loginPage) {
    this.startPage = startPage;
    this.loginPage = loginPage;
  }

  @Given("^the (\\S+) user exist$")
  public void verifyUserExists(String userName) throws Throwable {
    User user = Inspect.getUser(userName);
    Assert.assertNotNull(user);
    this.user = user;
  }

  @Given("^is not logged in$")
  public void verifyNotLoggedIn() throws Throwable {
    startPage.navigateTo();
    loginPage.ensureLoggedOut();
  }

  @When("^(\\S+) logs in$")
  public void login(String userName) throws Throwable {
    startPage.selectLogin();
    Assert.assertTrue("Not on login page!", loginPage.isViewing());
    loginPage.loginUsingPw(user.email(), userName.toLowerCase());
  }

  @Then("^the user should be logged in$")
  public void verifyLoggedIn() throws Throwable {
    Assert.assertTrue("Not logged in!", loginPage.isLoggedIn());
  }
}
