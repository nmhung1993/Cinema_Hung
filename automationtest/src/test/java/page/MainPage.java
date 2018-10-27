package page;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;

import static utils.TestReport.testReport;

public class MainPage extends PageBase {
    AppiumDriver appiumDriver;
    private WebDriverWait wait;

    @FindBy(id = "btnUpload")
    WebElement btnUpload;

    @FindBy(id = "btnProfile")
    WebElement btnProfile;

    public MainPage(AppiumDriver appiumDriver){
        this.appiumDriver = appiumDriver;
        PageFactory.initElements(appiumDriver, this);
        this.wait = new WebDriverWait(appiumDriver, 30);
    }
    public PageSignin clickSignIn(){
        wait.until(ExpectedConditions.elementToBeClickable(By.id("btnUpload")));
        btnUpload.click();
        WebElement goToSignin = appiumDriver.findElement(By.id("btDialogYes"));
        goToSignin.click();
        return new PageSignin(appiumDriver);
    }
    public PageProfile clickProfile(){
        wait.until(ExpectedConditions.elementToBeClickable(By.id("btnProfile")));
        btnProfile.click();
        return new PageProfile(appiumDriver);
    }
    public PageUpload clickUpload(){
        wait.until(ExpectedConditions.elementToBeClickable(By.id("btnUpload")));
        btnUpload.click();
        return new PageUpload(appiumDriver);
    }
    public int verifySigninSuccess(){
        boolean result = false;
        if (btnProfile.isDisplayed()){
            result = true;
        }
//        testReport(this.appiumDriver, result, "Sign In SUCCESS", "Sign In FAIL", true);
        if (result == true)
            return 1;
        else
            return 0;
    }
    public int verifySignoutSuccess() {
        boolean result = wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("btnProfile")));
//        testReport(this.appiumDriver, result, "Sign Out SUCCESS", "Sign Out FAIL", true);
        if (result == true)
            return 1;
        else return 0;
    }
    public int verifyUploadSuccess(String name) {
        boolean result = false;
        WebElement filmName = appiumDriver.findElement(By.xpath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.view.ViewGroup/android.widget.FrameLayout[2]/android.view.ViewGroup/android.widget.RelativeLayout/android.support.v7.widget.RecyclerView/android.widget.LinearLayout[1]/android.widget.LinearLayout/android.widget.LinearLayout/android.widget.TextView"));
        if(filmName.getText().equals(name)){
            result = true;
        }
        testReport(this.appiumDriver, result, "Upload SUCCESS", "Upload FAIL", true);
        if (result == true)
            return 1;
        else return 0;
    }
}
