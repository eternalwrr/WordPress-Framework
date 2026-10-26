package com.lysenka.framework.pages;

import com.lysenka.framework.webdriver.Browser;
import com.lysenka.framework.util.ConfigReader;
import io.qameta.allure.Step;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class CommentsPage extends BasePage {

    private static final Logger logger = LogManager.getLogger(CommentsPage.class);

    private final By commentField = By.id("comment");
    private final By authorField = By.id("author");
    private final By emailField = By.id("email");
    private final By submitButton = By.id("submit");
    private final By commentRows = By.cssSelector("#the-comment-list tr");
    private final By commentList = By.id("the-comment-list");
    private final By spamButton = By.cssSelector("a[aria-label='Mark this comment as spam']");
    private final By trashButton = By.cssSelector(".row-actions .trash a");
    private final By approveButton = By.cssSelector(".row-actions .approve a");
    private final By unapproveButton = By.cssSelector(".row-actions .unapprove a");

    private final By spamTab =
            By.cssSelector("ul.subsubsub li.spam a");
    private final By approvedTab =
            By.cssSelector("ul.subsubsub li.approved a");

    private final By commentBody = By.tagName("body");

    @Step("Enter comment text")
    public CommentsPage enterComment(String comment) {
        logger.debug("Entering comment text");
        Browser.findVisibleElement(commentField).sendKeys(comment);
        return this;
    }

    @Step("Enter comment author")
    public CommentsPage enterAuthor(String author) {
        logger.debug("Entering comment author");
        Browser.findVisibleElement(authorField).sendKeys(author);
        return this;
    }

    @Step("Enter comment author email")
    public CommentsPage enterEmail(String email) {
        logger.debug("Entering comment author email");
        Browser.findVisibleElement(emailField).sendKeys(email);
        return this;
    }

    @Step("Submit comment")
    public CommentsPage submitComment() {
        logger.info("Submitting comment");
        Browser.findVisibleElement(submitButton).click();
        return this;
    }

    @Step("Open post for commenting")
    public CommentsPage openPost(String postUrl) {
        logger.info("Opening post for commenting: {}", postUrl);
        WebDriver driver = Browser.getDriver();
        driver.manage().deleteAllCookies();
        driver.get(postUrl);
        return this;
    }

    @Step("Check whether comment is visible on the page")
    public boolean isCommentVisible(String comment) {
        boolean visible = Browser.getDriver()
                .findElement(commentBody)
                .getText()
                .contains(comment);
        logger.debug("Comment visible on page: {}", visible);
        return visible;
    }

    @Step("Open WordPress comments administration")
    public CommentsPage open() {
        logger.info("Opening WordPress comments administration");
        Browser.open(
                ConfigReader.getProperty("baseUrl") + "/edit-comments.php"
        );
        return this;
    }

    @Step("Check whether comment is present in the current list")
    public boolean isCommentPresent(String commentText) {
        return waitForComment(commentText);
    }

    @Step("Approve comment")
    public CommentsPage approveComment(String commentText) {
        logger.info("Approving comment");
        WebDriver driver = Browser.getDriver();
        WebDriverWait wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(15)
        );
        WebElement row = findRow(commentRows, commentText);
        if (!row.findElements(approveButton).isEmpty()) {
            WebElement approveLink = row.findElement(approveButton);
            String approveUrl = approveLink.getAttribute("href");
            if (approveUrl == null || approveUrl.isBlank()) {
                logger.error("Approve URL was not found for comment");
                throw new AssertionError(
                        "Approve URL was not found for comment: "
                                + commentText
                );
            }
            driver.get(approveUrl);
            logger.info("Comment approval action submitted");
        } else if (row.findElements(unapproveButton).isEmpty()) {
            logger.error(
                    "Neither Approve nor Unapprove action was found"
            );
            throw new AssertionError(
                    "Neither Approve nor Unapprove action found for comment: "
                            + commentText
            );
        } else {
            logger.debug("Comment is already approved");
        }
        return this;
    }

    @Step("Open approved comments")
    public CommentsPage openApproved() {
        logger.info("Opening approved comments");
        WebDriver driver = Browser.getDriver();
        WebDriverWait wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(15)
        );
        WebElement approvedLink = wait.until(
                ExpectedConditions.presenceOfElementLocated(approvedTab)
        );
        String approvedUrl = approvedLink.getAttribute("href");
        if (approvedUrl == null || approvedUrl.isBlank()) {
            logger.error("Approved comments tab URL was not found");
            throw new AssertionError(
                    "Approved tab URL was not found"
            );
        }
        driver.get(approvedUrl);
        wait.until(
                ExpectedConditions.urlContains("comment_status=approved")
        );
        wait.until(
                ExpectedConditions.presenceOfElementLocated(commentList)
        );
        logger.debug("Approved comments list opened");
        return this;
    }

    @Step("Mark comment as spam")
    public CommentsPage markCommentAsSpam(String commentText) {
        logger.info("Marking comment as spam");
        WebDriver driver = Browser.getDriver();
        WebDriverWait wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(15)
        );
        WebElement row = findRow(commentRows, commentText);
        new Actions(driver).moveToElement(row).perform();
        WebElement spamLink = wait.until(d ->
                row.findElements(spamButton)
                        .stream()
                        .filter(WebElement::isDisplayed)
                        .findFirst()
                        .orElse(null)
        );
        String spamUrl = spamLink.getAttribute("href");
        if (spamUrl != null && !spamUrl.isBlank()) {
            driver.get(spamUrl);
        } else {
            spamLink.click();
        }
        logger.info("Spam action submitted");
        return this;
    }

    @Step("Open spam comments")
    public CommentsPage openSpam() {
        logger.info("Opening spam comments");
        WebDriver driver = Browser.getDriver();
        WebDriverWait wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(15)
        );
        WebElement spamLink = wait.until(
                ExpectedConditions.elementToBeClickable(spamTab)
        );
        String spamUrl = spamLink.getAttribute("href");
        if (spamUrl == null || spamUrl.isBlank()) {
            logger.error("Spam tab URL was not found");
            throw new AssertionError("Spam tab URL was not found");
        }

        driver.get(spamUrl);
        wait.until(
                ExpectedConditions.urlContains("comment_status=spam")
        );
        wait.until(
                ExpectedConditions.presenceOfElementLocated(commentList)
        );
        logger.debug("Spam comments list opened");
        return this;
    }

    @Step("Move comment to trash")
    public CommentsPage moveCommentToTrash(String commentText) {
        logger.info("Moving comment to trash");
        WebDriver driver = Browser.getDriver();
        WebDriverWait wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(15)
        );
        WebElement row = findRow(commentRows, commentText);
        new Actions(driver).moveToElement(row).perform();
        WebElement trashLink = wait.until(d ->
                row.findElements(trashButton)
                        .stream()
                        .filter(WebElement::isDisplayed)
                        .findFirst()
                        .orElse(null)
        );
        String trashUrl = trashLink.getAttribute("href");
        if (trashUrl != null && !trashUrl.isBlank()) {
            driver.get(trashUrl);
        } else {
            trashLink.click();
        }
        logger.info("Comment trash action submitted");
        return this;
    }

    @Step("Open trashed comments")
    public CommentsPage openTrash() {
        logger.info("Opening trashed comments");
        WebDriver driver = Browser.getDriver();
        WebDriverWait wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(15)
        );
        String adminUrl = ConfigReader.getProperty("baseUrl")
                .replaceAll("/+$", "");
        if (adminUrl.endsWith("/wp-admin")) {
            adminUrl = adminUrl.substring(
                    0,
                    adminUrl.length() - "/wp-admin".length()
            );
        }
        String trashUrl = adminUrl
                + "/wp-admin/edit-comments.php?comment_status=trash";

        driver.get(trashUrl);
        wait.until(
                ExpectedConditions.urlContains("comment_status=trash")
        );
        wait.until(
                ExpectedConditions.presenceOfElementLocated(commentList)
        );
        logger.debug("Trashed comments list opened");
        return this;
    }

    @Step("Wait for comment to appear in the current list")
    public boolean waitForComment(String commentText) {
        logger.debug("Waiting for comment to appear in current list");
        WebDriverWait wait = new WebDriverWait(
                Browser.getDriver(),
                Duration.ofSeconds(10)
        );
        boolean present = wait.until(driver ->
                driver.findElements(commentRows)
                        .stream()
                        .anyMatch(row ->
                                row.getText().contains(commentText)
                        )
        );
        logger.debug("Comment appeared in current list: {}", present);
        return present;
    }
}
