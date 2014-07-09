/**
 * Copyright (c) CMG Ltd All rights reserved.
 *
 * This software is the confidential and proprietary information of CMG
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with CMG.
 */

package com.cmg.maven.plugins.cmgium.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.maven.plugin.MojoExecutionException;

import com.cmg.maven.plugins.cmgium.CmgiumMethod;

/** 
 * DOCME
 * 
 * @Creator Hai Lu
 * @author $Author$
 * @version $Revision$
 * @Last changed: $LastChangedDate$
 */

public class Validator {

    public static final String TESTSUITE_REGEX = "<td>[\\s\\n\\t]+<b>[\\s\\n\\t]+Test Suite[\\s\\n\\t]+</b>[\\s\\n\\t]+</td>";
	
	public static CmgiumMethod isValidMethod(Class clazz, CmgiumMethod method) {
        List<String> params = method.getParams();
        List<String> tmp = new ArrayList<String>();
        tmp.addAll(params);
        int size = tmp.size();

        boolean check = false;
        while (size >= 0) {
            try {
                if (size > 0) {
                    Class[] clazzs = new Class[size];
                    for (int i = 0; i < size; i++) {
                        clazzs[i] = String.class;
                    }
                    clazz.getMethod(method.getName(), clazzs);
                } else {
                    clazz.getMethod(method.getName());
                }
                method.setParams(tmp);
                return method;
            } catch (SecurityException e) {

            } catch (NoSuchMethodException e) {

            }
            size--;
            if (size >= 0) {
                tmp.remove(size);
            }
        }
        if (check) {
            return method;
        } else {
            return null;
        }
	}
	
	public static String validatePackage(String pk) {
		pk.trim();
		if (pk.startsWith("."))
			pk = pk.substring(1);
		if (pk.endsWith("."))
			pk = pk.substring(0, pk.length() - 1);
		return pk.toLowerCase();
	}
	
	public static boolean validateFolder(File folder) throws MojoExecutionException{
		if (!folder.exists()) {
			throw new MojoExecutionException("Could not found directory \"" + folder
					+ "\".");
		} else {
			return true;
		}
	}

	public static boolean validateFolder(String folder)  throws MojoExecutionException {
		return validateFolder(new File(folder));
	}

	public static boolean validateProperty(String prop, String propName)  throws MojoExecutionException  {
		if (prop == null || prop.length() == 0) {
			throw new MojoExecutionException("Could not found property \"" + propName + "\".");
		} else {
			return true;
		}
	}

    public static boolean isTestSuite(String source) {
        if (source.contains("Test Suite")) return true;

        return Pattern.compile(TESTSUITE_REGEX).matcher(source).find();
    }
}
