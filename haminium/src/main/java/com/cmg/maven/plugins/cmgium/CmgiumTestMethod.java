/**
 * Copyright (c) CMG Ltd All rights reserved.
 *
 * This software is the confidential and proprietary information of CMG
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with CMG.
 */

package com.cmg.maven.plugins.cmgium;

import java.util.List;

/** 
 * DOCME
 * 
 * @Creator Hai Lu
 * @author $Author$
 * @version $Revision$
 * @Last changed: $LastChangedDate$
 */

public class CmgiumTestMethod {
	public static final String TEST_METHOD_PREFIX = "test";
	
	private String name;
	
	private List<CmgiumMethod> methods;

	/** 
	 * @return the name 
	 */
	public String getName() {
		return name;
	}

	/** 
	 * @param name the name to set 
	 */
	
	public void setName(String name) {
		this.name = name;
	}

	/** 
	 * @return the methods 
	 */
	public List<CmgiumMethod> getMethods() {
		return methods;
	}

	/** 
	 * @param methods the methods to set 
	 */
	
	public void setMethods(List<CmgiumMethod> methods) {
		this.methods = methods;
	}
}
