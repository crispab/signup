package se.crisp.signup4.web

import java.util

import org.scalatest._
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatestplus.play.{ConfiguredBrowser, ConfiguredServer}
import se.crisp.signup4.models.User
import se.crisp.signup4.util.{Inspect, SetUp, TearDown}
import se.crisp.signup4.web.pages._


@DoNotDiscover class SignUpForAnEventFeature
  extends FeatureSpec
    with Matchers
    with OptionValues
    with ConfiguredServer
    with ConfiguredBrowser
    with GivenWhenThen
    with TableDrivenPropertyChecks
    with BeforeAndAfterEach {

  override protected def afterEach() {
    lazy val tearDown = app.injector.instanceOf[TearDown]
    tearDown.removeGroupAndMembers("Friends")
  }

  feature("Sign up for an event") {
    lazy val setUp = app.injector.instanceOf[SetUp]
    lazy val inspect = app.injector.instanceOf[Inspect]

    lazy val baseUrl = "http://localhost:19001"
    lazy val startPage = new StartPage(baseUrl, webDriver)
    lazy val allGroupsPage = new AllGroupsPage(baseUrl, webDriver)
    lazy val groupPage = new GroupPage(baseUrl, webDriver, inspect)
    lazy val userPage = new UserPage(baseUrl, webDriver)
    lazy val signUpPage = new SignUpPage(baseUrl, webDriver)

    val examplesTom =
      Table(
        ("coming", "participants", "status"),
        ("On", 1, "On"),
        ("Off", 0, "Off"),
        ("Maybe", 0, "Maybe")
      )
    for ((coming, participants, status) <- examplesTom) {
      scenario(s"Tom responds to an invite with $coming") {

        Given("the group Friends exist with Tom, Dick, Harry")
        val users = setUp.createUsers(util.Arrays.asList("Tom", "Dick", "Harry"))
        val tom = users.get(0)
        val group = setUp.createGroup("Friends")
        setUp.addMembers(group, users)

        And("the group Friends has an event Breakfast")
        val event = setUp.createMorningEvent(group, "Breakfast")

        When("Tom navigates to his sign up page")
        signUpPage.navigateTo(tom.id.get, event.id.get)

        And(s"submits he is $coming")
        signUpPage.setStatus(coming)
        signUpPage.save()

        Then(s"the number of participants is $participants")
        assert(participants == inspect.getNoOfOn(event))

        And(s"the participation of Tom is $status")
        assert(status == inspect.getStatus(tom, event))
      }
    }


    val examplesEverybody =
      Table(
        ("user",  "comment",               "coming"),
        ("Tom",   "Yay!!",                 "On"),
        ("Dick",  "Naah, don't think so.", "Off"),
        ("Harry", "Hmm, I don't know...",  "Maybe")
      )
    for ((user, comment, coming) <- examplesEverybody) {
      scenario(s"$user respond to an event with comments") {
        Given("the group Friends exist with Tom, Dick, Harry")
        val users = setUp.createUsers(util.Arrays.asList("Tom", "Dick", "Harry"))
        val group = setUp.createGroup("Friends")
        setUp.addMembers(group, users)

        And("the group Friends has an event Breakfast")
        val event = setUp.createMorningEvent(group, "Breakfast")

        When(s"$user navigates to his sign up page")
        val member = findMember(user, users)
        signUpPage.navigateTo(member.id.get, event.id.get)

        And(s"enters a comment '$comment'")
        signUpPage.addComment(comment)

        And(s"submits he is $coming")
        signUpPage.setStatus(coming)
        signUpPage.save()

        Then(s"the participation of $user is $coming")
        assert(coming == inspect.getStatus(member, event))

        And(s"the comment by $user is '$comment'")
        assert(comment == inspect.getComment(member, event))
      }
    }
  }

  private def findMember(memberName: String, members: util.List[User]): User = {
    import scala.collection.JavaConversions._
    for (member <- members) {
      if (member.firstName == memberName) return member
    }
    throw new IllegalArgumentException("Can't find member with name " + memberName)
  }

}
