/**
 * Copyright (c) CMG Ltd All rights reserved.
 *
 * This software is the confidential and proprietary information of CMG
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with CMG.
 */

package com.cmg.maven.plugins.cmgium;

import com.cmg.maven.plugins.cmgium.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/** 
 * DOCME
 * 
 * @Creator Hai Lu
 * @author $Author$
 * @version $Revision$
 * @Last changed: $LastChangedDate$
 */

public class CmgiumMethod {
	private String className;
	private String name;
	private List<String> params;
	
	public CmgiumMethod() {
		
	}
	
	public CmgiumMethod(String name, List<String> params) {
		this.name = name;
		this.params = params;
	}
	
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
	 * @return the params 
	 */
	public List<String> getParams() {
		if (params == null) 
			params = new ArrayList<String>();
		return params;
	}
	/** 
	 * @param params the params to set 
	 */
	
	public void setParams(List<String> params) {
		this.params = params;
	}
	
	public String toMethodString() {
		StringBuffer sb = new StringBuffer();
		sb.append(getName());
		sb.append("(");
		int paramSize = getParams().size();
		if (paramSize > 0) {
			for (int i = 0; i < paramSize; i++) {
				String param = params.get(i);
				sb.append("\"").append(StringUtils.escapeJavaString(param)).append("\"");
				if (i != params.size() - 1) {
					sb.append(", ");
				}
			}
			
		}
		sb.append(");");
		return sb.toString();
	}

	/** 
	 * @return the className 
	 */
	public String getClassName() {
		return className;
	}

	/** 
	 * @param className the className to set 
	 */
	
	public void setClassName(String className) {
		this.className = className;
	}

}
