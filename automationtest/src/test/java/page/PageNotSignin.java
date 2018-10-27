package page;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static utils.TestReport.testReport;

public class PageNotSignin extends PageBase {
    private AppiumDriver appiumDriver;
    private WebDriverWait wait;

    @FindBy(id = "btnUpload")
    WebElement btnUpload;

    @FindBy(id = "btnProfile")
    WebElement btnProfile;

    public PageNotSignin(AppiumDriver appiumDriver){
        this.appiumDriver = appiumDriver;
        PageFactory.initElements(appiumDriver,this);
        this.wait = new WebDriverWait(appiumDriver,30);
    }
/*    public PageSignin clickSignIn(){
        wait.until(ExpectedConditions.elementToBeClickable(By.id("btnUpload")));
        btnUpload.click();
        WebElement goToSignin = appiumDriver.findElement(By.id("btDialogYes"));
        goToSignin.click();
        return new PageSignin(appiumDriver);
    }*/
/*    public int verifySignoutSuccess(){
        boolean result = wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("btnProfile")));
        testReport(this.appiumDriver, result, "Sign Out SUCCESS", "Sign Out FAIL", true);
        if (result == true)
            return 1;
        return 0;
    }*/
}
