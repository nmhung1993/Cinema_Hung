package page;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static utils.TestReport.testReport;

public class PageUpload extends PageBase {
    AppiumDriver appiumDriver;
    private WebDriverWait wait;

    @FindBy(id = "btnChonAnh") WebElement btnChonAnh;
    @FindBy(id = "btDialogNo") WebElement btDialogNo;
    @FindBy(id = "btDialogYes") WebElement btDialogYes;
    @FindBy(id = "shutter_button") WebElement shutter_button;
    @FindBy(id = "retake_button") WebElement retake_button;
    @FindBy(id = "camera_switch_button") WebElement camera_switch_button;


    @FindBy(id = "name_ed") WebElement name_ed;

    @FindBy(id = "genre_sp") WebElement genre_sp;

    @FindBy(id = "releaseDate_ed") WebElement releaseDate_ed;

    @FindBy(id = "content_ed") WebElement content_ed;

    @FindBy(id = "btnUp") WebElement btnUp;

    public PageUpload(AppiumDriver appiumDriver){
        this.appiumDriver = appiumDriver;
        PageFactory.initElements(appiumDriver, this);
        this.wait = new WebDriverWait(appiumDriver, 30);
    }

    public MainPage UpLoad(String name, String content){
        System.out.println("--- Enter upload function ---");
        btnChonAnh.click();
        wait.until(ExpectedConditions.elementToBeClickable(By.id("btDialogNo")));
        btDialogNo.click();
        waitForLoading(2);
        WebElement takephoto = appiumDriver.findElement(By.xpath("//android.widget.ImageButton[@content-desc=\"Shutter\"]"));
        takephoto.click();
        waitForLoading(2);
        WebElement savephoto = appiumDriver.findElement(By.xpath("//android.widget.ImageButton[@content-desc=\"Done\"]"));
        savephoto.click();
        waitForLoading(2);
        name_ed.sendKeys(name);
        appiumDriver.hideKeyboard();
        genre_sp.click();
        waitForLoading(2);
        WebElement spGenre = appiumDriver.findElement(By.xpath("/hierarchy/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.ListView/android.widget.TextView[3]"));
        spGenre.click();
        waitForLoading(2);
        releaseDate_ed.click();
        waitForLoading(2);
        WebElement datePicker = appiumDriver.findElement(By.xpath("//android.view.View[@content-desc=\"10 October 2018\"]"));
        datePicker.click();
        waitForLoading(2);
        WebElement okBtn = appiumDriver.findElement(By.xpath("/hierarchy/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.ScrollView/android.widget.LinearLayout/android.widget.Button[2]"));
        okBtn.click();
        content_ed.sendKeys(content);
        appiumDriver.hideKeyboard();
        testReport(appiumDriver,true,"Before click Upload Button",true);
        btnUp.click();
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
