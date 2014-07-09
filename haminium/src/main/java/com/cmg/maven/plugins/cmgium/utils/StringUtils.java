package com.cmg.maven.plugins.cmgium.utils;

/**
 * Created by Hai Lu on 26/05/2014.
 */
public class StringUtils {

    public static String escapeJavaString(String source) {
        source = source.replace("\\", "\\\\");
        source = source.replace("\"", "\\\"");
        return source;
    }

    public static String upperCaseFirstChar(String source) {
        return source.substring(0,1).toUpperCase() + source.substring(1, source.length());
    }

    public static String lowerCaseFirstChar(String source) {
        return source.substring(0,1).toLowerCase() + source.substring(1, source.length());
    }

}
