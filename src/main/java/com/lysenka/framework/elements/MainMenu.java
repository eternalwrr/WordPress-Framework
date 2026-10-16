package com.lysenka.framework.elements;

import com.lysenka.framework.webdriver.Browser;
import org.openqa.selenium.By;

public class MainMenu {

    private final By posts = By.id("menu-posts");
    private final By media = By.id("menu-media");
    private final By pages = By.id("menu-pages");
    private final By comments = By.id("menu-comments");
    private final By users = By.xpath(
            "//li[@id='menu-users']/a[contains(normalize-space(.), 'Users')]" //проверяем именно видимость Users а не существование контейнера в html
    );
    public void openPosts() {
        Browser.findVisibleElement(posts).click();
    }

    public void openMedia() {
        Browser.findVisibleElement(media).click();
    }

    public void openPages() {
        Browser.findVisibleElement(pages).click();
    }

    public void openComments() {Browser.findVisibleElement(comments).click();}

    public void openUsers() {
        Browser.findVisibleElement(users).click();
    }

    public boolean isPostsVisible() {
        return Browser.isElementDisplayed(posts);
    }

    public boolean isMediaVisible() {
        return Browser.isElementDisplayed(media);
    }

    public boolean isPagesVisible() {
        return Browser.isElementDisplayed(pages);
    }

    public boolean isCommentsVisible() {
        return Browser.isElementDisplayed(comments);
    }

    public boolean isUsersVisible() {
        return Browser.isElementDisplayed(users);
    }
}
