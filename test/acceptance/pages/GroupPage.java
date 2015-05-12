package acceptance.pages;

import acceptance.SharedDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;

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
}
