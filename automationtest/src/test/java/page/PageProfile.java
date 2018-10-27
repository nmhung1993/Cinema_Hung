package page;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class PageProfile extends PageBase {
    AppiumDriver appiumDriver;
    private WebDriverWait wait;

    @FindBy(id = "btnSignOut")
    WebElement btnSignOut;

    public PageProfile(AppiumDriver appiumDriver){
        this.appiumDriver = appiumDriver;
        PageFactory.initElements(appiumDriver, this);
        this.wait = new WebDriverWait(appiumDriver, 30);
    }
    public MainPage SignOut(){
        wait.until(ExpectedConditions.elementToBeClickable(By.id("btnSignOut")));
        btnSignOut.click();
        WebElement signOut = appiumDriver.findElement(By.id("btDialogYes"));
        signOut.click();
        waitForLoading(2);
        return new MainPage(appiumDriver);
    }
}