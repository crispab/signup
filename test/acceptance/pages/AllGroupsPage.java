package acceptance.pages;

import acceptance.PlayContainer;
import acceptance.SharedDriver;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;

public class AllGroupsPage {
  private final SharedDriver driver;

  public AllGroupsPage(SharedDriver driver) {
    this.driver = driver;
  }

  public boolean isViewing() {
    return "Grupper".equals(driver.findElement(By.id("page_name")).getText());
  }

  public boolean verifyGroupListed(String groupName) {
    try {
      driver.findElement(By.xpath("//a[contains(., '" + groupName + "')]"));
      return true;
    } catch (NoSuchElementException ex) {
      return false;
    }
  }

  public AllGroupsPage navigateTo() {
    driver.navigate().to(PlayContainer.getBaseUrl() + "/groups");
    Assert.assertTrue("Not on groups page!", isViewing());
    return this;
  }

  public AllGroupsPage selectGroup(String groupName) {
    driver.findElement(By.xpath("//a[contains(., '" + groupName + "')]")).click();
    return this;
  }
}
