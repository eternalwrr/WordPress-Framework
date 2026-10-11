package com.lysenka.framework.tests;

import com.lysenka.framework.pages.LoginPage;
import com.lysenka.framework.pages.PagesPage;
import com.lysenka.framework.util.ConfigReader;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

public class PagesTest extends BaseTest {

    @Test
    public void canOpenNewPageFormTest() {
        new LoginPage()
                .open()
                .login(
                        ConfigReader.getProperty("admin.username"),
                        ConfigReader.getProperty("admin.password")
                );
        PagesPage pagesPage = new PagesPage()
                .open()
                .openNewPageForm();
        assertTrue(pagesPage.isEditorOpened(), "Page Editor is not opened");
    }

    @Test
    public void canEnterPageTitleTest() {
        new LoginPage()
                .open()
                .login(
                        ConfigReader.getProperty("admin.username"),
                        ConfigReader.getProperty("admin.password")
                );

        PagesPage pagesPage = new PagesPage()
                .open()
                .openNewPageForm()
                .closePatternModal()
                .enterTitle("this is a title");

        assertTrue(pagesPage.isEditorOpened(), "Page editor was not opened");
    }
}
