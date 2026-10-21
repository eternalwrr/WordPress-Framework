package com.lysenka.framework.tests;

import com.lysenka.framework.pages.LoginPage;
import com.lysenka.framework.pages.PagesPage;
import com.lysenka.framework.pages.PostsPage;
import com.lysenka.framework.util.ConfigReader;
import com.lysenka.framework.webdriver.Browser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.util.LinkedHashSet;
import java.util.Set;

public abstract class BaseTest {

    private static final Logger logger = LogManager.getLogger(BaseTest.class);

    private final Set<String> postsToCleanup = new LinkedHashSet<>();
    private final Set<String> pagesToCleanup = new LinkedHashSet<>();

    @BeforeMethod
    public void setUp() {
        logger.info("Preparing test environment");
        postsToCleanup.clear();
        pagesToCleanup.clear();
        Browser.getDriver();
        logger.debug("Test environment is ready");
    }

    protected void loginAsAdmin() {
        logger.info("Logging in as administrator");
        new LoginPage()
                .open()
                .login(
                        ConfigReader.getProperty("admin.username"),
                        ConfigReader.getProperty("admin.password")
                );
        logger.info("Administrator login flow completed");
    }

    protected void registerPostForCleanup(String title) {
        postsToCleanup.add(title);
        logger.debug("Registered post for cleanup: {}", title);
    }

    protected void registerPageForCleanup(String title) {
        pagesToCleanup.add(title);
        logger.debug("Registered page for cleanup: {}", title);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        logger.info("Starting test cleanup");
        try {
            if (!postsToCleanup.isEmpty() || !pagesToCleanup.isEmpty()) {
                ensureAdminSession();
                cleanupPosts();
                cleanupPages();
            } else {
                logger.debug("No posts or pages registered for cleanup");
            }
        } catch (RuntimeException | AssertionError e) {
            logger.error("Test data cleanup failed", e);
            throw e;
        } finally {
            try {
                Browser.close();
            } catch (RuntimeException e) {
                logger.error("Error while closing browser during teardown", e);
            } finally {
                logger.info("Test teardown finished");
            }
        }
    }

    private void ensureAdminSession() {
        logger.debug("Checking administrator session");
        String baseUrl = ConfigReader.getProperty("baseUrl");
        Browser.getDriver().get(baseUrl);
        if (Browser.isElementDisplayed(By.id("user_login"))) {
            logger.info("Administrator session is not active; logging in");
            new LoginPage().login(
                    ConfigReader.getProperty("admin.username"),
                    ConfigReader.getProperty("admin.password")
            );
        } else {
            logger.debug("Administrator session appears to be active");
        }
    }

    private void cleanupPosts() {
        PostsPage postsPage = new PostsPage();
        for (String title : postsToCleanup) {
            logger.info("Cleaning up post: {}", title);
            postsPage.openPostsList();
            if (postsPage.isPostPresent(title)) {
                logger.debug("Moving post to trash: {}", title);
                postsPage.movePostToTrash(title);
            } else {
                logger.debug("Post not found in posts list: {}", title);
            }
            postsPage.openTrash();
            if (postsPage.isPostPresent(title)) {
                logger.debug("Deleting post permanently: {}", title);
                postsPage.deletePostPermanently(title);
            } else {
                logger.debug("Post not found in trash: {}", title);
            }
        }
        logger.info("Post cleanup finished");
    }

    private void cleanupPages() {
        PagesPage pagesPage = new PagesPage();
        for (String title : pagesToCleanup) {
            logger.info("Cleaning up page: {}", title);
            pagesPage.openPagesList();
            if (pagesPage.isPagePresent(title)) {
                logger.debug("Moving page to trash: {}", title);
                pagesPage.movePageToTrash(title);
            } else {
                logger.debug("Page not found in pages list: {}", title);
            }
            pagesPage.openTrash();
            if (pagesPage.isPagePresent(title)) {
                logger.debug("Deleting page permanently: {}", title);
                pagesPage.deletePagePermanently(title);
            } else {
                logger.debug("Page not found in trash: {}", title);
            }
        }
        logger.info("Page cleanup finished");
    }
}