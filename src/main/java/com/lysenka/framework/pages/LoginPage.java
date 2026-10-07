package com.lysenka.framework.pages;

import com.lysenka.framework.util.ConfigReader;
import com.lysenka.framework.webdriver.Browser;
import org.openqa.selenium.By;

public class LoginPage {

    private final By userNameField = By.id("user_login");
    private final By passwordField = By.id("user_pass");
    private final By loginButton = By.id("wp-submit");

    public LoginPage open() {
        Browser.getDriver().get(ConfigReader.getProperty("baseUrl"));
        return this;
    }

    public LoginPage enterUserName(String userName) {
        Browser.findVisibleElement(userNameField).sendKeys(userName);
        return this;
    }

    public LoginPage enterPassword(String password) {
        Browser.findVisibleElement(passwordField).sendKeys(password);
        return this;
    }

    public DashboardPage clickLogin() {
        Browser.findVisibleElement(loginButton).click();
        return new DashboardPage();
    }

    public DashboardPage login(String username, String password) {
        enterUserName(username);
        enterPassword(password);
        return clickLogin();
    }
}
