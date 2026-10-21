package com.lysenka.framework.tests;

import com.lysenka.framework.pages.PagesPage;
import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class PagesTest extends BaseTest {

    @Test
    public void canOpenNewPageFormTest() {
        loginAsAdmin();
        PagesPage pagesPage = new PagesPage()
                .open()
                .openNewPageForm();
        assertTrue(pagesPage.isEditorOpened(), "Page Editor is not opened");
    }

    @Test
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
    public void canPublishDraftPageTest() {
        loginAsAdmin();
        String pageTitle = "Draft To Published !!! " + System.currentTimeMillis();
        registerPageForCleanup(pageTitle);
        PagesPage pagesPage = new PagesPage()
                .open()
                .openNewPageForm()
                .closePatternModal()
                .enterTitle(pageTitle)
                .enterContent("Test content for draft page");
        pagesPage.saveDraft();
        pagesPage
                .openPagesList()
                .changePageStatus(pageTitle, "publish");
        String pageStatus = pagesPage.getPageStatus(pageTitle);
        assertTrue(
                pageStatus.contains("status-publish"),
                "Page was not published. Actual classes: " + pageStatus
        );
    }

    @Test
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
