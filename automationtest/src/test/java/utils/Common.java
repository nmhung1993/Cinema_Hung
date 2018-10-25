package utils;

import io.appium.java_client.AppiumDriver;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Common {

    public static String captureScreenshot(AppiumDriver appiumDriver) {
        String filePath = "";
        try {
            String workingDir = System.getProperty("user.dir");
            filePath = workingDir + "/report/" + generateUniqueString() + ".png";
            File scrFile = ((TakesScreenshot) appiumDriver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(scrFile, new File(filePath));
        } catch (Exception e) {

        } finally {
            return filePath;
        }
    }

    public static String captureScreenshotForVerifyingToastMsg(AppiumDriver appiumDriver) {
        String filePath = "";
        try {
            String workingDir = System.getProperty("user.dir");
            filePath = workingDir + "/report/toast-msg-checking/" + generateUniqueString() + ".png";
            File scrFile = ((TakesScreenshot) appiumDriver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(scrFile, new File(filePath));
        } catch (Exception e) {

        } finally {
            return filePath;
        }
    }

    public static String generateUniqueString() {
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(Calendar.getInstance().getTime());
        return timeStamp;
    }
}
