package acceptance.signup;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import acceptance.cucumber.examples.java.websockets.SharedDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginLogoutSteps {

    private final WebDriver driver;

    // Instantiated by Cucumber/Dependency Injection/Picocontainer
    public LoginLogoutSteps(SharedDriver driver) {
        this.driver = driver;
    }

    @When("^I click on the Login menu item$")
    public void i_click_on_the_Login_menu_item() throws Throwable {
        driver.findElement(By.xpath("//a[@href='/login']")).click();
    }

    @When("^submit credentials$")
    public void submit_credentials() throws Throwable {
        driver.findElement(By.id("email")).sendKeys("admin@crisp.se");
        driver.findElement(By.id("password")).sendKeys("admin");
        driver.findElement(By.xpath("//button[contains(., 'Logga in')]")).click();
    }

    @Then("^I should be logged in$")
    public void i_should_be_logged_in() throws Throwable {
        // Will throw if element can't be found
        driver.findElement(By.xpath("//a[contains(., 'Admin Istratör')]"));
    }

    @Given("^I am logged in$")
    public void i_am_logged_in() throws Throwable {
        driver.manage().deleteAllCookies();

        new IndexPageSteps((SharedDriver)driver).i_am_on_the_start_page();
        i_click_on_the_Login_menu_item();
        submit_credentials();
        i_should_be_logged_in();
    }

    @When("^I click My Identity Dropdown$")
    public void i_click_My_Identity_Dropdown() throws Throwable {
        driver.findElement(By.xpath("//a[contains(., 'Admin Istratör')]")).click();
    }

    @When("^I click logout$")
    public void i_click_logout() throws Throwable {
        driver.findElement(By.xpath("//a[@href='/logout']")).click();
    }

    @Then("^I should be logged out$")
    public void i_should_be_logged_out() throws Throwable {
        driver.findElement(By.xpath("//div[contains(., 'Du har loggats ut!')]"));
    }
}
