/*
 * Copyright (c) 2014 CMG Ltd All rights reserved.
 *
 * This software is the confidential and proprietary information of CMG
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with CMG.
 */

package com.cmg.plugins.opencmsbuilder.command;

import com.cmg.plugins.opencmsbuilder.client.CommandRequester;
import com.cmg.plugins.opencmsbuilder.helper.WindowsService;
import com.cmg.plugins.opencmsbuilder.remote.RemoteCommand;
import com.cmg.plugins.opencmsbuilder.util.Logger;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.IOException;

/**
 * @author Hai Lu
 */
public class ServiceCommand {
    public static final String START = "start";
    public static final String STOP = "stop";
    public static final String RESTART = "restart";

    private final String type;
    private final String serviceName;
    private final String remoteServer;

    public ServiceCommand(String type, String serviceName, String remoteServer) {
        this.type = type;
        this.serviceName = serviceName;
        this.remoteServer = remoteServer;
    }

    public void execute() throws MojoFailureException, MojoExecutionException {
        Logger.getLogger().info(type + " service " + serviceName + " ...");
        try {
            if (remoteServer != null && remoteServer.length() > 0) {
                CommandRequester requestor = new CommandRequester(null);
                RemoteCommand command = new RemoteCommand();
                command.setName(type);
                command.setParameters(new String[] {serviceName});
                requestor.addCommand(command);
                requestor.execute();
            } else {
                WindowsService ws = new WindowsService(serviceName);

                boolean status = ws.restart();
                if (status) {
                    Logger.getLogger().info("Completed");
                } else {
                    throw new MojoExecutionException("Could not " + type + " service!");
                }
            }
        } catch (InterruptedException e) {
            throw new MojoFailureException("Could not "  + type + " service " + serviceName, e);
        } catch (IOException e) {
            throw new MojoFailureException("Could not " + type + " service " + serviceName, e);
        } catch (WindowsService.ServiceNotFoundException e) {
            throw new MojoFailureException("Could not " + type + " service " + serviceName, e);
        }
    }
}
