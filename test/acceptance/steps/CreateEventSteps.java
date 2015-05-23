package acceptance.steps;

import acceptance.pages.CreateEventPage;
import acceptance.pages.LoginPage;
import cucumber.api.java.After;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import models.Group;
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
    private String groupName;
    private String eventName;
    private Date eventDateTime;

    public CreateEventSteps(LoginPage loginPage, CreateEventPage createEventPage) {
        this.loginPage = loginPage;
        this.createEventPage = createEventPage;
    }

    @Given("^the (\\S+) plan a (\\S+) (.*)$")
    public void createEventAtTime(String groupName, String eventName, String eventDateTime) throws Throwable {
        this.groupName = groupName;
        this.eventName = eventName;
        this.eventDateTime = new PrettyTimeParser().parse(eventDateTime).get(0);
    }

    @When("^The administrator creates the event$")
    public void createEvent() throws Throwable {
        loginPage.navigateTo();
        loginPage.loginUsingPw("admin@crisp.se", "admin");
        Group group = Inspect.getGroup(groupName);
        createEventPage.navigateTo(asLong(group.id()));
        createEventPage.submitForm(eventName, "Let's get together and fine dine!", "The Ritz", new DateTime(eventDateTime));
    }

    @Then("^the event (\\S+) exists in the (\\S+) group$")
    public void verifyEventExists(String eventName, String groupName) throws Throwable {
        Assert.assertTrue("Event " + eventName + " wasn't found for group " + groupName, Inspect.isEventAvailableForGroup(eventName, groupName));
    }

    @And("^nobody has signed up yet$")
    public void verifyNobodySignedUp() throws Throwable {
        // Express the Regexp above with the code you wish you had
        // throw new PendingException();
    }

    @After
    public void cleanUpScenario() {
        if(groupName!= null) {
            TearDown.removeGroupAndMembers(groupName);
            groupName = null;
        }
    }
}
