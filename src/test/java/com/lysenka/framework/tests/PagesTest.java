package com.lysenka.framework.tests;

import com.lysenka.framework.pages.PagesPage;
import com.lysenka.framework.webdriver.Browser;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import java.time.Duration;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

@Feature("Pages management")
public class PagesTest extends BaseTest {

    @Test
    @Story("Opening new page form")
    public void canOpenNewPageFormTest() {
        loginAsAdmin();
        PagesPage pagesPage = new PagesPage()
                .open()
                .openNewPageForm();
        assertTrue(pagesPage.isEditorOpened(), "Page Editor is not opened");
    }

    @Test
    @Story("Entering title")
    public void canEnterPageTitleTest() {
        loginAsAdmin();

        PagesPage pagesPage = new PagesPage()
                .open()
                .openNewPageForm()
                .closePatternModal()
                .enterTitle("this is a title");

        assertTrue(pagesPage.isEditorOpened(), "Page editor was not opened");

        assertTrue(pagesPage.getTitle().contains("this is a title"), "Page title was not entered");
    }

    @Test
    @Story("Entering content")
    public void canEnterPageContentTest() {
        loginAsAdmin();
        String expectedContent = "this is a CONTENT";
        PagesPage pagesPage = new PagesPage()
                .open()
                .openNewPageForm()
                .closePatternModal()
                .enterContent(expectedContent);
        String actualContent = pagesPage.getContent();
        assertTrue(actualContent.contains(expectedContent), "Page content was not entered");
    }

    @Test
    @Story("Publishing from draft")
    public void canPublishDraftPageTest() {
        loginAsAdmin();
        String pageTitle = "Draft To Published !!! "
                + System.currentTimeMillis();
        registerPageForCleanup(pageTitle);
        PagesPage pagesPage = new PagesPage()
                .open()
                .openNewPageForm()
                .closePatternModal()
                .enterTitle(pageTitle)
                .enterContent("Test content for draft page");
        pagesPage.saveDraft();
        pagesPage.openPagesList();
        pagesPage.publishDraftPage(pageTitle);
        String pageStatus = pagesPage.getPageStatus(pageTitle);
        assertTrue(
                pageStatus.contains("status-publish"),
                "Page was not published. Actual classes: " + pageStatus
        );
    }

    @Test
    @Story("Drafting from publish")
    public void canMovePublishedPageToDraftTest() {
        loginAsAdmin();
        String pageTitle = "Published To Draft " + System.currentTimeMillis();
        registerPageForCleanup(pageTitle);
        PagesPage pagesPage = new PagesPage()
                .open()
                .openNewPageForm()
                .closePatternModal()
                .enterTitle(pageTitle)
                .enterContent("Test content for published page");
        pagesPage.publish();
        pagesPage.openPagesList();
        String publishedStatus = pagesPage.getPageStatus(pageTitle);
        assertTrue(
                publishedStatus.contains("status-publish"),
                "Page was not published before status change: " + publishedStatus
        );
        pagesPage.changePageStatus(pageTitle, "draft");
        String draftStatus = pagesPage.getPageStatus(pageTitle);
        assertTrue(
                draftStatus.contains("status-draft"),
                "Page was not moved to draft. Actual classes: " + draftStatus
        );
    }

    @Test
    @Story("Page deletion")
    public void canDeletePageTest() {
        loginAsAdmin();
        String pageTitle = "Page To Delete " + System.currentTimeMillis();
        PagesPage pagesPage = new PagesPage()
                .open()
                .openNewPageForm()
                .closePatternModal()
                .enterTitle(pageTitle)
                .enterContent("Page created for deletion test");
        pagesPage.saveDraft();
        pagesPage
                .openPagesList()
                .movePageToTrash(pageTitle);
        pagesPage.openTrash();
        pagesPage.deletePagePermanently(pageTitle);
        assertFalse(
                pagesPage.isPagePresent(pageTitle),
                "Page was not permanently deleted: " + pageTitle
        );
    }
}
