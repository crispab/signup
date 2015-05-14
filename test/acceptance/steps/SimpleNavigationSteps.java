package acceptance.steps;

import acceptance.pages.AllGroupsPage;
import acceptance.pages.GroupPage;
import acceptance.pages.StartPage;
import acceptance.pages.UserPage;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import org.junit.Assert;

import java.util.List;


public class SimpleNavigationSteps {
  private final StartPage startPage;
  private final AllGroupsPage allGroupsPage;
  private final GroupPage groupPage;
  private final UserPage userPage;

  public SimpleNavigationSteps(StartPage startPage, AllGroupsPage allGroupsPage, GroupPage groupPage, UserPage userPage) {
    this.startPage = startPage;
    this.allGroupsPage = allGroupsPage;
    this.groupPage = groupPage;
    this.userPage = userPage;
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

  @Given("^the user is on the groups page$")
  public void navigateToGroupsPage() throws Throwable {
    allGroupsPage.navigateTo();
  }

  @When("^selecting the (\\S+) group$")
  public void selectGroup(String groupName) throws Throwable {
    allGroupsPage.selectGroup(groupName);
  }

  @Then("^the group (\\S+) page should display$")
  public void verifyViewingGroup(String groupName) throws Throwable {
    Assert.assertTrue("Not on group page for " + groupName + "!", groupPage.isViewing(groupName));
  }

  @Then("^members (.*) is listed on the page$")
  public void membersListed(List<String> memberNames) throws Throwable {
    for (String memberName : memberNames) {
      Assert.assertTrue("Member " + memberNames + " not listed on page!", groupPage.verifyMemberListed(memberName));
    }
  }

  @When("^the user is on the group (\\S+) page$")
  public void navigateToGroup(String groupName) throws Throwable {
    groupPage.navigateTo(groupName);
  }

  @When("^selecting the member (\\S+)$")
  public void selectMember(String memberName) throws Throwable {
    groupPage.toggleMemberList();
    groupPage.selectMember(memberName);
  }

  @Then("^the user page for (\\S+) should display$")
  public void verifyViewingUser(String userName) throws Throwable {
    Assert.assertTrue("Not viewing user " + userName + "!", userPage.isViewing(userName));
  }
}
