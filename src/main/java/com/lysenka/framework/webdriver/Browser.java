package com.lysenka.framework.webdriver;


import com.lysenka.framework.util.ConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class Browser {

    private static final Logger logger = LogManager.getLogger(Browser.class);
    private static WebDriver driver;

    public static boolean isElementDisplayed(By locator) {
        try {
            return getDriver().findElement(locator).isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    private Browser() {
    }

    public static WebDriver getDriver() {
        if (driver == null) {
            BrowserType browserType = BrowserType.valueOf(
                    ConfigReader.getProperty("browserType", "chrome").toUpperCase()
            );
            driver = BrowserFactory.createDriver(browserType);
            int implicitWait = Integer.parseInt(
                    ConfigReader.getProperty("implicit.wait", "10"));
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWait));
            logger.info("Browser started: {}", browserType);
        }
        return driver;
    }

    public static void open(String url) {
        getDriver().get(url);
        logger.info("Browser opened: {}", url);
    }

    public static WebElement findVisibleElement(By locator) {
        int timeout = Integer.parseInt(
                ConfigReader.getProperty("explicit.wait", "15")
        );
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(timeout));
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static void close() {
        if (driver != null) {
            driver.quit();
            driver = null;
            logger.info("Browser closed");
        }
    }

    public static boolean waitUntilElementDisappears(By locator) {
        int timeout = Integer.parseInt(
                ConfigReader.getProperty("explicit.wait", "15")
        );
        WebDriverWait wait = new WebDriverWait(
                getDriver(),
                Duration.ofSeconds(timeout)
        );
        return wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }
}
