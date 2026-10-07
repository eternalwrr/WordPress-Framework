package com.lysenka.framework.tests;

import com.lysenka.framework.pages.DashboardPage;
import com.lysenka.framework.pages.LoginPage;
import com.lysenka.framework.util.ConfigReader;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;

public class LoginTest extends BaseTest {

    @Test
    public void adminCanLogin() {
        DashboardPage dashboardPage = new LoginPage()
                .open()
                .login(
                        ConfigReader.getProperty("admin.username"),
                        ConfigReader.getProperty("admin.password")
                );
        assertNotNull(dashboardPage);
    }
}
