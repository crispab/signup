package se.crisp.signup4.acceptance.pages;

import se.crisp.signup4.acceptance.PlayContainer;
import se.crisp.signup4.acceptance.SharedDriver;
import se.crisp.signup4.models.Group;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import se.crisp.signup4.util.Inspect;

import javax.inject.Inject;

public class GroupPage {
  private final SharedDriver driver;

  @Inject Inspect inspect;

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
    Group group = inspect.getGroup(groupName);
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
    waitForAnimationToComplete();
    return this;
  }

  private void waitForAnimationToComplete() {
    synchronized (driver) {
      try {
        // Hate this! There is an animation that displays the member list that takes some time to complete
        driver.wait(300);
      } catch (InterruptedException e) {
        // just ignore
      }
    }
  }
}
