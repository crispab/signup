package se.crisp.signup4.acceptance;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;


@RunWith(Cucumber.class)
@CucumberOptions(
    monochrome = true,
    plugin = {"pretty", "html:target/test-reports/html", "junit:target/test-reports/junit/cucumber.xml"},
    features = {"test/se/crisp/signup4/acceptance/features"}
)
public class RunCucumberTest {}
