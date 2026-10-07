package com.lysenka.framework.pages;

import com.lysenka.framework.elements.MainMenu;
import com.lysenka.framework.elements.TopPageMenu;

public abstract class BasePage {

    private MainMenu mainMenu;
    private TopPageMenu topPageMenu;

    protected MainMenu getMainMenu() {
        if (mainMenu == null) {
            mainMenu = new MainMenu();
        }
        return mainMenu;
    }

    protected TopPageMenu getTopPageMenu() {
        if (topPageMenu == null) {
            topPageMenu = new TopPageMenu();
        }
        return topPageMenu;
    }
}
