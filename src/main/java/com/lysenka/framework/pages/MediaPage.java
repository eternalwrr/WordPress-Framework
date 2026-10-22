package com.lysenka.framework.pages;

import com.lysenka.framework.webdriver.Browser;
import io.qameta.allure.Step;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.nio.file.Path;

public class MediaPage extends BasePage {

    private static final Logger logger = LogManager.getLogger(MediaPage.class);

    private final By addNewMediaFileButton = By.cssSelector("a.page-title-action");
    private final By fileInputLocator = By.cssSelector("input[type='file']");
    private final By deletePermanentlyButton = By.cssSelector("button.delete-attachment");

    @Step("Open WordPress media library")
    public MediaPage open() {
        logger.info("Opening WordPress media library");
        getMainMenu().openMedia();
        return this;
    }

    @Step("Open media upload form")
    public MediaPage openUploadForm() {
        logger.debug("Opening media upload form");
        Browser.findVisibleElement(addNewMediaFileButton).click();
        return this;
    }

    @Step("Upload media file: {file}")
    public MediaPage uploadFile(Path file) {
        WebDriver driver = Browser.getDriver();
        Path absolutePath = file.toAbsolutePath().normalize();

        if (!java.nio.file.Files.isRegularFile(absolutePath)) {
            logger.error("Test file does not exist: {}", absolutePath);
            throw new IllegalArgumentException(
                    "Test file does not exist: " + absolutePath
            );
        }

        logger.info("Uploading media file: {}", absolutePath.getFileName());

        if (Browser.isRemote() && driver instanceof RemoteWebDriver remoteDriver) {
            logger.debug("Configuring LocalFileDetector for remote upload");
            remoteDriver.setFileDetector(new LocalFileDetector());
        }

        WebElement fileInput = driver.findElement(fileInputLocator);
        fileInput.sendKeys(absolutePath.toString());
        logger.debug("File path submitted to upload input");
        return this;
    }

    @Step("Check whether media file '{fileName}' is uploaded")
    public boolean isFileUploaded(String fileName) {
        String nameWithoutExtension =
                fileName.substring(0, fileName.lastIndexOf('.'));

        By uploadedFile = By.cssSelector(
                "li.attachment[aria-label='" + nameWithoutExtension + "']"
        );

        try {
            Browser.findVisibleElement(uploadedFile);
            logger.info("Media file is visible in library: {}", fileName);
            return true;
        } catch (org.openqa.selenium.TimeoutException e) {
            logger.warn("Media file was not found in library: {}", fileName);
            return false;
        }
    }

    @Step("Delete media file: {fileName}")
    public MediaPage deleteFile(String fileName) {
        String nameWithoutExtension =
                fileName.substring(0, fileName.lastIndexOf('.'));

        By uploadedFile = By.cssSelector(
                "li.attachment[aria-label='" + nameWithoutExtension + "']"
        );

        logger.info("Deleting media file: {}", fileName);
        Browser.findVisibleElement(uploadedFile).click();
        Browser.findVisibleElement(deletePermanentlyButton).click();
        Browser.getDriver().switchTo().alert().accept();
        logger.info("Media deletion confirmed: {}", fileName);
        return this;
    }

    @Step("Check whether media file '{fileName}' is absent")
    public boolean isFileAbsent(String fileName) {
        String nameWithoutExtension =
                fileName.substring(0, fileName.lastIndexOf('.'));

        By uploadedFile = By.cssSelector(
                "li.attachment[aria-label='" + nameWithoutExtension + "']"
        );

        boolean absent = Browser.waitUntilElementDisappears(uploadedFile);
        logger.debug("Media file absent '{}': {}", fileName, absent);
        return absent;
    }
}
