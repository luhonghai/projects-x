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

package com.luhonghai.maven.plugins.haminium.old;

import com.luhonghai.maven.plugins.haminium.utils.StringUtils;

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
