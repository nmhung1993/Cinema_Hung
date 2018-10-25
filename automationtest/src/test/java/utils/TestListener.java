package utils;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.NetworkMode;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener {
    private static ExtentReports extentReports;


    private static String getTestMethodName(ITestResult iTestResult) {
        return iTestResult.getMethod().getConstructorOrMethod().getName();
    }

    public void onStart(ITestContext iTestContext) {
        System.out.println("-----------Start tests-------------");

        // create html report
        //if (TestBase.extentTest == null) {
        if (TestReport.extentTest == null) {
            String workingDir = System.getProperty("user.dir");
            System.out.println("workingDir" + workingDir);
            extentReports = new ExtentReports(workingDir + "/report/AutomationTestReport.html", true, NetworkMode.OFFLINE);
            //reports = new ExtentReports(new SimpleDateFormat("dd-MM-yyyy_hh-mm-ss-ms_").format(new Date()) + "reports.html");
            //iTestContext.setAttribute("WebDriver", this.driver);
        }
    }


    public void onTestStart(ITestResult iTestResult) {
        System.out.println("-------------Start test method: " + iTestResult.getName().toUpperCase() +"-------------");
        TestReport.extentTest = extentReports.startTest(iTestResult.getName());
    }

    public void onFinish(ITestContext iTestContext) {
        System.out.println("-----------Finish test-----------");
        extentReports.endTest(TestReport.extentTest);
        extentReports.flush();

        // stop appium server
        //TestBase.appiumServer.stop();
    }

    public void onTestSuccess(ITestResult iTestResult) {
        System.out.println("Test succeed: " + iTestResult.getName());
    }

    public void onTestFailure(ITestResult iTestResult) {
        System.out.println("Test failed: " + iTestResult.getName());
    }

    public void onTestSkipped(ITestResult iTestResult) {
        System.out.println("Test skipped: " + iTestResult.getName());
    }

    public void onTestFailedButWithinSuccessPercentage(ITestResult iTestResult) {
        System.out.println("Test failed but it is in defined success ratio: " + iTestResult.getName());
    }
}



