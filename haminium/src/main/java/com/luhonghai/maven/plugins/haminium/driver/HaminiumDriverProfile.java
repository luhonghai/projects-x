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

import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Hai Lu
 */
public class HaminiumDriverProfile {
    private static final Logger LOG = Logger.getLogger(HaminiumDriverProfile.class.getName());

    private static final String LOCALHOST = "127.0.0.1";

    private static final String PROTOCOL = "http";

    private static final String WEBDRIVER_HUB_FILTER = "/wd/hub";

    // Default Remote Grid Server port is 4444
    private int remotePort = 4444;

    private FirefoxProfile profile;

    private DesiredCapabilities capabilities;

    private Map<String, String> extensions;

    protected HaminiumDriverProfile() {

    }

    public void addExtension(String filePath) {
        if (filePath == null || filePath.length() == 0) return;
        if (extensions == null) {
            extensions = new HashMap<String, String>();
        }
        if (!extensions.containsKey(filePath.toLowerCase())) {
            if (getProfile() == null) {
                setProfile(new FirefoxProfile());
            }
            try {
                getProfile().addExtension(new File(filePath));
                extensions.put(filePath.toLowerCase(), filePath);
            } catch (IOException ioe) {
                LOG.log(Level.SEVERE, "Could not load extension " + filePath, ioe);
            }
        } else {
            LOG.info("Skip existing extension " + filePath);
        }
    }

    public static HaminiumDriverProfile createInstance() {
        return new HaminiumDriverProfile();
    }

    public DesiredCapabilities getCapabilities() {
        if (capabilities == null) {
            capabilities = DesiredCapabilities.firefox();
        }
        capabilities.setCapability(FirefoxDriver.PROFILE, getProfile());
        return capabilities;
    }

    public URL getRemoteUrl() throws MalformedURLException {
        return new URL( PROTOCOL +"://" + LOCALHOST + ":" + getRemotePort() + WEBDRIVER_HUB_FILTER);
    }

    public int getRemotePort() {
        return remotePort;
    }

    public void setRemotePort(int remotePort) {
        this.remotePort = remotePort;
    }

    public FirefoxProfile getProfile() {
        if (profile == null) {
            profile = new FirefoxProfile();
        }
        return profile;
    }

    public void setProfile(FirefoxProfile profile) {
        this.profile = profile;
    }

    public void setCapabilities(DesiredCapabilities capabilities) {
        this.capabilities = capabilities;
    }
}
