package signup;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import cucumber.examples.java.websockets.SharedDriver;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class IndexPageSteps {

    private final WebDriver driver;

    // Instantiated by Cucumber/Dependency Injection/Picocontainer
    public IndexPageSteps(SharedDriver driver) {
        this.driver = driver;
    }

    @Given("^I have a web browser running$")
    public void i_have_a_web_browser_running() throws Throwable {
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

    @Given("^I am on the start page$")
    public void i_am_on_the_start_page() throws Throwable {
        driver.navigate().to("http://localhost:19000");
    }

    @When("^I click on the Groups menu item$")
    public void i_click_on_the_Groups_menu_item() throws Throwable {
        driver.findElement(By.xpath("//a[@href='/groups']")).click();
    }

    @Then("^the Groups page should display$")
    public void the_Groups_page_should_display() throws Throwable {
        // Will throw if element can't be found
        driver.findElement(By.xpath("//a[contains(., 'Crisp Rocket Days')]"));
    }

    @Given("^I am on the groups page$")
    public void i_am_on_the_groups_page() throws Throwable {
        driver.navigate().to("http://localhost:19000");
        driver.findElement(By.xpath("//a[@href='/groups']")).click();
    }

    @When("^I click on a group$")
    public void i_click_on_a_group() throws Throwable {
        driver.findElement(By.xpath("//a[contains(., 'Crisp Rocket Days')]")).click();
    }

    @Then("^the group page should display$")
    public void the_group_page_should_display() throws Throwable {
        Assert.assertEquals(
                "Crisp Rocket Days",
                driver.findElement(By.tagName("h2")).getText());
    }

    @Given("^I am on a group page$")
    public void i_am_on_a_group_page() throws Throwable {
        driver.navigate().to("http://localhost:19000");
        driver.findElement(By.xpath("//a[@href='/groups']")).click();
        driver.findElement(By.xpath("//a[contains(., 'Crisp Rocket Days')]")).click();
    }

    @When("^I click on a user$")
    public void i_click_on_a_user() throws Throwable {
        driver.findElement(By.xpath("//a[contains(., 'Torbjörn Fälldin')]")).click();
    }

    @Then("^the user page should display$")
    public void the_user_page_should_display() throws Throwable {
        Assert.assertEquals(
                "Torbjörn Fälldin",
                driver.findElement(By.tagName("h2")).getText());
    }
}
