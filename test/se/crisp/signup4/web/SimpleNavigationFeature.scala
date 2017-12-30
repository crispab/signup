package se.crisp.signup4.web

import java.util

import org.scalatest._
import org.scalatestplus.play.{ConfiguredBrowser, ConfiguredServer}
import se.crisp.signup4.util.{Inspect, SetUp, TearDown}
import se.crisp.signup4.web.pages.{AllGroupsPage, GroupPage, StartPage, UserPage}

@DoNotDiscover class SimpleNavigationFeature
  extends FeatureSpec
    with Matchers
    with OptionValues
    with ConfiguredServer
    with ConfiguredBrowser
    with GivenWhenThen
    with BeforeAndAfterEach {

  override protected def afterEach() {
    lazy val tearDown = app.injector.instanceOf[TearDown]
    tearDown.removeGroupAndMembers("Friends")
  }

  feature("Simple navigation") {
    lazy val setUp = app.injector.instanceOf[SetUp]
    lazy val inspect = app.injector.instanceOf[Inspect]

    lazy val baseUrl = "http://localhost:19001"
    lazy val startPage = new StartPage(baseUrl, webDriver)
    lazy val allGroupsPage = new AllGroupsPage(baseUrl, webDriver)
    lazy val groupPage = new GroupPage(baseUrl, webDriver, inspect)
    lazy val userPage = new UserPage(baseUrl, webDriver)

    scenario("The user requests the start page") {
      Given("the user has a blank web browser")
      startPage.navigateToBlank()

      When("pointing the browser to the start page")
      startPage.navigateTo()

      Then("the start page should display")
      startPage.isViewing shouldBe true
    }

    scenario("The home link should work") {
      Given("the user is on the start page")
      startPage.navigateTo()
      startPage.isViewing shouldBe true

      When("selecting the home link")
      startPage.selectHome()

      Then("the start page should display")
      startPage.isViewing shouldBe true
    }

    scenario("The user selects Groups in the menu") {
      Given("the group Friends exist with Tom, Dick, Harry")
      val users = setUp.createUsers(util.Arrays.asList("Tom", "Dick", "Harry"))
      val group = setUp.createGroup("Friends")
      setUp.addMembers(group, users)

      And("the user is on the start page")
      startPage.navigateTo()
      startPage.isViewing shouldBe true

      When("selecting the Groups menu item")
      startPage.selectGroups()

      Then("the groups page should display")
      allGroupsPage.isViewing shouldBe true

      And("the group Friends is listed on the page")
      allGroupsPage.verifyGroupListed("Friends") shouldBe true
    }

    scenario("The user clicks on a group on the groups page") {
      Given("the group Friends exist with Tom, Dick, Harry")
      val users = setUp.createUsers(util.Arrays.asList("Tom", "Dick", "Harry"))
      val group = setUp.createGroup("Friends")
      setUp.addMembers(group, users)

      And("the user is on the groups page")
      allGroupsPage.navigateTo()
      allGroupsPage.isViewing shouldBe true

      When("selecting the Friends group")
      allGroupsPage.selectGroup("Friends")

      Then("the group Friends page should display")
      groupPage.isViewing("Friends") shouldBe true

      And("members Tom, Dick, Harry is listed on the page")
      groupPage.verifyMemberListed("Tom") shouldBe true
      groupPage.verifyMemberListed("Dick") shouldBe true
      groupPage.verifyMemberListed("Harry") shouldBe true
    }

    scenario("The user clicks on a user on the groups page") {
      Given("the group Friends exist with Tom, Dick, Harry")
      val users = setUp.createUsers(util.Arrays.asList("Tom", "Dick", "Harry"))
      val group = setUp.createGroup("Friends")
      setUp.addMembers(group, users)

      And("the user is on the group Friends page")
      groupPage.navigateTo("Friends")
      groupPage.isViewing("Friends") shouldBe true

      When("selecting the member Dick")
      groupPage.toggleMemberList()
      groupPage.selectMember("Dick")

      Then("the user page for Dick should display")
      userPage.isViewing("Dick")
    }

  }
}
