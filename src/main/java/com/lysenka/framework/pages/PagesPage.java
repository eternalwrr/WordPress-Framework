package com.lysenka.framework.pages;

import com.lysenka.framework.webdriver.Browser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class PagesPage extends BasePage {

    private final By addNewPageButton = By.cssSelector("a.page-title-action");
    private final By pageEditor = By.cssSelector("#editor");
    private final By pageTitleField = By.cssSelector("h1[contenteditable='true'][aria-label='Add title']");
    private final By publishButton = By.cssSelector(".editor-post-publish-button");
    private final By saveDraftButton = By.cssSelector("button.editor-post-save-draft");
    private final By closePatternModalButton = By.cssSelector("button[aria-label='Close']");

    public PagesPage open() {
        getMainMenu().openPages();
        return this;
    }

    public PagesPage openNewPageForm() {
        Browser.findVisibleElement(addNewPageButton).click();
        return this;
    }

    public boolean isEditorOpened() {
        return Browser.findVisibleElement(pageEditor).isDisplayed();
    }

    public PagesPage enterTitle(String title) {
        WebDriver driver = Browser.getDriver();

        driver.switchTo().frame(0);

        WebElement titleField = Browser.findVisibleElement(
                By.cssSelector("h1[contenteditable='true']")
        );

        titleField.click();
        titleField.sendKeys(title);

        driver.switchTo().defaultContent();

        return this;
    }

    public String getTitle() {
        WebDriver driver = Browser.getDriver();

        driver.switchTo().frame(0);

        String title = Browser.findVisibleElement(
                By.cssSelector("h1[contenteditable='true']")
        ).getText().trim();

        driver.switchTo().defaultContent();

        return title;
    }

    public PagesPage saveDraft() {
        Browser.findVisibleElement(saveDraftButton).click();
        return this;
    }

    public PagesPage closePatternModal() {
        Browser.findVisibleElement(closePatternModalButton).click();
        return this;
    }
}
