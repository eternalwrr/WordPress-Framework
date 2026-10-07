package com.lysenka.framework.tests;

import com.lysenka.framework.webdriver.Browser;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public abstract class BaseTest {
    @BeforeMethod
    public void setUp() {
        Browser.getDriver();
    }

    @AfterMethod
    public void tearDown() {
        Browser.close();
    }
}
