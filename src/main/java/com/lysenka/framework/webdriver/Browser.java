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

    private Browser() {
    }

    public static boolean isElementDisplayed(By locator) {
        try {
            return getDriver().findElement(locator).isDisplayed();
        } catch (NoSuchElementException e) {
            logger.debug("Element not found: {}", locator);
            return false;
        }
    }

    public static WebDriver getDriver() {
        if (driver == null) {
            BrowserType browserType = BrowserType.valueOf(
                    ConfigReader.getProperty("browserType", "chrome")
                            .toUpperCase()
            );
            logger.debug("Initializing browser: {}", browserType);
            driver = BrowserFactory.createDriver(browserType);
            driver.manage().timeouts().implicitlyWait(Duration.ZERO);
            logger.info("Browser started: {}", browserType);
        }
        return driver;
    }

    public static void open(String url) {
        logger.info("Opening URL: {}", url);

        try {
            getDriver().get(url);
            logger.debug("URL opened successfully: {}", url);
        } catch (RuntimeException e) {
            logger.error("Failed to open URL: {}", url, e);
            throw e;
        }
    }

    public static WebElement findVisibleElement(By locator) {
        int timeout = Integer.parseInt(
                ConfigReader.getProperty("explicit.wait", "15")
        );
        logger.debug(
                "Waiting up to {} seconds for visible element: {}",
                timeout,
                locator
        );
        WebDriverWait wait = new WebDriverWait(
                getDriver(),
                Duration.ofSeconds(timeout)
        );

        try {
            WebElement element = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(locator)
            );
            logger.debug("Element is visible: {}", locator);
            return element;
        } catch (RuntimeException e) {
            logger.error("Element did not become visible: {}", locator, e);
            throw e;
        }
    }

    public static void close() {
        if (driver != null) {
            logger.info("Closing browser");

            try {
                driver.quit();
                logger.info("Browser closed successfully");
            } catch (RuntimeException e) {
                logger.error("Failed to close browser cleanly", e);
                throw e;
            } finally {
                driver = null;
            }
        } else {
            logger.debug("Browser is already closed");
        }
    }

    public static boolean waitUntilElementDisappears(By locator) {
        int timeout = Integer.parseInt(
                ConfigReader.getProperty("explicit.wait", "15")
        );
        logger.debug(
                "Waiting up to {} seconds for element to disappear: {}",
                timeout,
                locator
        );
        WebDriverWait wait = new WebDriverWait(
                getDriver(),
                Duration.ofSeconds(timeout)
        );
        boolean disappeared = wait.until(
                ExpectedConditions.invisibilityOfElementLocated(locator)
        );
        logger.debug(
                "Element disappearance result for {}: {}",
                locator,
                disappeared
        );
        return disappeared;
    }

    public static boolean isRemote() {
        return "remote".equalsIgnoreCase(
                ConfigReader.getProperty("executionMode", "local")
        );
    }
}