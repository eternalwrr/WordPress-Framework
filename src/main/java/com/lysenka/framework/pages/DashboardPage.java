package com.lysenka.framework.pages;

import com.lysenka.framework.webdriver.Browser;
import org.openqa.selenium.By;

public class DashboardPage extends BasePage {

    private final By dashboardTitle = By.cssSelector(".wrap h1");

    public boolean isOpened() {
        return Browser.findVisibleElement(dashboardTitle).isDisplayed();
    }

    public boolean isPostsMenuVisible() {
        return getMainMenu().isPostsVisible();
    }

    public boolean isMediaMenuVisible() {
        return getMainMenu().isMediaVisible();
    }

    public boolean isPagesMenuVisible() {
        return getMainMenu().isPagesVisible();
    }

    public boolean isCommentsMenuVisible() {
        return getMainMenu().isCommentsVisible();
    }

    public boolean isUsersMenuVisible() {
        return getMainMenu().isUsersVisible();
    }
}
