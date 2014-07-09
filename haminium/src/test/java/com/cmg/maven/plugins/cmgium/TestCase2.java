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

/** 
 * DOCME
 * 
 * @Creator Hai Lu
 * @author $Author$
 * @version $Revision$
 * @Last changed: $LastChangedDate$
 */

public class TestCase2 extends TestCase {

	/**
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp() 
	 */
	protected void setUp() throws Exception {
		System.out.println("TestCase2.setUp");
		super.setUp();
	}

	/**
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown() 
	 */
	protected void tearDown() throws Exception {
		System.out.println("TestCase2.tearDown");
		super.tearDown();
	}
	
	public void testCase2() {
		System.out.println("TestCase2.testCase2");
	}

}
