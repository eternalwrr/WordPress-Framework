package com.lysenka.framework.pages;

import com.lysenka.framework.webdriver.Browser;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.nio.file.Path;

public class MediaPage extends BasePage {

    private final By addNewMediaFileButton = By.cssSelector("a.page-title-action");
    private final By fileInput = By.cssSelector("input[type='file']");
    private final By deletePermanentlyButton = By.cssSelector("button.delete-attachment");

    public MediaPage open() {
        getMainMenu().openMedia();
        return this;
    }

    public MediaPage openUploadForm() {
        Browser.findVisibleElement(addNewMediaFileButton).click();
        return this;
    }

    public MediaPage uploadFile(Path filePath) {
        WebElement input = Browser.getDriver().findElement(fileInput);
        input.sendKeys(filePath.toAbsolutePath().toString());
        return this;
    }

    public boolean isFileUploaded(String fileName) {
        String nameWithoutExtension = fileName.substring(0, fileName.lastIndexOf('.'));
        By uploadedFile = By.cssSelector(
                "li.attachment[aria-label='" + nameWithoutExtension + "']");
        try {
            Browser.findVisibleElement(uploadedFile);
            return true;
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    public MediaPage deleteFile(String fileName) {
        String nameWithoutExtension = fileName.substring(0, fileName.lastIndexOf('.'));
        By uploadedFile = By.cssSelector("li.attachment[aria-label='" + nameWithoutExtension + "']");
        Browser.findVisibleElement(uploadedFile).click();
        Browser.findVisibleElement(deletePermanentlyButton);
        Browser.findVisibleElement(deletePermanentlyButton).click();
        Browser.getDriver().switchTo().alert().accept();
        return this;
    }

    public boolean isFileAbsent(String fileName) {
        String nameWithoutExtension = fileName.substring(0, fileName.lastIndexOf('.'));
        By uploadedFile = By.cssSelector("li.attachment[aria-label='" + nameWithoutExtension + "']");
        return Browser.waitUntilElementDisappears(uploadedFile);
    }
}
