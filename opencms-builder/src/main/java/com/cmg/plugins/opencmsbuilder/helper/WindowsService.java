/*
 * Copyright (c) 2014 CMG Ltd All rights reserved.
 *
 * This software is the confidential and proprietary information of CMG
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with CMG.
 */

package com.cmg.plugins.opencmsbuilder.helper;

import com.cmg.plugins.opencmsbuilder.Constants;
import com.cmg.plugins.opencmsbuilder.util.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author Hai Lu
 */
public class WindowsService {

    private static final String RUNNING = "RUNNING";
    private static final String STOPPED = "STOPPED";

    private final String serviceName;
    private String[] script;
    private String output;

    public WindowsService(String serviceName) {
        this.serviceName = serviceName;
    }

    private boolean waitForStatus(String status) throws IOException, InterruptedException {
        int count = 0;
        while (true) {
            script = new String[]{"cmd.exe", "/c", "sc", "query", serviceName, "|", "find", "/C", "\"" + status + "\""};
            int code = execute();
            if (output.contains("1")) {
                Logger.getLogger().info("Service " + serviceName + " " + status);
                return true;
            }
            Logger.getLogger().info("waiting ...");
            Thread.sleep(1000);
            count++;
            if (count > Constants.SERVICE_WAITING_TIME) {
                Logger.getLogger().error("Waiting timeout!");
                return false;
            }
        }
    }

    public boolean isExits() throws IOException, InterruptedException {
        script = new String[]{"cmd.exe", "/c", "sc", "query", serviceName, "|", "find", "/C", "\"does not exist\""};
        execute();
        return output.contains("0");
    }

    public boolean isRunning() throws InterruptedException, IOException {
        //to check whether service is running or not
        script = new String[]{"cmd.exe", "/c", "sc", "query", serviceName, "|", "find", "/C", "\"RUNNING\""};
        int code = execute();
        if (code == 1) {
            return false;
        } else {
            return output.contains("1");
        }
    }

    public  boolean start() throws InterruptedException, IOException, ServiceNotFoundException {
        if (!isExits()) {
            throw  new ServiceNotFoundException(serviceName);
        }
        if (isRunning()) {
            Logger.getLogger().info("Service is running. Skip start call");
            return true;
        } else {
            Logger.getLogger().info("Start service " + serviceName);
            script = new String[]{"cmd.exe", "/c", "sc", "start", serviceName};
            int code = execute();
            if (waitForStatus(RUNNING)) {
                return code == 0;
            } else {
                return false;
            }
        }
    }

    public  boolean stop() throws InterruptedException, IOException, ServiceNotFoundException {
        if (!isExits()) {
            throw  new ServiceNotFoundException(serviceName);
        }
        if (isRunning()) {
            Logger.getLogger().info("Stop service " + serviceName);
            script = new String[]{"cmd.exe", "/c", "sc", "stop", serviceName};
            int code = execute();
            if (waitForStatus(STOPPED)) {
                return code == 0;
            } else {
                return false;
            }
        } else {
            Logger.getLogger().info("Service is not running. Skip stop call");
            return true;
        }
    }

    public  boolean restart() throws InterruptedException, IOException, ServiceNotFoundException {
        if (stop()) {
            return start();
        }
        return false;
    }

    private int execute() throws IOException, InterruptedException {
        Process proc = Runtime.getRuntime().exec(script);
        int code = proc.waitFor();
        String line;
        BufferedReader input = null;
        output = "";
        StringBuffer out = new StringBuffer();
        try {
            input = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            while ((line = input.readLine()) != null) {
                out.append(line).append("\n");
            }
            output = out.toString();
        } catch (IOException ex) {
            throw ex;
        } finally {
            try {
                input.close();
            } catch (IOException ex) {
                throw ex;
            }
        }
        return code;
    }

    public static class ServiceNotFoundException extends Exception {
        public ServiceNotFoundException(String serviceName) {
            super("Service " + serviceName + " not found");
        }
    }
}
