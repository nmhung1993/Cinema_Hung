package page;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static utils.TestReport.testReport;

public class PageSignin extends PageBase {
    AppiumDriver appiumDriver;
    private WebDriverWait wait;

    @FindBy(id = "email_ed")
    WebElement email_ed;

    @FindBy(id = "password_ed")
    WebElement password_ed;

    @FindBy(id = "btnSignup")
    WebElement btnSignup;

    @FindBy(id = "btnSignin")
    WebElement btnSignin;

    @FindBy(id = "dialog_body")
    WebElement dialog_body;

    public PageSignin(AppiumDriver appiumDriver){
        this.appiumDriver = appiumDriver;
        PageFactory.initElements(appiumDriver, this);
        this.wait = new WebDriverWait(appiumDriver, 30);
    }
    public MainPage signIn(String email, String password){
        System.out.println("--- Enter sign in function ---");
        email_ed.sendKeys(email);
        appiumDriver.hideKeyboard();
        password_ed.sendKeys(password);
        appiumDriver.hideKeyboard();
//        testReport(this.appiumDriver,true,"Nhập liệu đăng nhập",true);
        btnSignin.click();
        waitForLoading(2);
//        if (dialog_body.isDisplayed())
//            testReport(this.appiumDriver,false,"Đăng nhập thất bại",true);

//        Helper apiHelper = new Helper();
//        try {
//            apiHelper.activeAccount(phone);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return new MainPage(appiumDriver);
    }

    public PageSignup clickSignup(){
        wait.until(ExpectedConditions.elementToBeClickable(By.id("btnSignup")));
        btnSignup.click();
        return new PageSignup(appiumDriver);
    }
}