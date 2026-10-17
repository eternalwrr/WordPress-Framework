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

public class PostsPage extends BasePage {

    private final By addNewPostButton = By.cssSelector("a.page-title-action");
    private final By postEditor = By.cssSelector("#editor");
    private final By postTitleField = By.cssSelector("h1[contenteditable='true']");
    private final By defaultBlockAppender = By.cssSelector("p[aria-label='Add default block']");
    private final By postContentField = By.cssSelector("p[contenteditable='true'][data-title='Paragraph']");
    private final By contentField = By.cssSelector("p[contenteditable='true']");
    private final By publishButton = By.cssSelector("button.editor-post-publish-button__button");
    private final By saveDraftButton = By.cssSelector("button.editor-post-save-draft");
    private final By confirmPublishButton = By.cssSelector(".editor-post-publish-panel__header-publish-button button");
    private final By postRows = By.cssSelector("#the-list tr");
    private final By quickEditButton = By.cssSelector("button.editinline, a.editinline");
    private final By statusSelect = By.name("_status");
    private final By quickEditUpdateButton = By.cssSelector(".inline-edit-save .button-primary");
    private final By trashButton = By.cssSelector("a.submitdelete");

    public PostsPage open() {
        getMainMenu().openPosts();
        return this;
    }

    public PostsPage openNewPostForm() {
        Browser.findVisibleElement(addNewPostButton).click();
        return this;
    }

    public boolean isEditorOpened() {
        return Browser.findVisibleElement(postEditor).isDisplayed();
    }

    public PostsPage enterTitle(String title) {
        WebDriver driver = Browser.getDriver();
        driver.switchTo().frame(0);
        WebElement titleField = Browser.findVisibleElement(postTitleField);
        titleField.click();
        titleField.sendKeys(title);
        driver.switchTo().defaultContent();
        return this;
    }

    public PostsPage enterContent(String content) {
        WebDriver driver = Browser.getDriver();
        driver.switchTo().frame(0);
        Browser.findVisibleElement(defaultBlockAppender).click();
        WebElement contentField = Browser.findVisibleElement(postContentField);
        contentField.sendKeys(content);
        driver.switchTo().defaultContent();
        return this;
    }

    public String getTitle() {
        WebDriver driver = Browser.getDriver();
        driver.switchTo().frame(0);
        String title = Browser.findVisibleElement(postTitleField).getText().trim();
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

    public PostsPage publish() {
        Browser.findVisibleElement(publishButton).click();
        Browser.findVisibleElement(confirmPublishButton).click();
        return this;
    }

    public PostsPage saveDraft() {
        Browser.findVisibleElement(saveDraftButton).click();
        return this;
    }

    public PostsPage openPostsList() {
        String adminUrl = ConfigReader.getProperty("baseUrl");
        Browser.getDriver().get(adminUrl + "/edit.php?post_type=post");
        return this;
    }

    public PostsPage changePostStatus(String title, String status) {
        WebDriver driver = Browser.getDriver();

        WebElement postRow = Browser.getDriver()
                .findElements(postRows)
                .stream()
                .filter(row -> row.getText().contains(title))
                .findFirst()
                .orElseThrow(() ->
                        new AssertionError("Post not found: " + title));
        new Actions(driver).moveToElement(postRow).perform();
        postRow.findElement(quickEditButton).click();
        WebElement quickEditRow = driver.findElement(By.cssSelector("#the-list tr.inline-edit-row"));
        Select statusDropdown = new Select(quickEditRow.findElement(statusSelect));
        statusDropdown.selectByValue(status);
        quickEditRow.findElement(quickEditUpdateButton).click();
        return this;
    }

    public String getPostStatus(String title) {
        WebDriver driver = Browser.getDriver();
        WebDriverWait wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(10)
        );
        return wait.until(d -> {
            WebElement postRow = d.findElements(postRows)
                    .stream()
                    .filter(row -> row.getText().contains(title))
                    .findFirst()
                    .orElse(null);
            if (postRow == null) {
                return null;
            }
            String classes = postRow.getAttribute("class");
            if (classes.contains("status-publish")
                    || classes.contains("status-draft")) {
                return classes;
            }
            return null;
        });
    }

    public PostsPage movePostToTrash(String title) {
        WebDriver driver = Browser.getDriver();
        WebElement postRow = driver.findElements(postRows)
                .stream()
                .filter(row -> row.getText().contains(title))
                .findFirst()
                .orElseThrow(() ->
                        new AssertionError("Post not found: " + title));
        new Actions(driver).moveToElement(postRow).perform();
        WebElement trashLink = postRow.findElement(
                By.cssSelector(".row-actions .trash a")
        );
        String trashUrl = trashLink.getAttribute("href");
        driver.get(trashUrl);
        return this;
    }

    public PostsPage openTrash() {
        String adminUrl = ConfigReader.getProperty("baseUrl");
        Browser.getDriver().get(
                adminUrl
                        + "/edit.php?post_status=trash&post_type=post&orderby=date&order=desc"
        );
        return this;
    }

    public PostsPage deletePostPermanently(String title) {
        WebDriver driver = Browser.getDriver();
        WebDriverWait wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(10)
        );
        WebElement postRow = wait.until(d ->
                d.findElements(postRows)
                        .stream()
                        .filter(row -> row.getText().contains(title))
                        .findFirst()
                        .orElse(null)
        );
        new Actions(driver).moveToElement(postRow).perform();
        WebElement deleteLink = wait.until(d ->
                postRow.findElements(trashButton)
                        .stream()
                        .filter(WebElement::isDisplayed)
                        .findFirst()
                        .orElse(null)
        );
        deleteLink.click();
        return this;
    }

    public boolean isPostPresent(String title) {
        return Browser.getDriver()
                .findElements(postRows)
                .stream()
                .anyMatch(row -> row.getText().contains(title));
    }
}
