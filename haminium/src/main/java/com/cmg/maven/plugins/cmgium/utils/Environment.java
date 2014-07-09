package com.cmg.maven.plugins.cmgium.utils;

import com.cmg.maven.plugins.cmgium.Cmgium;

/**
 * Created by Hai Lu on 27/05/2014.
 */
public class Environment {
    public static final int IE = 1;
    public static final int CHROME = 2;
    public static final int FIREFOX = 3;

    public static final int getBrowser() {
        String webdriver = PropertiesHelper.getKey("webdriver");
        if (webdriver.equals("org.openqa.selenium.ie.InternetExplorerDriver")) {
            return IE;
        } else if (webdriver.equals("org.openqa.selenium.chrome.ChromeDriver")) {
            return CHROME;
        }
        return FIREFOX;
    }
}
