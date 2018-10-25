package page;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;

public class MainPage extends PageBase {
    AppiumDriver appiumDriver;
    private WebDriverWait wait;

    public MainPage(AppiumDriver appiumDriver){
        this.appiumDriver = appiumDriver;
        PageFactory.initElements(appiumDriver, this);
        this.wait = new WebDriverWait(appiumDriver, 30);
    }

    public void goToLoginPage() {
//        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("\t/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.view.ViewGroup/android.widget.FrameLayout[2]/android.view.ViewGroup/android.widget.RelativeLayout/android.widget.ImageButton")));
//        wait.until(ExpectedConditions.elementToBeClickable(By.id("btnUpload")));

        WebElement uploadBtn = appiumDriver.findElement(By.id("btnUpload"));
        uploadBtn.click();
        WebElement goToLogin = appiumDriver.findElement(By.id("btDialogYes"));
        goToLogin.click();
        WebElement inputEmail = appiumDriver.findElement(By.id("email_ed"));
        inputEmail.click();
        inputEmail.sendKeys("test7@hung.com");
        WebElement inputPassword = appiumDriver.findElement(By.id("password_ed"));
        inputPassword.click();
        inputPassword.sendKeys("1111");
        WebElement signinBtn = appiumDriver.findElement(By.id("btnSignin"));
        signinBtn.click();
    }
}
