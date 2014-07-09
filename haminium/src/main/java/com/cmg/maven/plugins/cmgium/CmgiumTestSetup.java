/**
 * Copyright (c) CMG Ltd All rights reserved.
 *
 * This software is the confidential and proprietary information of CMG
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with CMG.
 */

package com.cmg.maven.plugins.cmgium;

import com.cmg.maven.plugins.cmgium.exception.CmgiumException;

import junit.extensions.TestSetup;
import junit.framework.Test;

/**
 * DOCME
 * 
 * @Creator Hai Lu
 * @author $Author$
 * @version $Revision$
 * @Last changed: $LastChangedDate$
 */

public class CmgiumTestSetup extends TestSetup {

	/**
	 * TODO: document
	 * 
	 * @param test
	 */
	public CmgiumTestSetup(Test test) {
		super(test);
	}

	public void setUp() {
		try {
			Cmgium.setup();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (CmgiumException e) {
			e.printStackTrace();
		}
	}

	public void tearDown() {
		Cmgium.teardown();
	}

}
