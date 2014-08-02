/*
 * Copyright (c) 2014 CMG Ltd All rights reserved.
 *
 * This software is the confidential and proprietary information of CMG
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with CMG.
 */

package com.cmg.plugins.opencmsbuilder.util;

import java.io.File;

/**
 * @author Hai Lu
 */
public class FileUtils {
    private static File _tmpDir;
    public static File tmpDir() {
        if (_tmpDir == null) {
            String tmpDir = System.getProperty("java.io.tmpdir");
            _tmpDir = new File(tmpDir);

            if (!_tmpDir.exists() || !_tmpDir.isDirectory()) {
                _tmpDir.mkdirs();
            }
        }
        return _tmpDir;
    }

    public static String validatePath(String path) {
        path = path.replace("\\", "/");
        if (path.endsWith("/"))
            path = path.substring(0, path.length() - 1);
        return path;
    }

    public static boolean isContain(File folder, File target) {
        if (folder == null || !folder.exists() || target == null || !target.exists()) {
            return false;
        }
        return target.getAbsolutePath().contains(folder.getAbsolutePath());
    }

    public static boolean isContain(String folder, File target) {
       return isContain(new File(folder), target);
    }

    public static boolean isContain(String folder, String target) {
        return isContain(new File(folder), new File(target));
    }
}
