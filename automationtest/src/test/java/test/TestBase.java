package test;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class TestBase {

    AndroidDriver androidDriver;
    DesiredCapabilities desiredCapabilities = new DesiredCapabilities();


    public static int testResult;
    public static ResourceBundle rb;
    public static int APPIUM_PORT = 4723;
    public static String APPIUM_URL = "127.0.0.1";
    public static List<Integer> selectedTicketsIndex;
    public static Double SHIPPING_FEE = 20000.00;


    public void getConfig(){
        rb = ResourceBundle.getBundle("config");
        APPIUM_PORT = Integer.parseInt(rb.getString("APPIUM_PORT"));
        APPIUM_URL = rb.getString("APPIUM_URL");
        // build appium capability
    }

    @BeforeMethod
    public void setUp() throws MalformedURLException {

        getConfig();

        desiredCapabilities.setCapability("deviceName", "MyAndroid");
        desiredCapabilities.setCapability("platformName", "Android");
        desiredCapabilities.setCapability("automationName", "appium");
        desiredCapabilities.setCapability("autoGrantPermissions", "true");
        desiredCapabilities.setCapability("autoAcceptAlerts", "true");
        desiredCapabilities.setCapability(AndroidMobileCapabilityType.APP_PACKAGE, "com.apitiny.administrator.hungcinema");
        //desiredCapabilities.setCapability(AndroidMobileCapabilityType.APP_ACTIVITY, ".activity.SplashScreenActivity");
        desiredCapabilities.setCapability(AndroidMobileCapabilityType.APP_ACTIVITY, ".activity.MainActivity");
        System.out.println("BeforeMethod");
        selectedTicketsIndex = new ArrayList<Integer>();
        testResult = 1;
        androidDriver = new AndroidDriver(new URL(APPIUM_URL + ":" + APPIUM_PORT + "/wd/hub"), desiredCapabilities);
    }

    @AfterMethod
    public void tearDown() {
        androidDriver.quit();
    }

//    private Promotion getApplicablePromotionForSelectedEvent(Event selectedEvent, User user) {
//        try {
//            Helper apiHelper = new Helper();
//            Promotion appliedPromotion = null;
//            List<Promotion> promotions = apiHelper.getUserPromotions(user.getId());
//
//            // no promotion
//            if (promotions.size() == 0)
//                return null;
//            else {
//                for (Promotion promotion : promotions) {
//                    // promotion used for specific event
//                    if(promotion.getUsingForEvent().equals(selectedEvent.getId())){
//                        appliedPromotion = promotion;
//                        break;
//                    }
//                    // promotion used for all events
//                    else if(promotion.getUsingForEvent().equals("")){
//                        appliedPromotion = promotion;
//                        break;
//                    }
//                }
//            }
//            return appliedPromotion;
//        } catch (Exception ex) {
//            return null;
//        }
//    }
//
//    public Double getUserPromotionAmountForThisEvent(Event selectedEvent, User user, Double ticketFee){
//        Promotion promotion = getApplicablePromotionForSelectedEvent(selectedEvent, user);
//
//        if (promotion != null) {
//            String value = "";
//            if (promotion.getIsPercent().equals("true")) {
//                value = promotion.getPercentValue();
//                value = value.replace("%", "");
//                return (Double.parseDouble(value) * ticketFee) / 100;
//            }
//            else {
//                value = promotion.getMoneyValue();
//                return Double.parseDouble(value);
//            }
//        } else return 0.00;
//    }

    public void sleep(int milisecond) {
        try {
            Thread.sleep(milisecond);
        } catch (Exception e) {
        }
    }
}