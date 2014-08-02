/*
 * Copyright (c) 2014 CMG Ltd All rights reserved.
 *
 * This software is the confidential and proprietary information of CMG
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with CMG.
 */

package com.cmg.plugins.opencmsbuilder.remote;

/**
 * @author Hai Lu
 */
public class RemoteCommand {
    public static final int CMS_SHELL = 1;
    public static final int WINDOWS_SERVICE = 2;

    private int type = CMS_SHELL;

    private String name;

    private String[] parameters;
    // This will hide parameters in log
    private boolean isPrivate = false;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getParameters() {
        if (parameters == null) {
            parameters = new String[] {};
        }
        return parameters;
    }

    public void setParameters(String[] parameters) {
        this.parameters = parameters;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
