package page;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import static utils.TestReport.testReport;

public class PageSignup extends PageBase {
    AppiumDriver appiumDriver;
    private WebDriverWait wait;

    @FindBy(id = "name_ed")
    WebElement name_ed;

    @FindBy(id = "email_ed")
    WebElement email_ed;

    @FindBy(id = "password_ed")
    WebElement password_ed;

    @FindBy(id = "repassword_ed")
    WebElement repassword_ed;

    @FindBy(id = "btnSignUp")
    WebElement btnSignUp;

    public PageSignup(AppiumDriver appiumDriver){
        this.appiumDriver = appiumDriver;
        PageFactory.initElements(appiumDriver, this);
        this.wait = new WebDriverWait(appiumDriver, 30);
    }
    public MainPage SignUp(String name, String email, String password){
        System.out.println("--- Enter sign up function ---");
        name_ed.sendKeys(name);
        email_ed.sendKeys(email);
        password_ed.sendKeys(password);
        appiumDriver.hideKeyboard();
        repassword_ed.sendKeys(password);
        appiumDriver.hideKeyboard();
        testReport(this.appiumDriver,true,"Nhập liệu đăng ký",true);
        btnSignUp.click();
        waitForLoading(2);
//        Helper apiHelper = new Helper();
//        try {
//            apiHelper.activeAccount(phone);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return new MainPage(appiumDriver);
    }
}