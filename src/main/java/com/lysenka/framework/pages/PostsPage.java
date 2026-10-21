package com.lysenka.framework.pages;

import com.lysenka.framework.util.ConfigReader;
import com.lysenka.framework.webdriver.Browser;
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
    private final By savingIndicator = By.cssSelector(".editor-post-saving");
    private final By savedIndicator = By.cssSelector(".editor-post-saved");
    private final By savedButton = By.xpath(
            "//button[.//span[normalize-space()='Saved'] or normalize-space()='Saved']"
    );

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

    public PostsPage enterContent(String content) {
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
            return wait.until(
                    ExpectedConditions.visibilityOfElementLocated(editorTitle)
            ).getText().trim();
        } finally {
            driver.switchTo().defaultContent();
        }
    }

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
            return wait.until(
                    ExpectedConditions.visibilityOfElementLocated(editorContent)
            ).getText().trim();
        } finally {
            driver.switchTo().defaultContent();
        }
    }

    public PostsPage publish() {
        WebDriver driver = Browser.getDriver();
        WebDriverWait wait = new WebDriverWait(
                driver,
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
        return this;
    }

    public PostsPage saveDraft() {
        WebDriver driver = Browser.getDriver();
        WebDriverWait wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(20)
        );
        wait.until(
                ExpectedConditions.elementToBeClickable(saveDraftButton)
        ).click();

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(savedButton)
        );
        return this;
    }



    public PostsPage openPostsList() {
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

    public PostsPage changePostStatus(String title, String status) {
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
        if (postRow == null) {
            throw new AssertionError("Post not found: " + title);
        }
        new Actions(driver).moveToElement(postRow).perform();
        postRow.findElement(quickEditButton).click();
        WebElement editRow = wait.until(
                ExpectedConditions.visibilityOfElementLocated(quickEditRow)
        );
        Select statusDropdown = new Select(
                editRow.findElement(statusSelect)
        );

        System.out.println("Available statuses:");
        statusDropdown.getOptions().forEach(option ->
                System.out.println(
                        "Text: [" + option.getText() +
                                "], value: [" + option.getAttribute("value") + "]"
                )
        );

        statusDropdown.selectByValue(status);
        editRow.findElement(quickEditUpdateButton).click();
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
        if (postRow == null) {
            throw new AssertionError("Post not found: " + title);
        }
        new Actions(driver).moveToElement(postRow).perform();
        WebElement trashLink = postRow.findElement(trashPostLink);
        String trashUrl = trashLink.getAttribute("href");
        if (trashUrl == null || trashUrl.isBlank()) {
            throw new AssertionError(
                    "Trash URL was not found for post: " + title
            );
        }
        driver.get(trashUrl);
        return this;
    }

    public PostsPage openTrash() {
        String adminUrl = ConfigReader.getProperty("baseUrl")
                .replaceAll("/+$", "");
        if (adminUrl.endsWith("/wp-admin")) {
            adminUrl = adminUrl.substring(
                    0,
                    adminUrl.length() - "/wp-admin".length()
            );
        }
        Browser.getDriver().get(
                adminUrl
                        + "/wp-admin/edit.php?post_status=trash&post_type=post"
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
        if (postRow == null) {
            throw new AssertionError(
                    "Post was not found in Trash: " + title
            );
        }
        new Actions(driver).moveToElement(postRow).perform();
        WebElement deleteLink = wait.until(d ->
                postRow.findElements(deletePostButton)
                        .stream()
                        .filter(WebElement::isDisplayed)
                        .findFirst()
                        .orElse(null)
        );
        if (deleteLink == null) {
            throw new AssertionError(
                    "Permanent delete button was not found for post: " + title
            );
        }
        deleteLink.click();
        return this;
    }

    public boolean isPostPresent(String title) {
        return Browser.getDriver()
                .findElements(postRows)
                .stream()
                .anyMatch(row -> row.getText().contains(title));
    }

    public String getPostUrl(String title) {
        WebDriver driver = Browser.getDriver();
        WebDriverWait wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(20)
        );
        WebElement postRow = wait.until(d ->
                d.findElements(postRows)
                        .stream()
                        .filter(row -> row.getText().contains(title))
                        .findFirst()
                        .orElse(null)
        );
        if (postRow == null) {
            throw new AssertionError("Post not found: " + title);
        }
        return postRow.findElement(viewPostLink).getAttribute("href");
    }

    public PostsPage openPostForEditing(String title) {
        WebDriver driver = Browser.getDriver();
        WebDriverWait wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(15)
        );
        WebElement postRow = wait.until(d ->
                d.findElements(postRows)
                        .stream()
                        .filter(row -> row.getText().contains(title))
                        .findFirst()
                        .orElse(null)
        );
        if (postRow == null) {
            throw new AssertionError("Post not found: " + title);
        }
        WebElement titleLink = postRow.findElement(
                By.cssSelector(".row-title")
        );
        titleLink.click();
        wait.until(
                ExpectedConditions.visibilityOfElementLocated(publishButton)
        );
        return this;
    }
}
