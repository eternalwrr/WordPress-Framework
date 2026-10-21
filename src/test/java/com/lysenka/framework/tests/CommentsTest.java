package com.lysenka.framework.tests;

import com.lysenka.framework.pages.CommentsPage;
import com.lysenka.framework.pages.PostsPage;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

public class CommentsTest extends BaseTest {

    @Test
    public void guestCanCommentOnPublishedPostTest() {
        loginAsAdmin();

        String postTitle = "Guest Comment Test " + System.currentTimeMillis();
        String commentText = "Guest comment " + System.currentTimeMillis();

        PostsPage postsPage = new PostsPage()
                .open()
                .openNewPostForm()
                .enterTitle(postTitle)
                .enterContent("Post content for guest comment test");
        registerPostForCleanup(postTitle);
        postsPage.publish();
        postsPage.openPostsList();
        String postUrl = postsPage.getPostUrl(postTitle);
        CommentsPage commentsPage = new CommentsPage()
                .openPost(postUrl)
                .enterComment(commentText)
                .enterAuthor("Test Guest")
                .enterEmail("guest@example.com")
                .submitComment();

        assertTrue(
                commentsPage.isCommentVisible(commentText),
                "Guest comment was not displayed on the post"
        );

    }

    @Test
    public void guestCommentCanBeFoundInAdminTest() {
        loginAsAdmin();
        String postTitle = "Guest Comment Test " + System.currentTimeMillis();
        String commentText = "Guest comment " + System.currentTimeMillis();
        PostsPage postsPage = new PostsPage()
                .open()
                .openNewPostForm()
                .enterTitle(postTitle)
                .enterContent("Post content for guest comment test");
        registerPostForCleanup(postTitle);
        postsPage.publish();
        postsPage.openPostsList();
        String postUrl = postsPage.getPostUrl(postTitle);
        CommentsPage commentsPage = new CommentsPage()
                .openPost(postUrl)
                .enterComment(commentText)
                .enterAuthor("Test Guest")
                .enterEmail("guest@example.com")
                .submitComment();
        assertTrue(
                commentsPage.isCommentVisible(commentText),
                "Guest comment was not displayed on the post"
        );
        loginAsAdmin();
        CommentsPage adminCommentsPage = new CommentsPage().open();
        assertTrue(
                adminCommentsPage.isCommentPresent(commentText),
                "Guest comment was not found in WordPress admin"
        );
    }

    @Test
    public void guestCommentCanBeMovedToTrashTest() {
        loginAsAdmin();
        String postTitle = "Trash Comment Test " + System.currentTimeMillis();
        String commentText = "Comment to trash " + System.currentTimeMillis();
        PostsPage postsPage = new PostsPage()
                .open()
                .openNewPostForm()
                .enterTitle(postTitle)
                .enterContent("Post content for comment test");
        registerPostForCleanup(postTitle);
        postsPage.publish();
        postsPage.openPostsList();
        String postUrl = postsPage.getPostUrl(postTitle);
        CommentsPage commentsPage = new CommentsPage()
                .openPost(postUrl)
                .enterComment(commentText)
                .enterAuthor("Test Guest")
                .enterEmail("guest@example.com")
                .submitComment();
        assertTrue(
                commentsPage.isCommentVisible(commentText),
                "Comment was not displayed on the post"
        );
        loginAsAdmin();
        commentsPage.open();
        assertTrue(
                commentsPage.isCommentPresent(commentText),
                "Comment was not found in admin"
        );
        commentsPage.moveCommentToTrash(commentText)
                .openTrash();
        assertTrue(
                commentsPage.isCommentPresent(commentText),
                "Comment was not found in Trash after moving it there"
        );
    }

    @Test
    public void guestCommentCanBeMovedToSpamTest() {
        loginAsAdmin();
        String postTitle = "Trash Comment Test " + System.currentTimeMillis();
        String commentText = "Comment to trash " + System.currentTimeMillis();
        PostsPage postsPage = new PostsPage()
                .open()
                .openNewPostForm()
                .enterTitle(postTitle)
                .enterContent("Post content for comment test");
        registerPostForCleanup(postTitle);
        postsPage.publish();
        postsPage.openPostsList();
        String postUrl = postsPage.getPostUrl(postTitle);
        CommentsPage commentsPage = new CommentsPage()
                .openPost(postUrl)
                .enterComment(commentText)
                .enterAuthor("Test Guest")
                .enterEmail("guest@example.com")
                .submitComment();
        assertTrue(
                commentsPage.isCommentVisible(commentText),
                "Comment was not displayed on the post"
        );
        loginAsAdmin();
        commentsPage.open();
        assertTrue(
                commentsPage.isCommentPresent(commentText),
                "Comment was not found in admin"
        );
        commentsPage.markCommentAsSpam(commentText)
                        .openSpam();
        assertTrue(
                commentsPage.isCommentPresent(commentText),
                "Comment was not found in Spam after marking it as spam"
        );
    }

    @Test
    public void guestCommentCanBeApprovedTest() {
        loginAsAdmin();
        String postTitle = "Trash Comment Test " + System.currentTimeMillis();
        String commentText = "Comment to trash " + System.currentTimeMillis();
        PostsPage postsPage = new PostsPage()
                .open()
                .openNewPostForm()
                .enterTitle(postTitle)
                .enterContent("Post content for comment test");
        registerPostForCleanup(postTitle);
        postsPage.publish();
        postsPage.openPostsList();
        assertTrue(
                postsPage.isPostPresent(postTitle),
                "Post was not published: " + postTitle
        );
        String postUrl = postsPage.getPostUrl(postTitle);
        CommentsPage commentsPage = new CommentsPage()
                .openPost(postUrl)
                .enterComment(commentText)
                .enterAuthor("Test Guest")
                .enterEmail("guest@example.com")
                .submitComment();
        assertTrue(
                commentsPage.isCommentVisible(commentText),
                "Comment was not displayed on the post"
        );
        loginAsAdmin();
        commentsPage.open();
        assertTrue(
                commentsPage.isCommentPresent(commentText),
                "Comment was not found in admin"
        );
        commentsPage.approveComment(commentText)
                        .openApproved();
        assertTrue(
                commentsPage.waitForComment(commentText),
                "Comment was not found in Approved after approving comment"
        );
    }
}
