package com.lysenka.framework.pages;

import com.lysenka.framework.util.ConfigReader;
import com.lysenka.framework.webdriver.Browser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class PagesPage extends BasePage {

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
    private final By quickEditButton = By.cssSelector("button.editinline, a.editinline");
    private final By statusSelect = By.name("_status");
    private final By quickEditUpdateButton = By.cssSelector(".inline-edit-save .button-primary");
    private final By trashButton = By.cssSelector("a.submitdelete");

    private WebElement findPageRow(String title) {
        return Browser.getDriver()
                .findElements(pageRows)
                .stream()
                .filter(row -> row.getText().contains(title))
                .findFirst()
                .orElseThrow(() ->
                        new AssertionError("Page not found: " + title));
    }

    private WebElement waitForPageRow(String title) {
        WebDriverWait wait = new WebDriverWait(
                Browser.getDriver(),
                Duration.ofSeconds(10)
        );
        return wait.until(driver ->
                driver.findElements(pageRows)
                        .stream()
                        .filter(row -> row.getText().contains(title))
                        .findFirst()
                        .orElse(null)
        );
    }

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
        WebElement titleField =
                Browser.findVisibleElement(pageTitleField);
        titleField.click();
        titleField.sendKeys(title);
        driver.switchTo().defaultContent();
        return this;
    }

    public PagesPage enterContent(String content) {
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
        String title = Browser.findVisibleElement(pageTitleField).getText().trim();
        driver.switchTo().defaultContent();
        return title;
    }

    public String getContent() {
        WebDriver driver = Browser.getDriver();
        driver.switchTo().frame(0);

        String content = Browser.findVisibleElement(contentField).getText();

        driver.switchTo().defaultContent();
        return content;
    }

    public PagesPage publish() {
        Browser.findVisibleElement(publishButton).click();
        Browser.findVisibleElement(confirmPublishButton).click();
        return this;
    }

    public PagesPage saveDraft() {
        Browser.findVisibleElement(saveDraftButton).click();
        return this;
    }

    public PagesPage closePatternModal() {
        Browser.findVisibleElement(closePatternModalButton).click();
        return this;
    }

    public PagesPage changePageStatus(String title, String status) {
        WebDriver driver = Browser.getDriver();
        WebElement pageRow = findPageRow(title);
        new Actions(driver).moveToElement(pageRow).perform();
        pageRow.findElement(quickEditButton).click();
        WebElement quickEditRow = driver.findElement(By.cssSelector("#the-list tr.inline-edit-row"));
        Select statusDropdown = new Select(quickEditRow.findElement(statusSelect));
        statusDropdown.selectByValue(status);
        quickEditRow.findElement(quickEditUpdateButton).click();
        return this;
    }

    public PagesPage openPagesList() {
        String adminUrl = ConfigReader.getProperty("baseUrl");
        Browser.getDriver().get(adminUrl + "/edit.php?post_type=page");
        return this;
    }

    public String getPageStatus(String title) {
        WebDriver driver = Browser.getDriver();

        WebDriverWait wait = new WebDriverWait(
                driver, Duration.ofSeconds(10)
        );

        return wait.until(d -> {
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
    }


    public PagesPage movePageToTrash(String title) {
        WebDriver driver = Browser.getDriver();
        WebElement pageRow = findPageRow(title);
        new Actions(driver)
                .moveToElement(pageRow)
                .perform();
        WebElement trashLink = pageRow.findElement(
                By.cssSelector(".row-actions .trash a")
        );
        String trashUrl = trashLink.getAttribute("href");
        driver.get(trashUrl);
        return this;
    }


    public PagesPage openTrash() {
        String adminUrl = ConfigReader.getProperty("baseUrl");
        Browser.getDriver().get(
                adminUrl + "/edit.php?post_status=trash&post_type=page&orderby=date&order=desc"
        );
        return this;
    }

    public PagesPage deletePagePermanently(String title) {
        WebDriver driver = Browser.getDriver();

        WebDriverWait wait = new WebDriverWait(
                driver, Duration.ofSeconds(10)
        );
        WebElement pageRow = wait.until(d ->
                d.findElements(pageRows)
                        .stream()
                        .filter(row -> row.getText().contains(title))
                        .findFirst()
                        .orElse(null)
        );
        new Actions(driver)
                .moveToElement(pageRow)
                .perform();
        WebElement deleteLink = wait.until(d ->
                pageRow.findElements(trashButton)
                        .stream()
                        .filter(WebElement::isDisplayed)
                        .findFirst()
                        .orElse(null)
        );
        deleteLink.click();
        return this;
    }

    public boolean isPagePresent(String title) {
        return Browser.getDriver()
                .findElements(pageRows)
                .stream()
                .anyMatch(row -> row.getText().contains(title));
    }
}
