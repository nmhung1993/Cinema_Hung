# Cài Đặt môi trường và tạo project

## Android
1. Tải và cài JDK, thêm biến môi trường [JAVA_HOME](https://www.tutorialspoint.com/maven/maven_environment_setup.htm)
2. Tải và cài Maven, thêm biến môi trường cho [maven](https://www.tutorialspoint.com/maven/maven_environment_setup.htm)
3. Tải and cài [Appium](https://github.com/appium/appium-desktop/releases)
4. Tải và cài [Android SKD](https://www.androidcentral.com/installing-android-sdk-windows-mac-and-linux-tutorial), thêm biến môi trường ANDROID_HOME cho [Window](https://www.360logica.com/blog/how-to-set-path-environmental-variable-for-sdk-in-windows/) hoặc [MacOS](https://github.com/bahattincinic/react-native-starter-kit/wiki/How-to-set-ANDROID_HOME-environment-variable-in-mac)
5. Mở IDE, tạo mới maven project
6. Thêm các dependencies bên dưới vào file pom.xml
```xml
    <dependency>
        <groupId>io.appium</groupId>
        <artifactId>java-client</artifactId>
        <version>5.0.4</version>
    </dependency>
    <dependency>
        <groupId>org.testng</groupId>
        <artifactId>testng</artifactId>
        <version>6.8</version>
    </dependency>
    <dependency>
        <groupId>com.relevantcodes</groupId>
        <artifactId>extentreports</artifactId>
        <version>2.41.2</version>
    </dependency>
```
7.  Mở commandline/terminal, gõ lênh "mvn clean install" để cài đặt các package cần thiết
8.  Mở appium và chạy appium server
9.  Kết nối điện thoại android vào máy tính
10. Mở terminal, đi đến thư mục chứa project, nhập lệnh :mvn test -DsuiteXmlFile=smoke.xml
12. Sau khi kết thúc, một file báo cáo dạng htlm sẽ được sinh ra và lưu trữ trong thư mục report