package acceptance.steps;

import acceptance.pages.CreateEventPage;
import acceptance.pages.ErrorPage;
import acceptance.pages.LoginPage;
import cucumber.api.java.After;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import se.crisp.signup4.models.Event;
import se.crisp.signup4.models.Group;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.ocpsoft.prettytime.nlp.PrettyTimeParser;
import cucumber.api.java.en.Given;
import util.Inspect;
import util.TearDown;

import java.util.Date;

import static util.Conversion.*;

public class CreateEventSteps {
  private final LoginPage loginPage;
  private final CreateEventPage createEventPage;
  private final ErrorPage errorPage;
  private String groupName;
  private String eventName;
  private Date eventDateTime;

  public CreateEventSteps(LoginPage loginPage, CreateEventPage createEventPage, ErrorPage errorPage) {
    this.loginPage = loginPage;
    this.createEventPage = createEventPage;
    this.errorPage = errorPage;
  }

  @Given("^the (\\S+) plan a (\\S+) (.*)$")
  public void planEventAtTime(String groupName, String eventName, String eventDateTime) throws Throwable {
    this.groupName = groupName;
    this.eventName = eventName;
    this.eventDateTime = new PrettyTimeParser().parse(eventDateTime).get(0);
  }

  @When("^The administrator creates the event$")
  public void createEvent() throws Throwable {
    loginPage.navigateTo().loginUsingPw("admin@crisp.se", "admin");
    Group group = Inspect.getGroup(groupName);
    createEventPage.navigateTo(asLong(group.id())).submitForm(eventName, "Let's get together and fine dine!", "The Ritz", new DateTime(eventDateTime));
    Assert.assertFalse("Got an error page!!!", errorPage.isViewing());
  }

  @Then("^the event (\\S+) exists in the (\\S+) group$")
  public void verifyEventExists(String eventName, String groupName) throws Throwable {
    Assert.assertTrue("Event " + eventName + " wasn't found for group " + groupName, Inspect.isEventAvailableForGroup(eventName, groupName));
  }

  @Then("^nobody has signed up yet$")
  public void verifyNobodySignedUp() throws Throwable {
    Event event = Inspect.getEvent(eventName, groupName);
    Assert.assertEquals(0, Inspect.getNoOfOn(event));
  }

  @After
  public void cleanUpScenario() {
    if (groupName != null) {
      TearDown.removeGroupAndMembers(groupName);
      groupName = null;
    }
  }
}
