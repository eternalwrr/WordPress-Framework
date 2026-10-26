package com.lysenka.framework.tests;

import com.lysenka.framework.pages.DashboardPage;
import com.lysenka.framework.pages.LoginPage;
import com.lysenka.framework.util.ConfigReader;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Feature("Menu accessibility")
public class MenuAccessTest extends BaseTest {

    @DataProvider(name = "users")
    public Object[][] users() {
        return new Object[][]{
                {
                    ConfigReader.getProperty("admin.username"),
                    ConfigReader.getProperty("admin.password"),
                    true, true, true, true, true
                },
                {
                    ConfigReader.getProperty("editor.username"),
                    ConfigReader.getProperty("editor.password"),
                    true, true, true, true, false
                },
                {
                    ConfigReader.getProperty("author.username"),
                    ConfigReader.getProperty("author.password"),
                    true, true, false, true, false
                },
                {
                    ConfigReader.getProperty("contributor.username"),
                    ConfigReader.getProperty("contributor.password"),
                    true, false, false, true, false
                },
                {
                    ConfigReader.getProperty("subscriber.username"),
                    ConfigReader.getProperty("subscriber.password"),
                    false, false, false, false, false
                }
        };
    }

    @Test(
            dataProvider = "users",
            groups = {"menu"}
    )
    @Story("Menu accessibility for different roles of users")
    public void userShouldSeeAllowedMenuItems(
            String username,
            String password,
            boolean postsExpected,
            boolean mediaExpected,
            boolean pagesExpected,
            boolean commentsExpected,
            boolean usersExpected
    ) {
        DashboardPage dashboardPage = new LoginPage()
                .open()
                .login(username, password);
        assertTrue(dashboardPage.isOpened(), "Dashboard Page is not opened after login");
        assertEquals(
                dashboardPage.isPostsMenuVisible(),
                postsExpected, "Incorrect Posts menu visibility for user: " + username
        );
        assertEquals(
                dashboardPage.isMediaMenuVisible(),
                mediaExpected, "Incorrect Media menu visibility for user: " + username
        );
        assertEquals(
                dashboardPage.isPagesMenuVisible(),
                pagesExpected, "Incorrect Pages menu visibility for user: " + username
        );
        assertEquals(
                dashboardPage.isCommentsMenuVisible(),
                commentsExpected, "Incorrect Comments menu visibility for user: " + username
        );
        assertEquals(
                dashboardPage.isUsersMenuVisible(),
                usersExpected, "Incorrect Users menu visibility for user: " + username
        );
    }
}
