package signup;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class Stepdefs {

    private WebDriver driver;

    @Given("^I have a web browser running$")
    public void i_have_a_web_browser_running() throws Throwable {
        driver = new FirefoxDriver();
    }

    @When("^I enter the site index page url$")
    public void i_enter_the_site_s_index_page_url() throws Throwable {
        driver.navigate().to("http://localhost:19000");
    }

    @Then("^it should display$")
    public void it_should_display() throws Throwable {
        Assert.assertEquals(
                "Välkommen till SignUp",
                driver.findElement(By.tagName("h2")).getText());
    }
}
