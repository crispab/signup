package acceptance;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import util.TestHelper;

import java.util.concurrent.TimeUnit;

/**
 * Copyright (c) 2008-2014 The Cucumber Organisation
 *
 * License: https://github.com/cucumber/cucumber-jvm/blob/master/LICENCE
 * Original file: https://github.com/cucumber/cucumber-jvm/blob/master/examples/java-webbit-websockets-selenium/src/test/java/cucumber/examples/java/websockets/SharedDriver.java
 *
 */
public class SharedDriver extends EventFiringWebDriver {

  private static final WebDriver realDriver = createWebDriver();

  private static WebDriver createWebDriver() {
    String driverType = getWebDriverType();
    final WebDriver driver;
    switch (driverType.toLowerCase()) {
      case "htmlunit":
        driver = new HtmlUnitDriver(BrowserVersion.FIREFOX_24);
        break;
      case "firefox":
        driver = new FirefoxDriver();
        break;
      case "phantomjs":
        driver = new PhantomJSDriver();
        break;
      default:
        throw new RuntimeException("WebDriver type not correctly configured. Unknown driver type: '" + driverType + "'");
    }
    driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
    driver.manage().window().setSize(new Dimension(1280,800));
    return driver;
  }

  private static String getWebDriverType() {
    return TestHelper.readPropertyFromFile("test.webdriver.type", "conf/application.conf");
  }


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
    if(scenario.isFailed()) {
      try {
        byte[] screenshot = getScreenshotAs(OutputType.BYTES);
        scenario.embed(screenshot, "image/png");
      } catch (UnsupportedOperationException somePlatformsDontSupportScreenshots) {
        System.err.println(somePlatformsDontSupportScreenshots.getMessage());
      }
    }
  }
}