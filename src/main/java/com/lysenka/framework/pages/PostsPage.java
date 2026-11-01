package com.lysenka.framework.pages;

import com.lysenka.framework.util.ConfigReader;
import com.lysenka.framework.webdriver.Browser;
import io.qameta.allure.Step;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class PostsPage extends BasePage {

    private static final Logger logger = LogManager.getLogger(PostsPage.class);

    private final By addNewPostButton = By.cssSelector("a.page-title-action");
    private final By postRows = By.cssSelector("#the-list tr");
    private final By postEditor = By.cssSelector("#editor");
    private final By editorFrame = By.cssSelector("iframe[title='Editor canvas']");
    private final By editorTitle = By.cssSelector("h1[contenteditable='true']");
    private final By editorContent = By.cssSelector("p.block-editor-rich-text__editable[contenteditable='true']");
    private final By publishButton = By.cssSelector("button.editor-post-publish-button__button");
    private final By saveDraftButton = By.cssSelector("button.editor-post-save-draft");
    private final By confirmPublishButton = By.cssSelector(".editor-post-publish-panel__header-publish-button button");
    private final By quickEditButton = By.cssSelector("button.editinline, a.editinline");
    private final By quickEditRow = By.cssSelector("#the-list tr.inline-edit-row");
    private final By statusSelect = By.name("_status");
    private final By quickEditUpdateButton = By.cssSelector(".inline-edit-save .button-primary");
    private final By trashPostLink = By.cssSelector(".row-actions .trash a");
    private final By deletePostButton = By.cssSelector("a.submitdelete");
    private final By viewPostLink = By.cssSelector(".row-actions .view a");
    private final By savedButton = By.xpath(
                    "//button[.//span[normalize-space()='Saved'] "
                            + "or normalize-space()='Saved']");

    @Step("Open WordPress posts")
    public PostsPage open() {
        logger.info("Opening WordPress posts");
        getMainMenu().openPosts();
        return this;
    }

    @Step("Open new post form")
    public PostsPage openNewPostForm() {
        logger.info("Opening new post form");
        Browser.findVisibleElement(addNewPostButton).click();
        return this;
    }

    @Step("Check that post editor is opened")
    public boolean isEditorOpened() {
        boolean opened = Browser.findVisibleElement(postEditor).isDisplayed();
        logger.debug("Post editor opened: {}", opened);
        return opened;
    }

    @Step("Enter post title: {title}")
    public PostsPage enterTitle(String title) {
        logger.debug("Entering post title");
        WebDriver driver = Browser.getDriver();
        WebDriverWait wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(20)
        );
        driver.switchTo().defaultContent();
        WebElement frame = wait.until(
                ExpectedConditions.presenceOfElementLocated(editorFrame)
        );
        driver.switchTo().frame(frame);
        WebElement titleField = wait.until(
                ExpectedConditions.elementToBeClickable(editorTitle)
        );
        titleField.click();
        titleField.sendKeys(title);
        driver.switchTo().defaultContent();
        return this;
    }

    @Step("Enter post content")
    public PostsPage enterContent(String content) {
        logger.debug("Entering post content");
        WebDriver driver = Browser.getDriver();
        WebDriverWait wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(20)
        );
        driver.switchTo().defaultContent();
        WebElement frame = wait.until(
                ExpectedConditions.presenceOfElementLocated(editorFrame)
        );
        driver.switchTo().frame(frame);
        WebElement titleField = wait.until(
                ExpectedConditions.visibilityOfElementLocated(editorTitle)
        );
        titleField.sendKeys(Keys.TAB);
        WebElement contentField = wait.until(
                ExpectedConditions.visibilityOfElementLocated(editorContent)
        );
        contentField.sendKeys(content);
        wait.until(d -> !contentField.getText().trim().isEmpty());
        driver.switchTo().defaultContent();
        return this;
    }

    @Step("Get post title")
    public String getTitle() {
        WebDriver driver = Browser.getDriver();
        WebDriverWait wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(15)
        );
        try {
            driver.switchTo().defaultContent();
            WebElement frame = wait.until(
                    ExpectedConditions.presenceOfElementLocated(editorFrame)
            );
            driver.switchTo().frame(frame);
            String title = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(editorTitle)
            ).getText().trim();

            logger.debug("Retrieved post title");
            return title;
        } finally {
            driver.switchTo().defaultContent();
        }
    }

    @Step("Get post content")
    public String getContent() {
        WebDriver driver = Browser.getDriver();
        WebDriverWait wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(15)
        );
        try {
            driver.switchTo().defaultContent();
            WebElement frame = wait.until(
                    ExpectedConditions.presenceOfElementLocated(editorFrame)
            );
            driver.switchTo().frame(frame);
            String content = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(editorContent)
            ).getText().trim();
            logger.debug("Retrieved post content");
            return content;
        } finally {
            driver.switchTo().defaultContent();
        }
    }

    @Step("Publish post")
    public PostsPage publish() {
        logger.info("Publishing post");
        WebDriverWait wait = new WebDriverWait(
                Browser.getDriver(),
                Duration.ofSeconds(20)
        );
        wait.until(
                ExpectedConditions.elementToBeClickable(publishButton)
        ).click();
        wait.until(
                ExpectedConditions.elementToBeClickable(confirmPublishButton)
        ).click();
        wait.until(
                ExpectedConditions.invisibilityOfElementLocated(
                        confirmPublishButton
                )
        );
        logger.info("Post publication confirmation submitted");
        return this;
    }

    @Step("Save post as draft")
    public PostsPage saveDraft() {
        logger.info("Saving post as draft");
        WebDriverWait wait = new WebDriverWait(
                Browser.getDriver(),
                Duration.ofSeconds(20)
        );
        wait.until(
                ExpectedConditions.elementToBeClickable(saveDraftButton)
        ).click();
        wait.until(
                ExpectedConditions.visibilityOfElementLocated(savedButton)
        );
        logger.info("Post saved as draft");
        return this;
    }

    @Step("Open posts list")
    public PostsPage openPostsList() {
        logger.debug("Opening posts list");
        String adminUrl = ConfigReader.getProperty("baseUrl")
                .replaceAll("/+$", "");
        if (adminUrl.endsWith("/wp-admin")) {
            adminUrl = adminUrl.substring(
                    0,
                    adminUrl.length() - "/wp-admin".length()
            );
        }
        Browser.getDriver().get(
                adminUrl + "/wp-admin/edit.php?post_type=post"
        );
        return this;
    }

    @Step("Change post '{title}' status to '{status}'")
    public PostsPage changePostStatus(String title, String status) {
        logger.info("Changing post status to: {}", status);
        WebDriver driver = Browser.getDriver();
        WebDriverWait wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(10)
        );
        WebElement postRow = findRow(postRows, title);
        new Actions(driver).moveToElement(postRow).perform();
        WebElement editButton = wait.until(
                ExpectedConditions.elementToBeClickable(
                        postRow.findElement(quickEditButton)
                )
        );
        editButton.click();
        WebElement editRow = wait.until(
                ExpectedConditions.visibilityOfElementLocated(quickEditRow)
        );
        Select statusDropdown = new Select(editRow.findElement(statusSelect));
        statusDropdown.selectByValue(status);
        editRow.findElement(quickEditUpdateButton).click();
        wait.until(
                ExpectedConditions.invisibilityOfElementLocated(quickEditRow)
        );
        wait.until(driver1 ->
                driver1.findElements(postRows)
                        .stream()
                        .filter(row -> row.getText().contains(title))
                        .anyMatch(row -> {
                            String classes = row.getAttribute("class");
                            return classes != null
                                    && classes.contains("status-" + status);
                        })
        );
        logger.info("Post status changed successfully to: {}", status);
        return this;
    }

    @Step("Get status of post '{title}'")
    public String getPostStatus(String title) {
        logger.debug("Retrieving post status");
        WebDriverWait wait = new WebDriverWait(
                Browser.getDriver(),
                Duration.ofSeconds(10)
        );
        String status = wait.until(driver -> {
            WebElement postRow = driver.findElements(postRows)
                    .stream()
                    .filter(row -> row.getText().contains(title))
                    .findFirst()
                    .orElse(null);
            if (postRow == null) {
                return null;
            }
            String classes = postRow.getAttribute("class");
            if (classes != null
                    && (classes.contains("status-publish")
                    || classes.contains("status-draft"))) {
                return classes;
            }
            return null;
        });
        logger.debug("Post status retrieved: {}", status);
        return status;
    }

    @Step("Move post '{title}' to trash")
    public PostsPage movePostToTrash(String title) {
        logger.info("Moving post to trash");
        WebDriver driver = Browser.getDriver();
        WebElement postRow = findRow(postRows, title);
        new Actions(driver).moveToElement(postRow).perform();
        WebElement trashLink = postRow.findElement(trashPostLink);
        String trashUrl = trashLink.getAttribute("href");
        if (trashUrl == null || trashUrl.isBlank()) {
            logger.error("Trash URL was not found for post: {}", title);
            throw new AssertionError(
                    "Trash URL was not found for post: " + title
            );
        }
        driver.get(trashUrl);
        logger.info("Post moved to trash");
        return this;
    }

    @Step("Open posts trash")
    public PostsPage openTrash() {
        logger.debug("Opening posts trash");
        String adminUrl = ConfigReader.getProperty("baseUrl")
                .replaceAll("/+$", "");
        if (adminUrl.endsWith("/wp-admin")) {
            adminUrl = adminUrl.substring(
                    0,
                    adminUrl.length() - "/wp-admin".length()
            );
        }
        Browser.getDriver().get(
                adminUrl + "/wp-admin/edit.php"
                        + "?post_status=trash&post_type=post"
        );
        return this;
    }
    @Step("Permanently delete post '{title}'")
    public PostsPage deletePostPermanently(String title) {
        logger.info("Permanently deleting post");
        WebDriver driver = Browser.getDriver();
        WebDriverWait wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(10)
        );
        WebElement postRow = findRow(postRows, title);
        new Actions(driver).moveToElement(postRow).perform();
        WebElement deleteLink = wait.until(driver1 ->
                postRow.findElements(deletePostButton)
                        .stream()
                        .filter(WebElement::isDisplayed)
                        .findFirst()
                        .orElse(null)
        );
        if (deleteLink == null) {
            throw new AssertionError(
                    "Permanent delete button was not found for post: "
                            + title
            );
        }
        deleteLink.click();
        logger.info("Permanent post deletion action submitted");
        return this;
    }

    @Step("Check whether post '{title}' is present")
    public boolean isPostPresent(String title) {
        boolean present = Browser.getDriver()
                .findElements(postRows)
                .stream()
                .anyMatch(row -> row.getText().contains(title));
        logger.debug("Post present in current list: {}", present);
        return present;
    }

    @Step("Get URL of post '{title}'")
    public String getPostUrl(String title) {
        logger.debug("Retrieving URL of post: {}", title);
        WebElement postRow = findRow(postRows, title);
        String url = postRow.findElement(viewPostLink)
                .getAttribute("href");
        logger.debug("Post URL retrieved: {}", url);
        return url;
    }

    @Step("Open post '{title}' for editing")
    public PostsPage openPostForEditing(String title) {
        logger.info("Opening post for editing: {}", title);
        WebDriver driver = Browser.getDriver();
        WebDriverWait wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(15)
        );
        WebElement postRow = findRow(postRows, title);
        WebElement titleLink = postRow.findElement(
                By.cssSelector(".row-title")
        );
        titleLink.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(publishButton));
        logger.info("Post opened for editing: {}", title);
        return this;
    }
}
