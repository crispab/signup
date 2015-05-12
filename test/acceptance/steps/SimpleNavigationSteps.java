package acceptance.steps;

import acceptance.pages.AllGroupsPage;
import acceptance.pages.GroupPage;
import acceptance.pages.SignUpPage;
import acceptance.pages.StartPage;
import cucumber.api.PendingException;
import cucumber.api.java.After;
import cucumber.api.java.en.And;
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

public class SimpleNavigationSteps {
  List<User> members = null;
  Group group = null;
  StartPage startPage;
  AllGroupsPage allGroupsPage;
  GroupPage groupPage;

  public SimpleNavigationSteps(StartPage startPage, AllGroupsPage allGroupsPage, GroupPage groupPage) {
    this.startPage = startPage;
    this.allGroupsPage = allGroupsPage;
    this.groupPage = groupPage;
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

  @Given("^the user has a blank web browser$")
  public void browseToBlank() throws Throwable {
    startPage.navigateToBlank();
  }

  @Given("^pointing the browser to the start page$|^the user is on the start page$")
  public void navigateToStartPage() throws Throwable {
    startPage.navigateTo();
  }

  @Then("^the start page should display$")
  public void verifyOnStartPage() throws Throwable {
    startPage.isViewing();
  }

  @When("^selecting the home link$")
  public void selectHomeLink() throws Throwable {
    startPage.selectHome();
  }

  @When("^selecting the Groups menu item$")
  public void selectGroupsMenuItem() throws Throwable {
    startPage.selectGroups();
  }

  @Then("^the groups page should display$")
  public void verifyOnGroupsPage() throws Throwable {
    Assert.assertTrue("Not on groups page!", allGroupsPage.isViewing());
  }

  @Then("^the group (\\S+) is listed on the page$")
  public void verifyGroupListed(String groupName) throws Throwable {
    Assert.assertTrue("Group is not listed on page!", allGroupsPage.verifyGroupListed(groupName));
  }
}
