package acceptance.pages;

import acceptance.PlayContainer;
import acceptance.SharedDriver;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Assert;
import org.openqa.selenium.By;

public class CreateEventPage {
    private final SharedDriver driver;

    public CreateEventPage(SharedDriver driver) {
        this.driver = driver;
    }

    public void navigateTo(long groupId) {
        driver.navigate().to(PlayContainer.getBaseUrl() + "/events/new?groupId=" + groupId);
        Assert.assertTrue("Not viewing create new event page!", isViewing());
    }

    public boolean isViewing() {
        return "Ny".equals(driver.findElement(By.id("page_name")).getText());
    }

    public void submitForm(String eventName, String description, String venue, DateTime eventDateTime) {
        driver.findElement(By.id("name")).sendKeys(eventName);
        // driver.findElement(By.id("xxx")).sendKeys(description);
        driver.findElement(By.id("venue")).sendKeys(venue);

        driver.findElement(By.id("start_date")).sendKeys(date(eventDateTime));
        driver.findElement(By.id("start_time")).sendKeys(time(eventDateTime));
        driver.findElement(By.id("end_time")).sendKeys(time(eventDateTime.plusHours(3)));

        driver.findElement(By.id("action")).click();
    }

    private String date(DateTime eventDateTime) {
        return ISODateTimeFormat.date().print(eventDateTime.getMillis());
    }

    private String time(DateTime eventDateTime) {
        return ISODateTimeFormat.hourMinute().print(eventDateTime.getMillis());
    }
}
