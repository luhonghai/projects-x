/*
 * Copyright (c) 2014 CMG Ltd All rights reserved.
 *
 * This software is the confidential and proprietary information of CMG
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with CMG.
 */

package com.cmg.plugins.opencmsbuilder.remote;

import com.cmg.plugins.opencmsbuilder.ApplicationConfiguration;

/**
 * @author Hai Lu
 */
public class RemotePackage {

    private ApplicationConfiguration configuration;

    private RemoteCommand[] remoteCommands;

    public ApplicationConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(ApplicationConfiguration configuration) {
        this.configuration = configuration;
    }

    public RemoteCommand[] getRemoteCommands() {
        return remoteCommands;
    }

    public void setRemoteCommands(RemoteCommand[] remoteCommands) {
        this.remoteCommands = remoteCommands;
    }
}
