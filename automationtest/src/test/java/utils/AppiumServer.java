package utils;

import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;
import org.openqa.selenium.remote.DesiredCapabilities;
import test.TestBase;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;

public class AppiumServer {
    private AppiumDriverLocalService appiumDriverLocalService;
    private AppiumServiceBuilder appiumServiceBuilder;
    private DesiredCapabilities desiredCapabilities;

    public void start() {
        //Set server capabilities
        desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities.setCapability("noReset", "false");

        //Build the Appium service
        appiumServiceBuilder = new AppiumServiceBuilder();

        appiumDriverLocalService = AppiumDriverLocalService
                .buildService(new AppiumServiceBuilder()
                        .usingDriverExecutable(new File("/usr/local/bin/node"))
                        .withAppiumJS(new File("/usr/local/lib/node_modules/appium/build/lib/main.js"))
                        .withArgument(GeneralServerFlag.SESSION_OVERRIDE)
                        //.withArgument(GeneralServerFlag.LOG_LEVEL, "warn:error:info")
                        .withArgument(GeneralServerFlag.LOG_LEVEL, "error")
                        .withIPAddress(TestBase.APPIUM_URL).usingPort(TestBase.APPIUM_PORT));

        //Start the server
        //appiumDriverLocalService.start();
    }

    public void stop() {
        System.out.println("Stop appium server");
        //appiumDriverLocalService.stop();
    }

    public boolean checkIfServerIsRunnning(int port) {
        boolean isServerRunning = false;
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(port);
            serverSocket.close();
        } catch (IOException e) {
            //If control comes here, then it means that the port is in use
            isServerRunning = true;
        } finally {
            serverSocket = null;
        }
        return isServerRunning;
    }

    public String getServiceUrl() {
        return appiumDriverLocalService.getUrl().toString();
    }
}
