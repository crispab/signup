package acceptance.pages;

import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;

/**
 * Copyright (c) 2008-2014 The Cucumber Organisation
 *
 * License: https://github.com/cucumber/cucumber-jvm/blob/master/LICENCE
 * Original file: https://github.com/cucumber/cucumber-jvm/blob/master/examples/java-webbit-websockets-selenium/src/test/java/cucumber/examples/java/websockets/SharedDriver.java
 *
 */
public class SharedDriver extends EventFiringWebDriver {
  private static final WebDriver realDriver = new FirefoxDriver();

  static {
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        try {
          realDriver.quit();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });

  }

  public SharedDriver() {
    super(realDriver);
  }

  @Override
  public void close() {
    throw new UnsupportedOperationException("You shouldn't close this SharedDriver. It will close when the JVM exits.");
  }

  @Before
  public void deleteAllCookies() {
    manage().deleteAllCookies();
  }

  @After
  public void embedScreenshot(Scenario scenario) {
    try {
      byte[] screenshot = getScreenshotAs(OutputType.BYTES);
      scenario.embed(screenshot, "image/png");
    } catch (WebDriverException somePlatformsDontSupportScreenshots) {
      System.err.println(somePlatformsDontSupportScreenshots.getMessage());
    }
  }
}