package com.lysenka.framework.tests;

import com.lysenka.framework.pages.LoginPage;
import com.lysenka.framework.pages.PagesPage;
import com.lysenka.framework.pages.PostsPage;
import com.lysenka.framework.util.ConfigReader;
import com.lysenka.framework.webdriver.Browser;
import io.qameta.allure.Step;
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

    @Step("Log in as administrator")
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

    @Step("Register post '{title}' for cleanup")
    protected void registerPostForCleanup(String title) {
        postsToCleanup.add(title);
        logger.debug("Registered post for cleanup: {}", title);
    }

    @Step("Register page '{title}' for cleanup")
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

    @Step("Ensure administrator session is active")
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

    @Step("Clean up test posts")
    private void cleanupPosts() {
        PostsPage postsPage = new PostsPage();

        for (String title : postsToCleanup) {
            logger.info("Cleaning up post: {}", title);

            postsPage.openPostsList();

            if (postsPage.isPostPresent(title)) {
                postsPage.movePostToTrash(title);
            }

            postsPage.openTrash();

            if (postsPage.isPostPresent(title)) {
                postsPage.deletePostPermanently(title);
            }
        }

        logger.info("Post cleanup finished");
    }

    @Step("Clean up test pages")
    private void cleanupPages() {
        PagesPage pagesPage = new PagesPage();

        for (String title : pagesToCleanup) {
            logger.info("Cleaning up page: {}", title);

            pagesPage.openPagesList();

            if (pagesPage.isPagePresent(title)) {
                pagesPage.movePageToTrash(title);
            }

            pagesPage.openTrash();

            if (pagesPage.isPagePresent(title)) {
                pagesPage.deletePagePermanently(title);
            }
        }

        logger.info("Page cleanup finished");
    }
}
