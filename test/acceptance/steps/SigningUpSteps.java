package acceptance.steps;

import acceptance.pages.SignUpPage;
import cucumber.api.java.After;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import models.Event;
import models.Group;
import models.User;
import org.junit.Assert;
import util.Inspect;
import util.SetUp;
import util.TearDown;

import java.util.List;

// TODO: use a session ID to make names etc unique
// TODO: make BASE_URL configurable (point to CI-test install)

public class SigningUpSteps {

  public final String BASE_URL = "http://localhost:9000";

  List<User> members = null;
  Group group = null;
  Event event = null;
  SignUpPage signUpPage;

  public SigningUpSteps(SignUpPage signUpPage) {
    this.signUpPage = signUpPage;
    signUpPage.setBaseUrl(BASE_URL);
  }

  private User findMember(String memberName) {
    for(User member: members){
      if(member.firstName().equals(memberName))
        return member;
    }
    throw new IllegalArgumentException("Can't find member with name " + memberName);
  }

  private long asLong(scala.Option<Object> optionLong) {
    return Long.parseLong(optionLong.get().toString());
  }

  @Given("^the group (\\S+) exist with (.*)$")
  public void createGroup(String groupName, List<String> memberNames) throws Throwable {
    TearDown.removeGroupAndMembers(groupName);
    members = SetUp.createUsers(memberNames);
    group = SetUp.createGroup(groupName);
    SetUp.addMembers(group, members);
  }

  @Given("^the group (\\S+) has an event (\\S+)$")
  public void createEvent(String groupName, String eventName) throws Throwable {
    Assert.assertEquals(group.name(), groupName);
    event = SetUp.createMorningEvent(group, eventName);
  }

  @When("^(\\S+) navigates to his sign up page$")
  public void navigateToSignUpPage(String member) throws Throwable {
    signUpPage.navigateTo(asLong(findMember(member).id()), asLong(event.id()));
  }

  @When("^enters a comment \"([^\"]*)\"$")
  public void enterComment(String comment) throws Throwable {
    signUpPage.addComment(comment);
  }

  @When("^submits he is (\\S+)$")
  public void setParticipationAndSubmit(String status) throws Throwable {
    signUpPage.setStatus(status);
    signUpPage.save();
  }

  @Then("^the number of participants is (\\d+)$")
  public void verifyParticipants(int participants) throws Throwable {
    Assert.assertEquals(participants, Inspect.getNoOfOn(event));
  }

  @Then("^the participation of (\\S+) is (\\S+)$")
  public void verifyStatus(String member, String status) throws Throwable {
    Assert.assertEquals(status, Inspect.getStatus(findMember(member), event));
  }

  @Then("^the comment by (\\S+) is \"([^\"]*)\"$")
  public void verifyComment(String member, String comment) throws Throwable {
    Assert.assertEquals(comment, Inspect.getComment(findMember(member), event));
  }

  @After
  public void cleanUpScenario() {
    if(group!= null) {
      TearDown.removeGroupAndMembers(group.name());
      group = null;
    }
  }

}
