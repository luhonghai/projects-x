/** The MIT License (MIT)
 *
 *   Copyright (c) 2004 Hai Lu luhonghai@gmail.com
 *
 *   Permission is hereby granted, free of charge, to any person obtaining a copy
 *   of this software and associated documentation files (the "Software"), to deal
 *   in the Software without restriction, including without limitation the rights
 *   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   copies of the Software, and to permit persons to whom the Software is
 *   furnished to do so, subject to the following conditions:
 *
 *   The above copyright notice and this permission notice shall be included in
 *   all copies or substantial portions of the Software.
 *
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *   THE SOFTWARE.
 */

package com.luhonghai.haminium.old.utils;

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
