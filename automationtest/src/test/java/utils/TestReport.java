package utils;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;


import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static org.testng.Assert.fail;
import static utils.Common.captureScreenshot;

public class TestReport {

    public static ExtentTest extentTest;

    public static void testReport(AppiumDriver appiumDriver, boolean isPassed, String passMessage, String failMessage, boolean isCaptureScreenshot) {
        String screeshotPath = "";
        if (isCaptureScreenshot) {
            screeshotPath = extentTest.addScreenCapture(captureScreenshot(appiumDriver));
            passMessage += screeshotPath;
            failMessage += screeshotPath;
        }

        if (isPassed) {
            extentTest.log(LogStatus.PASS, passMessage);
        } else {
            extentTest.log(LogStatus.FAIL, failMessage);
        }
    }

    public static void testReport(AppiumDriver appiumDriver, boolean isPassed, String message, boolean isCaptureScreenshot) {
        String screeshotPath = "";
        if (isCaptureScreenshot) {
            screeshotPath = extentTest.addScreenCapture(captureScreenshot(appiumDriver));
            message += screeshotPath;
        }

        if (isPassed) {
            extentTest.log(LogStatus.PASS, message);
        } else {
            extentTest.log(LogStatus.FAIL, message);
        }
    }

    public static void testReport(AppiumDriver appiumDriver, LogStatus status, String msgDetail, boolean isCaptureScreenshot) {
        String screeshotPath = "";
        if (isCaptureScreenshot) {
            screeshotPath = extentTest.addScreenCapture(captureScreenshot(appiumDriver));
            msgDetail += screeshotPath;
        }

        extentTest.log(status, msgDetail);
    }

    public static void handleExceptionAndMarkFailResult(AppiumDriver appiumDriver, Exception ex){
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String sStackTrace = sw.toString(); // stack trace as a string
        System.out.println(sStackTrace);

        testReport(appiumDriver, LogStatus.FAIL, ex.getMessage() + "<br>StackTrace: " + sStackTrace.replace("\n", "<br>"), true);
        System.out.println("Exception: " + ex.getMessage());
        fail();
    }
}
