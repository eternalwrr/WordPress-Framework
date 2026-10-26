package com.lysenka.framework.tests;

import com.lysenka.framework.pages.DashboardPage;
import com.lysenka.framework.pages.LoginPage;
import com.lysenka.framework.util.ConfigReader;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

@Feature("Authorization")
public class LoginTest extends BaseTest {

    @Test(groups = {"smoke"})
    @Story("Authorizing works correctly")
    public void adminCanLogin() {
        DashboardPage dashboardPage = new LoginPage()
                .open()
                .login(
                        ConfigReader.getProperty("admin.username"),
                        ConfigReader.getProperty("admin.password")
                );
        assertTrue(
                dashboardPage.isOpened(),
                "Dashboard Page is not opened after login"
        );
    }
}
