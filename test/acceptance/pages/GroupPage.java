package acceptance.pages;

import acceptance.PlayContainer;
import acceptance.SharedDriver;
import models.Group;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import util.Inspect;

public class GroupPage {
  private final SharedDriver driver;

  public GroupPage(SharedDriver driver) {
    this.driver = driver;
  }

  public boolean isViewing(String groupName) {
    return groupName.equals(driver.findElement(By.id("page_name")).getText());
  }

  public boolean verifyMemberListed(String memberName) {
    try {
      driver.findElement(By.xpath("//a[contains(., '" + memberName + "')]"));
      return true;
    } catch (NoSuchElementException ex) {
      return false;
    }
  }

  public GroupPage navigateTo(String groupName) {
    Group group = Inspect.getGroup(groupName);
    driver.navigate().to(PlayContainer.getBaseUrl() + "/groups/" + group.id().get());
    Assert.assertTrue("Not viewing group " + groupName + "!", isViewing(groupName));
    return this;
  }

  public GroupPage selectMember(String memberName) {
    driver.findElement(By.xpath("//a[contains(., '" + memberName + "')]")).click();
    return this;
  }

  public GroupPage toggleMemberList() {
    driver.findElement(By.id("toggle_icon")).click();
    return this;
  }
}
