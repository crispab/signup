package se.crisp.signup4.acceptance

import java.util

import org.joda.time.DateTime
import org.ocpsoft.prettytime.nlp.PrettyTimeParser
import org.scalatest._
import org.scalatestplus.play.{ConfiguredBrowser, ConfiguredServer}
import se.crisp.signup4.util.{Inspect, SetUp, TearDown}
import se.crisp.signup4.acceptance.pages._

@DoNotDiscover class CreateEventFeature
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

  feature("Create event") {
    lazy val setUp = app.injector.instanceOf[SetUp]
    lazy val inspect = app.injector.instanceOf[Inspect]

    lazy val baseUrl = "http://localhost:19001"
    lazy val loginPage = new LoginPage(baseUrl, webDriver)
    lazy val createEventPage = new CreateEventPage(baseUrl, webDriver)
    lazy val errorPage = new ErrorPage(baseUrl, webDriver)

    scenario("The administrator creates an event") {
      Given("the group Friends exist with Tom, Dick, Harry")
      val users = setUp.createUsers(util.Arrays.asList("Tom", "Dick", "Harry"))
      val group = setUp.createGroup("Friends")
      setUp.addMembers(group, users)

      And("the Friends plan a Dinner tomorrow at 20:00")
      val eventDateTime = new PrettyTimeParser().parse("tomorrow at 20:00").get(0)

      When("The administrator logs in")
      loginPage.navigateTo()
      loginPage.loginUsingPw("admin@crisp.se", "admin")

      And("creates the event")
      createEventPage.navigateTo(group.id.get)
      createEventPage.submitForm("Dinner", "Let's get together and fine dine!", "The Ritz", new DateTime(eventDateTime))

      Then("there is not an error page")
      assert(!errorPage.isViewing)

      And("the event Dinner exists in the Friends group")
      assert(inspect.isEventAvailableForGroup("Dinner", "Friends"))

      And("nobody has signed up yet")
      val event = inspect.getEvent("Dinner", "Friends")
      assert(0 == inspect.getNoOfOn(event))
    }
  }
}
