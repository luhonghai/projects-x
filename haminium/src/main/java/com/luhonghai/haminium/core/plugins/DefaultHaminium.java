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

package com.luhonghai.haminium.core.plugins;

import com.luhonghai.haminium.annotation.Function;
import com.luhonghai.haminium.annotation.Parameter;
import com.luhonghai.haminium.core.Haminium;
import com.thoughtworks.selenium.Selenium;
import org.openqa.selenium.WebDriver;

/**
 * @author Hai Lu
 */
public class DefaultHaminium extends Haminium {

    public DefaultHaminium(WebDriver driver, Selenium selenium) {
        super(driver, selenium);
    }

    /**
     * This method (command) use for saves the entire contents of the current window canvas
     * to a PNG file.
     * By default, the root folder of screen shots is [PROJECT ROOT]/target/artifact/screenshots
     *
     * @param folderName the folder name inside root folder. Leave empty this will take
     *                   Test suite name
     * @param fileName the file name of screen shoot. Leave empty this will take
     *                 Test case name
     */
    @Function(
            comment =  "<p>This method (command) use for saves the entire contents" +
                    " of the current window canvas" +
                    "to a PNG file." +
                    "<p>By default, the root folder of screen shots is " +
                    "[PROJECT ROOT]/target/artifact/screenshots</p>",
            arguments =  {
                    @Parameter(
                            name = "folderName",
                            comment = "the folder name inside root folder. " +
                                      "Leave empty this will take " +
                                      "Test suite name"),
                    @Parameter(
                            name = "fileName",
                            comment = "the file name of screen shoot. " +
                                    "Leave empty this will take" +
                                    " Test case name")
            }
    )
    public void takeScreenshoot(String folderName, String fileName) {

    }

}
