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

public final class Browser {

    private static final Logger logger = LogManager.getLogger(Browser.class);

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private Browser() {
    }

    public static WebDriver getDriver() {
        WebDriver driver = DRIVER.get();

        if (driver == null) {
            var browserType = com.lysenka.framework.webdriver.BrowserType
                    .valueOf(
                            ConfigReader.getProperty(
                                    "browserType", "chrome"
                            ).toUpperCase()
                    );

            logger.info("Initializing browser: {}", browserType);
            driver = BrowserFactory.createDriver(browserType);
            driver.manage().timeouts().implicitlyWait(Duration.ZERO);
            DRIVER.set(driver);

            logger.info("Browser started: {}", browserType);
        }
        return driver;
    }

    public static void open(String url) {
        logger.info("Opening URL: {}", url);
        getDriver().get(url);
    }

    public static boolean isElementDisplayed(By locator) {
        try {
            return getDriver().findElement(locator).isDisplayed();
        } catch (NoSuchElementException e) {
            logger.debug("Element not found: {}", locator);
            return false;
        }
    }

    public static WebElement findVisibleElement(By locator) {
        int timeout = Integer.parseInt(
                ConfigReader.getProperty("explicit.wait", "15")
        );
        WebDriverWait wait = new WebDriverWait(
                getDriver(),
                Duration.ofSeconds(timeout)
        );
        return wait.until(
                ExpectedConditions.visibilityOfElementLocated(locator)
        );
    }

    public static boolean waitUntilElementDisappears(By locator) {
        int timeout = Integer.parseInt(
                ConfigReader.getProperty("explicit.wait", "15")
        );

        WebDriverWait wait = new WebDriverWait(
                getDriver(),
                Duration.ofSeconds(timeout)
        );

        return wait.until(
                ExpectedConditions.invisibilityOfElementLocated(locator)
        );
    }

    public static void close() {
        WebDriver driver = DRIVER.get();

        try {
            if (driver != null) {
                logger.info("Closing browser");
                driver.quit();
                logger.info("Browser closed successfully");
            }
        } catch (RuntimeException e) {
            logger.error("Failed to close browser", e);
            throw e;
        } finally {
            DRIVER.remove();
        }
    }

    public static boolean isRemote() {
        return "remote".equalsIgnoreCase(
                ConfigReader.getProperty("executionMode", "local")
        );
    }
}
