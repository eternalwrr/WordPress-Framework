package com.lysenka.framework.tests;

import com.lysenka.framework.pages.DashboardPage;
import com.lysenka.framework.pages.LoginPage;
import com.lysenka.framework.pages.MediaPage;
import com.lysenka.framework.util.ConfigReader;
import com.lysenka.framework.webdriver.Browser;
import org.testng.annotations.Test;

import java.nio.file.Path;

import static org.testng.Assert.assertTrue;

public class MediaUploadTest extends BaseTest {

    @Test
    public void canOpenMediaUploadTest() {
        DashboardPage dashboardPage = new LoginPage()
                .open()
                .login(
                        ConfigReader.getProperty("admin.username"),
                        ConfigReader.getProperty("admin.password")
                );

        assertTrue(dashboardPage.isOpened(), "Dashboard Page is not opened after login");

        new MediaPage()
                .open()
                .openUploadForm();

        assertTrue(Browser
                        .getDriver()
                        .getCurrentUrl()
                        .contains("upload.php"),
                "Media upload form is not opened"
        );
    }

    @Test
    public void canUploadAndDeleteMediaFileTest() {
        loginAsAdmin();
        Path file = Path.of("src/test/resources/test-data/test-image.png");
        String fileName = file.getFileName().toString();
        MediaPage mediaPage = new MediaPage()
                .open()
                .openUploadForm()
                .uploadFile(file);
        assertTrue(mediaPage.isFileUploaded(fileName), "File was not uploaded: " + fileName);
        mediaPage.deleteFile(fileName);
        assertTrue(mediaPage.isFileAbsent(fileName), "File was not deleted: " + fileName);
    }
}
