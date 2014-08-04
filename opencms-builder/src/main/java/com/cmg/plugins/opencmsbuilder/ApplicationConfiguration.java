/*
 * Copyright (c) 2014 CMG Ltd All rights reserved.
 *
 * This software is the confidential and proprietary information of CMG
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with CMG.
 */

package com.cmg.plugins.opencmsbuilder;

/**
 * @author Hai Lu
 */
public class ApplicationConfiguration {
    private final String WEBINF = "WEB-INF";

    private String username;
    private String password;
    private String baseURL;
    private String opencmsHome;
    private String servletMapping;
    private String defaultWebApp;
    private String moduleName;
    private String remoteServer;

    public String getRemoteServer() {
        if (remoteServer == null)
            remoteServer = "";
        return remoteServer;
    }

    public void setRemoteServer(String remoteServer) {
        this.remoteServer = remoteServer;
    }

    public String getWebInfPath() {
        return getOpencmsHome() + "/" + WEBINF;
    }



    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBaseURL() {
        if (baseURL == null)
            baseURL = "";
        if (baseURL.endsWith("/"))
            baseURL = baseURL.substring(0, baseURL.length() - 1);
        return baseURL;
    }

    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }

    public String getOpencmsHome() {
        if (opencmsHome == null)
            opencmsHome = "";
        if (opencmsHome.endsWith("/"))
            opencmsHome = opencmsHome.substring(0, opencmsHome.length() - 1);
        return opencmsHome;
    }

    public void setOpencmsHome(String opencmsHome) {
        this.opencmsHome = opencmsHome;
    }

    public String getServletMapping() {
        return servletMapping;
    }

    public void setServletMapping(String servletMapping) {
        this.servletMapping = servletMapping;
    }

    public String getDefaultWebApp() {
        return defaultWebApp;
    }

    public void setDefaultWebApp(String defaultWebApp) {
        this.defaultWebApp = defaultWebApp;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }
}
