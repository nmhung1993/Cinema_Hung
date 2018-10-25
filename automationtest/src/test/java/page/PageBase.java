package page;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.TouchAction;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.lang.reflect.Constructor;
import java.time.Duration;

//import net.sourceforge.tess4j.ITesseract;
//import net.sourceforge.tess4j.Tesseract;
//import net.sourceforge.tess4j.TesseractException;
//import net.sourceforge.tess4j.util.LoadLibs;

public abstract class PageBase {
    WebDriverWait wait;

    public enum PaymentMethod {
        CASH_ON_DELIVERY,
        AT_APITINY_OFFICE,
        BANK_TRANSFER,
        ONE_PAY
    }

    public enum ScrollingDirection {
        DOWN,
        UP,
        LEFT,
        RIGHT
    }


    public <T> T goToPreviousPage(Class<T> PrevPageClassName, AppiumDriver appiumDriver, WebElement backBtn) {
        try {
            backBtn.click();
            String className = PrevPageClassName.getName();
            Class<?> cls = Class.forName(className);

            Constructor constructor = cls.getDeclaredConstructor(AppiumDriver.class);//NextPageClassName.getClass());
            //return cls.getConstructor(NextPageClassName.getClass()).newInstance(driver);

            return (T) constructor.newInstance(appiumDriver);
            //return NextPageClassName.getDeclaredConstructor(Class.class).newInstance(driver);//newIntanceof ;
        } catch (Exception e) {
            System.out.println("goToPreviousPage() Err: " + e.getMessage());
            return null;
        }
    }

    public void waitForLoading(int second) {
        try {
            Thread.sleep(second * 1000);
        } catch (Exception ex) {
        }
    }

    public void scrollToElement(AppiumDriver appiumDriver, WebElement startElement, WebElement endElement) {
        System.out.println("scroll to element...");
        int x = startElement.getLocation().getX();
        System.out.println("startElement.getLocation().getX(): " + startElement.getLocation().getX());
        int y = startElement.getLocation().getY();
        System.out.println("startElement.getLocation().getY(): " + startElement.getLocation().getY());

        int x1 = endElement.getLocation().getX();
        System.out.println("endElement.getLocation().getX(): " + startElement.getLocation().getX());

        int y1 = endElement.getLocation().getY();
        System.out.println("endElement.getLocation().getY(): " + startElement.getLocation().getY());

        TouchAction touchAction = new TouchAction(appiumDriver);
        touchAction.press(startElement).moveTo(endElement).release().perform();
    }

    public void swipeVertical(AppiumDriver appiumDriver, double startPercentage, double finalPercentage, double anchorPercentage) throws Exception {
        Dimension size = appiumDriver.manage().window().getSize();
        int anchor = (int) (size.width * anchorPercentage);
        int startPoint = (int) (size.height * startPercentage);
        int endPoint = (int) (size.height * finalPercentage);

        System.out.println("size " + size);
        System.out.println("anchor " + anchor);
        System.out.println("startPoint " + startPoint);
        System.out.println("endPoint " + endPoint);
        new TouchAction(appiumDriver).press(anchor, startPoint).waitAction(Duration.ofMillis(1000)).moveTo(anchor, endPoint).release().perform();
    }

    public void scrolling(AppiumDriver appiumDriver, ScrollingDirection direction, double startPercentage, double finalPercentage, double anchorPercentage) throws Exception {
        Dimension size = appiumDriver.manage().window().getSize();
        int anchor = (int) (size.width * anchorPercentage);
        int startPoint = (int) (size.height * startPercentage);
        int endPoint = (int) (size.height * finalPercentage);

        System.out.println("size " + size);
        System.out.println("anchor " + anchor);
        System.out.println("startPoint " + startPoint);
        System.out.println("endPoint " + endPoint);
        new TouchAction(appiumDriver).press(anchor, startPoint).waitAction(Duration.ofMillis(1000)).moveTo(anchor, endPoint).release().perform();
    }

    public void scrolling(AppiumDriver appiumDriver) throws Exception {
        swipeVertical(appiumDriver, 0.5, 0.1, 0.5);
    }

    public void srollingDown2(AppiumDriver appiumDriver) throws Exception {
        (new TouchAction(appiumDriver)).press(306, 915).waitAction(Duration.ofMillis(500)).moveTo(4, 200).release().perform();
    }

    public void scrollingDown(AppiumDriver appiumDriver) {
        try {
            (new TouchAction(appiumDriver)).press(320, 1250).waitAction(Duration.ofMillis(2000)).moveTo(320, 200).release().perform();
        } catch (Exception ex) {

        }
    }

    public void scrollinDownInMyInterestedEvent(AppiumDriver appiumDriver){
        try {
            (new TouchAction(appiumDriver)).press(55, 1378).waitAction(Duration.ofMillis(2000)).moveTo( 55, 437).release().perform();
        } catch (Exception ex) {

        }
    }
//    public void srollingLeft(AppiumDriver appiumDriver) throws Exception {
//        (new TouchAction(appiumDriver)).press(864, 853).moveTo(-530, 18).release().perform();
//    }

    public void srollingLeft(AppiumDriver appiumDriver) {
        //(new TouchAction(appiumDriver)).press(800, 1200).waitAction(Duration.ofMillis(500)).moveTo(200, 1200).release().perform();
        try

        {
            (new TouchAction(appiumDriver)).press(800, 1200).waitAction(Duration.ofMillis(500)).moveTo(250, 1200).release().perform();
        } catch (Exception ex) {
        }
    }


}
