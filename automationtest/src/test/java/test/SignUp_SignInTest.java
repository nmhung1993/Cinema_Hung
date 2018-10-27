package test;

import org.testng.annotations.Test;
import page.MainPage;
import page.PageProfile;
import page.PageSignin;
import page.PageSignup;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static utils.TestReport.handleExceptionAndMarkFailResult;
import static utils.TestReport.testReport;

public class SignUp_SignInTest extends TestBase {

    String randomCode = createRandomCode(5, "0123456789");

    public String createRandomCode(int codeLength, String id) {
        List<Character> temp = id.chars()
                .mapToObj(i -> (char) i)
                .collect(Collectors.toList());
        Collections.shuffle(temp, new SecureRandom());
        return temp.stream()
                .map(Object::toString)
                .limit(codeLength)
                .collect(Collectors.joining());
    }

    String name = "hung AT " + randomCode, email = "hung" + randomCode + "@auto.test", pass = "1111";

    @Test
    public void testSignupSigninSuccess() {
        try {
            // open Signin page
            MainPage mainPage = new MainPage(androidDriver);
            PageSignin pageSignin = mainPage.clickSignIn();
            // open Signup page
            PageSignup pageSignup = pageSignin.clickSignup();
            // sign up account
            mainPage = pageSignup.SignUp(name, email, pass);
            // kiểm đăng nhập
            testResult *= mainPage.verifySigninSuccess();
            testReport(this.androidDriver,true,"Đăng ký thành công",true);
            PageProfile pageProfile = mainPage.clickProfile();
            mainPage = pageProfile.SignOut();
            Thread.sleep(3000);
            testResult *= mainPage.verifySignoutSuccess();

            // open Signin page
            mainPage = new MainPage(androidDriver);
            pageSignin = mainPage.clickSignIn();
            // sign in account
            mainPage = pageSignin.signIn(email, pass);
            // kiểm đăng nhập
            testResult *= mainPage.verifySigninSuccess();
            testReport(this.androidDriver,true,"Đăng nhập thành công",true);

            pageProfile = mainPage.clickProfile();
            mainPage = pageProfile.SignOut();
            Thread.sleep(3000);
            testResult *= mainPage.verifySignoutSuccess();
        } catch (Exception ex) {
            testResult *= 0;
            handleExceptionAndMarkFailResult(androidDriver, ex);
        }
    }

    /*@Test
    public void testSignin() {
        try {
            // open Signin page
            MainPage mainPage = new MainPage(androidDriver);
            PageSignin pageSignin = mainPage.clickSignIn();
            // sign in account
            mainPage = pageSignin.signIn(email, pass);
            // kiểm đăng nhập
            testResult *= mainPage.verifySigninSuccess();

            PageProfile pageProfile = mainPage.clickProfile();
            mainPage = pageProfile.SignOut();
            Thread.sleep(3000);
            testResult *= mainPage.verifySignoutSuccess();
        } catch (Exception ex) {
            testResult *= 0;
            handleExceptionAndMarkFailResult(androidDriver, ex);
        }
    }*/
}