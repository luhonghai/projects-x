/*
 * Copyright (c) 2014 CMG Ltd All rights reserved.
 *
 * This software is the confidential and proprietary information of CMG
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with CMG.
 */

package com.cmg.plugins.opencmsbuilder.mojo;

import com.cmg.plugins.opencmsbuilder.command.ServiceCommand;
import com.cmg.plugins.opencmsbuilder.helper.WindowsService;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.IOException;

/**
 * @author Hai Lu
 */

@Mojo(name = "start-service")
public class StartService  extends AbstractMojo {

    @Parameter(defaultValue = "${project}", required = true)
    private MavenProject mavenProject;

    @Parameter(property = "serviceName", required = true)
    private String serviceName;

    @Parameter(property = "remoteServer")
    private String remoteServer;

    public void execute() throws MojoExecutionException, MojoFailureException {
        MojoCMG.init(getLog());
        ServiceCommand command = new ServiceCommand(ServiceCommand.START, serviceName, remoteServer);
        command.execute();
    }
}
