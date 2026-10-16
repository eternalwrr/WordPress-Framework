package com.lysenka.framework.tests;

import com.lysenka.framework.pages.LoginPage;
import com.lysenka.framework.util.ConfigReader;
import com.lysenka.framework.webdriver.Browser;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public abstract class BaseTest {
    @BeforeMethod
    public void setUp() {
        Browser.getDriver();
    }

    protected void loginAsAdmin() {
        new LoginPage()
                .open()
                .login(
                        ConfigReader.getProperty("admin.username"),
                        ConfigReader.getProperty("admin.password")
                );
    }


    @AfterMethod
    public void tearDown() {
        Browser.close();
    }
}
