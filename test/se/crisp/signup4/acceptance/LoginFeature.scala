package se.crisp.signup4.acceptance

import org.scalatest._
import org.scalatestplus.play.{ConfiguredBrowser, ConfiguredServer}
import se.crisp.signup4.util.{Inspect, TearDown}
import se.crisp.signup4.acceptance.pages._

@DoNotDiscover class LoginFeature
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

  feature("Login") {
    lazy val inspect = app.injector.instanceOf[Inspect]

    lazy val baseUrl = "http://localhost:19001"
    lazy val startPage = new StartPage(baseUrl, webDriver)
    lazy val loginPage = new LoginPage(baseUrl, webDriver)

    scenario("The admin logs in") {

      Given("the Admin user exist")
      val user = inspect.getUser("Admin")
      assert(user != null)

      And("is not logged in")
      startPage.navigateTo()
      loginPage.ensureLoggedOut()

      When("Admin logs in")
      startPage.selectLogin()
      loginPage.loginUsingPw(user.email, "Admin".toLowerCase)

      Then("the user should be logged in")
      loginPage.isLoggedIn shouldBe true
    }

  }
}
