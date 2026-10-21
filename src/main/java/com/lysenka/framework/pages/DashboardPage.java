package com.lysenka.framework.pages;

import com.lysenka.framework.webdriver.Browser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;

public class DashboardPage extends BasePage {

    private static final Logger logger =
            LogManager.getLogger(DashboardPage.class);

    private final By dashboardTitle = By.cssSelector(".wrap h1");

    public boolean isOpened() {
        boolean opened = Browser.findVisibleElement(dashboardTitle).isDisplayed();
        logger.debug("Dashboard opened: {}", opened);
        return opened;
    }

    public boolean isPostsMenuVisible() {
        boolean visible = getMainMenu().isPostsVisible();
        logger.debug("Posts menu visible: {}", visible);
        return visible;
    }

    public boolean isMediaMenuVisible() {
        boolean visible = getMainMenu().isMediaVisible();
        logger.debug("Media menu visible: {}", visible);
        return visible;
    }

    public boolean isPagesMenuVisible() {
        boolean visible = getMainMenu().isPagesVisible();
        logger.debug("Pages menu visible: {}", visible);
        return visible;
    }

    public boolean isCommentsMenuVisible() {
        boolean visible = getMainMenu().isCommentsVisible();
        logger.debug("Comments menu visible: {}", visible);
        return visible;
    }

    public boolean isUsersMenuVisible() {
        boolean visible = getMainMenu().isUsersVisible();
        logger.debug("Users menu visible: {}", visible);
        return visible;
    }

}