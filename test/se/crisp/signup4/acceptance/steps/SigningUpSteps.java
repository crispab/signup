package se.crisp.signup4.acceptance.steps;

import se.crisp.signup4.acceptance.pages.SignUpPage;
import cucumber.api.java.After;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import se.crisp.signup4.models.Event;
import se.crisp.signup4.models.Group;
import se.crisp.signup4.models.User;
import org.junit.Assert;
import se.crisp.signup4.util.Inspect;
import se.crisp.signup4.util.SetUp;
import se.crisp.signup4.util.TearDown;

import java.util.List;

import static se.crisp.signup4.util.Conversion.*;

public class SigningUpSteps {
  private List<User> members = null;
  private Group group = null;
  private Event event = null;
  private final SignUpPage signUpPage;

  public SigningUpSteps(SignUpPage signUpPage) {
    this.signUpPage = signUpPage;
  }

  private User findMember(String memberName) {
    for(User member: members){
      if(member.firstName().equals(memberName))
        return member;
    }
    throw new IllegalArgumentException("Can't find member with name " + memberName);
  }

  @Given("^the group (\\S+) exist with (.*)$")
  public void createGroup(String groupName, List<String> memberNames) throws Throwable {
    group = SetUp.createGroup(groupName);
    members = SetUp.createUsers(memberNames);
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
    signUpPage.setStatus(status).save();
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
      event = null;
      members = null;
    }
  }

}
