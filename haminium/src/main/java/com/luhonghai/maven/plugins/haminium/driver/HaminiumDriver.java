/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Hai Lu luhonghai@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.luhonghai.maven.plugins.haminium.driver;

import com.thoughtworks.selenium.Selenium;
import com.thoughtworks.selenium.webdriven.WebDriverBackedSelenium;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * @author Hai Lu
 */

public class HaminiumDriver {

    private static final Logger LOG = Logger.getLogger(HaminiumDriver.class.getName());

    private final HaminiumDriverProfile profile;

    private final String baseUrl;

    private WebDriver driver;

    private Selenium selenium;

    public HaminiumDriver(String baseUrl, HaminiumDriverProfile profile) {
        this.profile = profile;
        this.baseUrl = baseUrl;
    }

    protected void init() throws IOException {
        driver = new RemoteWebDriver(profile.getRemoteUrl(), profile.getCapabilities());
        selenium = new WebDriverBackedSelenium(driver, baseUrl);
    }

    /**
     *  Try to shutdown WebDriver
     */

    public void shutdown() {
        if (driver != null) {
            driver.quit();
        }
        driver = null;
        selenium = null;
    }

}
