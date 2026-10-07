package com.lysenka.framework.webdriver;

import com.lysenka.framework.util.ConfigReader;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

public class BrowserFactory {

    private static final boolean HEADLESS =
            ConfigReader.getProperty("headless", "false")
                    .equalsIgnoreCase("true");

    public static WebDriver createDriver(BrowserType browserType) {
        WebDriver driver = null;

        switch (browserType) {
            case CHROME -> {
                WebDriverManager.chromedriver().setup();
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
                driver = new ChromeDriver(options);
            }
            case FIREFOX -> {
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions options = new FirefoxOptions();
                options.addArguments("--no-sandbox");
                options.addArguments("--disable-dev-shm-usage");
                driver = new FirefoxDriver(options);
            }
        }
        return driver;
    }
}
