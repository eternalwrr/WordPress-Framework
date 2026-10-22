package com.lysenka.framework.pages;

import com.lysenka.framework.util.ConfigReader;
import com.lysenka.framework.webdriver.Browser;
import io.qameta.allure.Step;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;

public class LoginPage {

    private static final Logger logger = LogManager.getLogger(LoginPage.class);

    private final By userNameField = By.id("user_login");
    private final By passwordField = By.id("user_pass");
    private final By loginButton = By.id("wp-submit");

    @Step("Open WordPress login page")
    public LoginPage open() {
        String baseUrl = ConfigReader.getProperty("baseUrl");
        logger.info("Opening WordPress login page: {}", baseUrl);
        Browser.getDriver().get(baseUrl);
        return this;
    }

    @Step("Enter username")
    public LoginPage enterUserName(String userName) {
        logger.debug("Entering username");
        Browser.findVisibleElement(userNameField).sendKeys(userName);
        return this;
    }

    @Step("Enter password")
    public LoginPage enterPassword(String password) {
        logger.debug("Entering password");
        Browser.findVisibleElement(passwordField).sendKeys(password);
        return this;
    }

    @Step("Submit login form")
    public DashboardPage clickLogin() {
        logger.info("Submitting WordPress login form");
        Browser.findVisibleElement(loginButton).click();
        return new DashboardPage();
    }

    @Step("Log in to WordPress")
    public DashboardPage login(String username, String password) {
        logger.info("Starting WordPress login flow");
        enterUserName(username);
        enterPassword(password);
        return clickLogin();
    }
}
