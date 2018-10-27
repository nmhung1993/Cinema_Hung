package test;

import org.testng.annotations.Test;
import page.MainPage;
import page.PageSignin;
import page.PageUpload;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static utils.TestReport.handleExceptionAndMarkFailResult;

public class UploadTest extends TestBase {
    String randomCode = createRandomCode(5, "ABCDEFGHIKLMNOPQRSTUVWXYZ0123456789");
    public String createRandomCode(int codeLength, String id) {
        List<Character> temp = id.chars()
                .mapToObj(i -> (char)i)
                .collect(Collectors.toList());
        Collections.shuffle(temp, new SecureRandom());
        return temp.stream()
                .map(Object::toString)
                .limit(codeLength)
                .collect(Collectors.joining());
    }

    @Test
    public void testUpload() {
        try {
            // open Signin page
            MainPage mainPage = new MainPage(androidDriver);
            PageSignin pageSignin = mainPage.clickSignIn();
            // sign in account
            mainPage = pageSignin.signIn("test7@hung.com", "1111");
            // kiểm đăng nhập
            testResult *= mainPage.verifySigninSuccess();

            PageUpload pageUpload = mainPage.clickUpload();
            mainPage = pageUpload.UpLoad("Name "+randomCode,"Content "+randomCode);
            Thread.sleep(3000);

            testResult *= mainPage.verifyUploadSuccess("Name "+randomCode);

        } catch (Exception ex) {
            testResult *= 0;
            handleExceptionAndMarkFailResult(androidDriver, ex);
        }
    }
}
