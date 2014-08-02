/*
 * Copyright (c) 2014 CMG Ltd All rights reserved.
 *
 * This software is the confidential and proprietary information of CMG
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with CMG.
 */

package com.cmg.plugins.opencmsbuilder.util;

import org.apache.maven.plugin.logging.Log;

/**
 * @author Hai Lu
 */
public class Logger {
    private final Log log;

    private static Logger logger;

    public Logger(Log log) {
        this.log = log;
    }

    public static void setMojoLog(final Log log) {
        logger = new Logger(log);
    }

    public static Logger getLogger() {
        if (logger == null) {
            logger = new Logger(null);
        }
        return logger;
    }

    public void info(String message) {
        if (log != null) {
            log.info(message);
        } else {
            System.out.println("INFO: " + message);
        }
    }

    public void warn(String message) {
        if (log != null) {
            log.warn(message);
        } else {
            System.out.println("WARNING: " + message);
        }
    }

    public void error(String message) {
        if (log != null) {
            log.error(message);
        } else {
            System.out.println("ERROR: " + message);
        }
    }

    public void error(String message, Throwable throwable) {
        if (log != null) {
            log.error(message, throwable);
        } else {
            System.out.println("ERROR: " + message);
            throwable.printStackTrace();
        }
    }
}
