package com.lysenka.framework.tests;

import com.lysenka.framework.pages.PostsPage;
import com.lysenka.framework.webdriver.Browser;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class PostsTest extends BaseTest {

    @Test
    public void canOpenNewPostFormTest() {
        loginAsAdmin();
        PostsPage postsPage = new PostsPage()
                .open()
                .openNewPostForm();
        assertTrue(postsPage.isEditorOpened(), "Post editor is not opened");
    }

    @Test
    public void canEnterPostTitleAndContentTest() {
        String title = "Post To Delete " + System.currentTimeMillis();
        loginAsAdmin();
        PostsPage postsPage = new PostsPage()
                .open()
                .openNewPostForm()
                .enterTitle(title)
                .enterContent("This is a test content");
        assertEquals(
                postsPage.getTitle(), title,
                "Post title was not entered correctly"
        );
        assertEquals(postsPage.getContent(),"This is a test content",
                "Post content was not entered correctly"
        );
    }


    @Test
    public void canPublishDraftPostTest() {
        loginAsAdmin();
        String postTitle = "Draft To Published " + System.currentTimeMillis();
        registerPostForCleanup(postTitle);
        PostsPage postsPage = new PostsPage()
                .open()
                .openNewPostForm()
                .enterTitle(postTitle)
                .enterContent("Test content for draft post")
                .saveDraft()
                .openPostsList()
                .openPostForEditing(postTitle)
                .publish()
                .openPostsList();
        String postStatus = postsPage.getPostStatus(postTitle);
        assertTrue(
                postStatus.contains("status-publish"),
                "Post was not published. Actual classes: " + postStatus
        );
    }



    @Test
    public void canMovePublishedPostToDraftTest() {
        loginAsAdmin();
        String postTitle = "Published To Draft " + System.currentTimeMillis();
        registerPostForCleanup(postTitle);
        PostsPage postsPage = new PostsPage()
                .open()
                .openNewPostForm()
                .enterTitle(postTitle)
                .enterContent("Test content for published post");
        postsPage.publish();
        postsPage.openPostsList();
        String publishedStatus = postsPage.getPostStatus(postTitle);
        assertTrue(
                publishedStatus.contains("status-publish"),
                "Post was not published before status change: " + publishedStatus
        );
        postsPage.changePostStatus(postTitle, "draft");
        String draftStatus = postsPage.getPostStatus(postTitle);
        assertTrue(
                draftStatus.contains("status-draft"),
                "Post was not moved to draft. Actual classes: " + draftStatus
        );
    }


    @Test
    public void canDeletePostTest() {
        String title = "Post To Delete " + System.currentTimeMillis();
        registerPostForCleanup(title);
        loginAsAdmin();
        PostsPage postsPage = new PostsPage()
                .open()
                .openNewPostForm()
                .enterTitle(title)
                .enterContent("This post will be deleted.")
                .saveDraft()
                .openPostsList();
        System.out.println("Current URL: " +
                Browser.getDriver().getCurrentUrl());

        System.out.println("Post title: " + title);

        System.out.println("Post exists: " +
                postsPage.isPostPresent(title));
        assertTrue(
                postsPage.isPostPresent(title),
                "Post should exist before deletion"
        );
        postsPage
                .movePostToTrash(title)
                .openTrash()
                .deletePostPermanently(title);
        assertFalse(
                postsPage.isPostPresent(title),
                "Post should not exist after permanent deletion"
        );
    }
}
