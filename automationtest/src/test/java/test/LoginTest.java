package test;

import org.testng.annotations.Test;
import page.*;

public class LoginTest extends TestBase{
    @Test
    public void testLogin() {
        MainPage mainPage = new MainPage(androidDriver);
        mainPage.goToLoginPage();
    }
}
