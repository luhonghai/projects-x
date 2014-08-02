/*
 * Copyright (c) 2014 CMG Ltd All rights reserved.
 *
 * This software is the confidential and proprietary information of CMG
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with CMG.
 */

package com.cmg.plugins.opencmsbuilder.mojo;

import com.cmg.plugins.opencmsbuilder.remote.RemoteServer;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * @author Hai Lu
 */

@Mojo(name = "remote-server")
public class StartRemoteServer extends AbstractMojo {

    @Parameter(property = "remotePort", required = true)
    private int remotePort = 77777;

    public void execute() throws MojoExecutionException, MojoFailureException {
        MojoCMG.init(getLog());
        RemoteServer server = new RemoteServer(remotePort);
        try {
            server.start();
        } catch (Exception e) {
            throw new MojoExecutionException("Could not start remote server", e);
        }
    }
}
