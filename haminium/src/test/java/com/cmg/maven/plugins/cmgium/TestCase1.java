/**
 * Copyright (c) CMG Ltd All rights reserved.
 *
 * This software is the confidential and proprietary information of CMG
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with CMG.
 */

package com.cmg.maven.plugins.cmgium;

import junit.framework.TestCase;

import java.io.File;

/** 
 * DOCME
 * 
 * @Creator Hai Lu
 * @author $Author$
 * @version $Revision$
 * @Last changed: $LastChangedDate$
 */

public class TestCase1 extends TestCase {

	/**
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp() 
	 */
	protected void setUp() throws Exception {
		System.out.println("TestCase1.setUp");
		super.setUp();
	}

	/**
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown() 
	 */
	protected void tearDown() throws Exception {
		System.out.println("TestCase1.tearDown");
		super.tearDown();
	}
	
	public void testCase1() {
        String fileName = "abc.html";
        String[] tmp = fileName.split("\\.");
        String name = tmp[0];
        name = name.substring(0,1).toUpperCase() + name.substring(1, name.length());
        System.out.println(name);

        //FileUtils.writeEmbeddedResourceToLocalFile("drivers/chromedriver.exe", new File("D:/film/test.exe"));
	}

}
