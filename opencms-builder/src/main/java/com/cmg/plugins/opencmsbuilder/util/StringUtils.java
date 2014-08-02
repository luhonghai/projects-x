/*
 * Copyright (c) 2014 CMG Ltd All rights reserved.
 *
 * This software is the confidential and proprietary information of CMG
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with CMG.
 */

package com.cmg.plugins.opencmsbuilder.util;

/**
 * @author Hai Lu
 */
public class StringUtils {

    public static String escapeJavaString(String source) {
        source = source.replace("\\", "\\\\");
        source = source.replace("\"", "\\\"");
        return source;
    }

    public static String trimDavPath(String source) {
        source = source.replace("\\", "/");
        if (source.startsWith("/"))
            source = source.substring(1, source.length());
        return source;
    }


}
