/**
 * Copyright (c) CMG Ltd All rights reserved.
 *
 * This software is the confidential and proprietary information of CMG
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with CMG.
 */

package com.cmg.maven.plugins.cmgium.utils;

import java.io.IOException;
import java.util.Properties;

/** 
 * DOCME
 * 
 * @Creator Hai Lu
 * @author $Author$
 * @version $Revision$
 * @Last changed: $LastChangedDate$
 */

public class PropertiesHelper {
	public static final String DEFAULT_PROPERTIES = "cmgium.properties";
	
	private static Properties prop;
	
	public static Properties getProperties() {
		if (prop == null) {
            prop = new Properties();
			try {
                prop.load(PropertiesHelper.class.getClassLoader().getResourceAsStream(DEFAULT_PROPERTIES));
			} catch (IOException e) {				
				//
			}
		}
		return prop;
	}

    public static void updateProperties(Properties p) {
        prop = p;
    }
	
	public static String getKey(String key) {
		Object obj = getProperties().get(key);
		if (obj != null) 
			return obj.toString();
		return "";
	}
}
