/**
 * Copyright (c) CMG Ltd All rights reserved.
 *
 * This software is the confidential and proprietary information of CMG
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with CMG.
 */

package com.cmg.maven.plugins.cmgium;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.cmg.maven.plugins.cmgium.utils.Environment;
import com.cmg.maven.plugins.cmgium.utils.Validator;
import com.thoughtworks.selenium.webdriven.WebDriverBackedSelenium;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.openqa.selenium.*;

import com.cmg.maven.plugins.cmgium.exception.CmgiumException;
import com.cmg.maven.plugins.cmgium.utils.PropertiesHelper;
import com.thoughtworks.selenium.Selenium;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

/**
 * DOCME
 * 
 * @Creator Hai Lu
 * @author $Author$
 * @version $Revision$
 * @Last changed: $LastChangedDate$
 */

public class Cmgium {

	private static Cmgium cmgium;


	public static final String DEFAULT_WEBDRIVER_CLASS = "org.openqa.selenium.firefox.FirefoxDriver";

    public static final String DEFAULT_TEST_PACKAGE = "com.cmg.cmgium.testscript";

	public static final String PROP_WEBDRIVER_CLASS = "webdriver";

    public static final String PROP_IE_WEBDRIVER = "webdriver.ie.driver";

    public static final String PROP_CHROME_WEBDRIVER = "webdriver.chrome.driver";

	public static final String PROP_BASE_URL = "baseURL";

	public static final String PROP_PROJECT_BASE_DIR = "projectBaseDir";

	public static final String PROP_PROJECT_BUILD_DIR = "projectBuildDir";

	public static final String PROP_TEST_PACKAGE = "testPackage";

	private static Selenium selenium;

	private static WebDriver driver;

	public static Selenium selenium() throws SecurityException,
			InstantiationException, IllegalAccessException,
			ClassNotFoundException, CmgiumException {
		setup();

		return selenium;
	}

	public static Cmgium cmgium() {
		if (cmgium == null) {
			cmgium = new Cmgium();
		}
		return cmgium;
	}

	public static void setup() throws SecurityException,
			InstantiationException, IllegalAccessException,
			ClassNotFoundException, CmgiumException {
		if (selenium == null) {
            System.setProperty(PROP_IE_WEBDRIVER, PropertiesHelper.getKey(PROP_IE_WEBDRIVER));
            System.setProperty(PROP_CHROME_WEBDRIVER, PropertiesHelper.getKey(PROP_CHROME_WEBDRIVER));
			setup(PropertiesHelper.getKey(PROP_WEBDRIVER_CLASS),
					PropertiesHelper.getKey(PROP_BASE_URL));
		}
	}

	public static void setup(String webdriver, String baseURL)
			throws SecurityException, InstantiationException,
			IllegalAccessException, ClassNotFoundException, CmgiumException {
		if (selenium == null || driver == null) {
            if (Environment.getBrowser() != Environment.IE) {
                Object driverObj = Class.forName(webdriver).newInstance();
                if (driverObj instanceof WebDriver) {
                    driver = (WebDriver) driverObj;
                } else {
                    throw new CmgiumException(webdriver
                            + " is not valid Webdriver class");
                }
            } else {
                DesiredCapabilities caps = DesiredCapabilities.internetExplorer();
                caps.setCapability("ignoreZoomSetting", true);
                driver = new InternetExplorerDriver(caps);
            }
			selenium = new WebDriverBackedSelenium(driver, baseURL);
			driver.manage().window().maximize();
			selenium.setTimeout("120000");
		}
	}

	public static void teardown() {
		driver.quit();
		driver = null;
		selenium = null;
		cmgium = null;
	}

    public void click(String locator) {
        if (Environment.getBrowser() != Environment.IE) {
            selenium.click(locator);
        } else {
            driver.findElement(getBy(locator)).click();
        }
    }

    public void type(String locator, String text) {
        if (Environment.getBrowser() != Environment.IE) {
            selenium.type(locator, text);
        } else {
            driver.findElement(getBy(locator)).sendKeys(text);
        }

    }

    public void sendKeys(String locator, String text) {
        driver.findElement(getBy(locator)).sendKeys(text);
    }

    public void andWait() {
        selenium.waitForPageToLoad("30000");
    }

	public void takeScreenshoot(String name) {
		CmgiumMethod currentTestMethod = getCurrentTestMethod();
		// skip take screenshot if could not found current test method execute
		if (currentTestMethod == null) 
			return;
		
		String screenshootDir = PropertiesHelper.getKey(PROP_PROJECT_BUILD_DIR)
				+ File.separator + "screenshots" + File.separator
				+ currentTestMethod.getClassName();
		File f = new File(screenshootDir);
		if (!f.exists() || !f.isDirectory()) {
			f.mkdirs();
		}
		File output = null;
		File file;
		try {
			if (name == null || name.length() == 0) {
				File[] files = f.listFiles();
				int count = 0;
				if (files != null && files.length > 0) {
					for (File fl : files) {
						if (fl.getName()
								.startsWith(currentTestMethod.getName())) {
							count++;
						}
					}
				}
				name = currentTestMethod.getName()
						+ (count == 0 ? "" : (" (" + count + ")"));
			}
			output = ((TakesScreenshot) driver)
					.getScreenshotAs(OutputType.FILE);
			file = new File(screenshootDir, name + ".png");
			FileUtils.moveFile(output, file);
		} catch (IOException e) {

		}
	}

	public void takeScreenshoot() {
		takeScreenshoot("");
	}

	public static CmgiumMethod getCurrentTestMethod() {
		String testPackge = PropertiesHelper.getKey(PROP_TEST_PACKAGE);
		final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
		for (int i = 0; i < ste.length; i++) {
			String className = ste[i].getClassName();
			String methodName = ste[i].getMethodName();
			if (className.startsWith(testPackge)) {
				CmgiumMethod method = new CmgiumMethod();
				method.setClassName(className);
				method.setName(methodName);
				return method;
			}
		}
		return null;
	}

    private By getBy(String locator) {
        String[] tmpSplit = locator.split("=");
        if (tmpSplit[0].equalsIgnoreCase("id")) {
            return By.id(tmpSplit[1]);
        } else if (tmpSplit[0].equalsIgnoreCase("name")) {
            return By.name(tmpSplit[1]);
        } else if (tmpSplit[0].equalsIgnoreCase("css")) {
            return By.cssSelector(tmpSplit[1]);
        } else if (tmpSplit[0].equalsIgnoreCase("link")) {
            return By.linkText(tmpSplit[1]);
        } else if (tmpSplit[0].equalsIgnoreCase("tag")) {
            return By.tagName(tmpSplit[1]);
        } else if (tmpSplit[0].equalsIgnoreCase("class")) {
            return By.className(tmpSplit[1]);
        }
        return By.xpath(locator);
    }

}
