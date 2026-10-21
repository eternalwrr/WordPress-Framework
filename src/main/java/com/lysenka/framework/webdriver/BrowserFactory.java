package com.lysenka.framework.webdriver;

import com.lysenka.framework.util.ConfigReader;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;

public class BrowserFactory {

    private static final Logger logger =
            LogManager.getLogger(BrowserFactory.class);

    private static final boolean HEADLESS =
            ConfigReader.getProperty("headless", "false")
                    .equalsIgnoreCase("true");

    public static WebDriver createDriver(BrowserType browserType) {
        String executionMode =
                ConfigReader.getProperty("executionMode", "local");

        logger.info(
                "Creating {} WebDriver in {} mode",
                browserType,
                executionMode
        );
        logger.debug("Headless mode: {}", HEADLESS);

        try {
            return switch (browserType) {
                case CHROME -> createChromeDriver(executionMode);
                case FIREFOX -> createFirefoxDriver(executionMode);
            };
        } catch (RuntimeException e) {
            logger.error(
                    "Failed to create {} WebDriver in {} mode",
                    browserType,
                    executionMode,
                    e
            );
            throw e;
        }
    }

    private static WebDriver createChromeDriver(String executionMode) {
        ChromeOptions options = new ChromeOptions();

        if (HEADLESS) {
            options.addArguments("--headless");
        }

        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--disable-notifications");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--disable-save-password-bubble");

        if (executionMode.equalsIgnoreCase("remote")) {
            logger.info("Starting remote Chrome WebDriver");
            return createRemoteDriver(options);
        }

        logger.debug("Setting up local ChromeDriver");
        WebDriverManager.chromedriver().setup();

        WebDriver driver = new ChromeDriver(options);
        logger.info("Local Chrome WebDriver created successfully");
        return driver;
    }

    private static WebDriver createFirefoxDriver(String executionMode) {
        FirefoxOptions options = new FirefoxOptions();

        if (HEADLESS) {
            options.addArguments("-headless");
        }

        if (executionMode.equalsIgnoreCase("remote")) {
            logger.info("Starting remote Firefox WebDriver");
            return createRemoteDriver(options);
        }

        logger.debug("Setting up local GeckoDriver");
        WebDriverManager.firefoxdriver().setup();

        WebDriver driver = new FirefoxDriver(options);
        logger.info("Local Firefox WebDriver created successfully");
        return driver;
    }

    private static WebDriver createRemoteDriver(Capabilities options) {
        String gridUrl = ConfigReader.getProperty(
                "gridUrl",
                "http://localhost:4444"
        );

        logger.info("Connecting to Selenium Grid: {}", gridUrl);

        try {
            WebDriver driver = new RemoteWebDriver(
                    new URL(gridUrl),
                    options
            );

            logger.info(
                    "Remote WebDriver session created successfully"
            );
            return driver;
        } catch (MalformedURLException e) {
            logger.error("Invalid Selenium Grid URL: {}", gridUrl, e);
            throw new IllegalArgumentException(
                    "Invalid Selenium Grid URL: " + gridUrl,
                    e
            );
        }
    }
}