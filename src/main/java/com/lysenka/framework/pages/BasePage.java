package com.lysenka.framework.pages;

import com.lysenka.framework.elements.MainMenu;
import com.lysenka.framework.webdriver.Browser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public abstract class BasePage {

    private static final Logger logger = LogManager.getLogger(BasePage.class);

    private MainMenu mainMenu;

    protected MainMenu getMainMenu() {
        if (mainMenu == null) {
            logger.debug("Initializing MainMenu");
            mainMenu = new MainMenu();
        }
        return mainMenu;
    }

    protected WebElement findRow(By rowsLocator, String text) {
        logger.debug("Searching for row containing: {}", text);
        WebDriverWait wait = new WebDriverWait(
                Browser.getDriver(),
                Duration.ofSeconds(10)
        );
        WebElement row = wait.until(driver ->
                driver.findElements(rowsLocator)
                        .stream()
                        .filter(element -> element.getText().contains(text))
                        .findFirst()
                        .orElse(null)
        );
        logger.debug("Found row containing: {}", text);
        return row;
    }

}