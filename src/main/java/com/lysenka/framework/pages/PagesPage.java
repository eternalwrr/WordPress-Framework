package com.lysenka.framework.pages;

import com.lysenka.framework.util.ConfigReader;
import com.lysenka.framework.webdriver.Browser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class PagesPage extends BasePage {

    private static final Logger logger = LogManager.getLogger(PagesPage.class);

    private final By addNewPageButton = By.cssSelector("a.page-title-action");
    private final By pageEditor = By.cssSelector("#editor");
    private final By pageTitleField = By.cssSelector("h1[contenteditable='true']");
    private final By publishButton = By.cssSelector("button.editor-post-publish-button__button");
    private final By saveDraftButton = By.cssSelector("button.editor-post-save-draft");
    private final By closePatternModalButton = By.cssSelector("button[aria-label='Close']");
    private final By pageContentField = By.cssSelector("p[contenteditable='true'][data-title='Paragraph']");
    private final By defaultBlockAppender = By.cssSelector("p[aria-label='Add default block']");
    private final By contentField = By.cssSelector("p[contenteditable='true']");
    private final By confirmPublishButton = By.cssSelector(".editor-post-publish-panel__header-publish-button button");
    private final By pageRows = By.cssSelector("#the-list tr");
    private final By quickEditRow = By.cssSelector("#the-list tr.inline-edit-row");
    private final By statusSelect = By.name("_status");
    private final By quickEditUpdateButton = By.cssSelector(".inline-edit-save .button-primary");
    private final By trashButton = By.cssSelector("a.submitdelete");
    private final By quickEditButton = By.cssSelector("button.editinline, a.editinline");

    private WebElement findPageRow(String title) {
        logger.debug("Searching for page row");
        WebElement row = Browser.getDriver()
                .findElements(pageRows)
                .stream()
                .filter(element -> element.getText().contains(title))
                .findFirst()
                .orElseThrow(() -> {
                    logger.error("Page not found");
                    return new AssertionError("Page not found: " + title);
                });
        logger.debug("Page row found");
        return row;
    }

    public PagesPage open() {
        logger.info("Opening WordPress pages");
        getMainMenu().openPages();
        return this;
    }

    public PagesPage openNewPageForm() {
        logger.info("Opening new page form");
        Browser.findVisibleElement(addNewPageButton).click();
        return this;
    }

    public boolean isEditorOpened() {
        boolean opened = Browser.findVisibleElement(pageEditor).isDisplayed();
        logger.debug("Page editor opened: {}", opened);
        return opened;
    }

    public PagesPage enterTitle(String title) {
        logger.debug("Entering page title");
        WebDriver driver = Browser.getDriver();
        driver.switchTo().frame(0);
        WebElement titleField = Browser.findVisibleElement(pageTitleField);
        titleField.click();
        titleField.sendKeys(title);
        driver.switchTo().defaultContent();
        return this;
    }

    public PagesPage enterContent(String content) {
        logger.debug("Entering page content");
        WebDriver driver = Browser.getDriver();
        driver.switchTo().frame(0);
        Browser.findVisibleElement(defaultBlockAppender).click();
        WebElement contentField = Browser.findVisibleElement(pageContentField);
        contentField.sendKeys(content);
        driver.switchTo().defaultContent();
        return this;
    }

    public String getTitle() {
        WebDriver driver = Browser.getDriver();
        driver.switchTo().frame(0);
        String title = Browser.findVisibleElement(pageTitleField)
                .getText()
                .trim();
        driver.switchTo().defaultContent();
        logger.debug("Retrieved page title");
        return title;
    }

    public String getContent() {
        WebDriver driver = Browser.getDriver();
        driver.switchTo().frame(0);
        String content = Browser.findVisibleElement(contentField).getText();
        driver.switchTo().defaultContent();
        logger.debug("Retrieved page content");
        return content;
    }

    public PagesPage publish() {
        logger.info("Publishing page");
        Browser.findVisibleElement(publishButton).click();
        Browser.findVisibleElement(confirmPublishButton).click();
        logger.info("Page publication confirmation submitted");
        return this;
    }

    public PagesPage saveDraft() {
        logger.info("Saving page as draft");
        Browser.findVisibleElement(saveDraftButton).click();
        return this;
    }

    public PagesPage closePatternModal() {
        logger.debug("Closing page pattern modal");
        Browser.findVisibleElement(closePatternModalButton).click();
        return this;
    }

    public PagesPage changePageStatus(String title, String status) {
        logger.info("Changing page status to: {}", status);
        WebDriver driver = Browser.getDriver();
        WebDriverWait wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(15)
        );
        WebElement pageRow = wait.until(d ->
                d.findElements(pageRows)
                        .stream()
                        .filter(row -> row.getText().contains(title))
                        .findFirst()
                        .orElse(null)
        );
        ((org.openqa.selenium.JavascriptExecutor) driver)
                .executeScript(
                        "arguments[0].scrollIntoView({block: 'center'});",
                        pageRow
                );
        new Actions(driver).moveToElement(pageRow).perform();
        WebElement editButton = wait.until(
                org.openqa.selenium.support.ui.ExpectedConditions
                        .elementToBeClickable(pageRow.findElement(quickEditButton))
        );
        editButton.click();
        WebElement editRow = wait.until(
                org.openqa.selenium.support.ui.ExpectedConditions
                        .visibilityOfElementLocated(quickEditRow)
        );
        Select statusDropdown = new Select(editRow.findElement(statusSelect));
        statusDropdown.selectByValue(status);
        editRow.findElement(quickEditUpdateButton).click();
        wait.until(
                org.openqa.selenium.support.ui.ExpectedConditions
                        .invisibilityOfElementLocated(quickEditRow)
        );
        wait.until(d ->
                d.findElements(pageRows)
                        .stream()
                        .filter(row -> row.getText().contains(title))
                        .anyMatch(row -> {
                            String classes = row.getAttribute("class");
                            return classes != null
                                    && classes.contains("status-" + status);
                        })
        );
        logger.info("Page status changed successfully to: {}", status);
        return this;
    }

    public PagesPage openPagesList() {
        logger.debug("Opening pages list");
        String adminUrl = ConfigReader.getProperty("baseUrl");
        Browser.getDriver().get(adminUrl + "/edit.php?post_type=page");
        return this;
    }

    public String getPageStatus(String title) {
        logger.debug("Retrieving page status");
        WebDriver driver = Browser.getDriver();
        WebDriverWait wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(10)
        );
        String status = wait.until(d -> {
            WebElement pageRow = d.findElements(pageRows)
                    .stream()
                    .filter(row -> row.getText().contains(title))
                    .findFirst()
                    .orElse(null);
            if (pageRow == null) {
                return null;
            }
            String classes = pageRow.getAttribute("class");
            if (classes.contains("status-publish")
                    || classes.contains("status-draft")) {
                return classes;
            }
            return null;
        });
        logger.debug("Page status retrieved");
        return status;
    }

    public PagesPage movePageToTrash(String title) {
        logger.info("Moving page to trash");
        WebDriver driver = Browser.getDriver();
        WebElement pageRow = findPageRow(title);
        ((org.openqa.selenium.JavascriptExecutor) driver)
                .executeScript(
                        "arguments[0].scrollIntoView({block: 'center'});",
                        pageRow
                );
        new Actions(driver).moveToElement(pageRow).perform();
        WebElement trashLink = pageRow.findElement(
                By.cssSelector(".row-actions .trash a")
        );
        String trashUrl = trashLink.getAttribute("href");
        driver.get(trashUrl);
        logger.info("Page moved to trash");
        return this;
    }

    public PagesPage openTrash() {
        logger.debug("Opening pages trash");
        String adminUrl = ConfigReader.getProperty("baseUrl");
        Browser.getDriver().get(
                adminUrl
                        + "/edit.php?post_status=trash&post_type=page&orderby=date&order=desc"
        );
        return this;
    }

    public PagesPage deletePagePermanently(String title) {
        logger.info("Permanently deleting page");
        WebDriver driver = Browser.getDriver();
        WebDriverWait wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(10)
        );
        WebElement pageRow = wait.until(d ->
                d.findElements(pageRows)
                        .stream()
                        .filter(row -> row.getText().contains(title))
                        .findFirst()
                        .orElse(null)
        );
        new Actions(driver).moveToElement(pageRow).perform();
        WebElement deleteLink = wait.until(d ->
                pageRow.findElements(trashButton)
                        .stream()
                        .filter(WebElement::isDisplayed)
                        .findFirst()
                        .orElse(null)
        );
        deleteLink.click();
        logger.info("Permanent page deletion action submitted");
        return this;
    }

    public boolean isPagePresent(String title) {
        boolean present = Browser.getDriver()
                .findElements(pageRows)
                .stream()
                .anyMatch(row -> row.getText().contains(title));
        logger.debug("Page present in current list: {}", present);
        return present;
    }
}