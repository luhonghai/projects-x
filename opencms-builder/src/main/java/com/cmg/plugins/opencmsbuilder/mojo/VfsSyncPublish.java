/*
 * Copyright (c) 2014 CMG Ltd All rights reserved.
 *
 * This software is the confidential and proprietary information of CMG
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with CMG.
 */

package com.cmg.plugins.opencmsbuilder.mojo;

import com.cmg.plugins.opencmsbuilder.ApplicationConfiguration;
import com.cmg.plugins.opencmsbuilder.publisher.SyncManager;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;

/**
 * @author Hai Lu
 */

@Mojo(name = "vfs-sync-publish")
public class VfsSyncPublish extends AbstractMojo {

    @Parameter(defaultValue = "${session}", required = true, readonly = true)
    private MavenSession mavenSession;

    @Component
    private BuildPluginManager pluginManager;

    @Parameter(defaultValue = "${project}", required = true)
    private MavenProject mavenProject;

    @Parameter(property = "project.build.sourceDirectory", required = true)
    private final File sourceDirectory = new File("");

    @Parameter(property = "project.build.directory", required = true)
    private final File outputDirectory = new File("");

    @Parameter(property = "project.build.testSourceDirectory", required = true)
    private final File testSourceDirectory = new File("");

    @Parameter(property = "encoding", defaultValue = "${project.build.sourceEncoding}")
    private final String encoding = "UTF-8";

    @Parameter(property = "webdavBaseUrl", required = true)
    private String webdavBaseUrl;

    @Parameter(property = "adminUsername", required = true)
    private String adminUsername;

    @Parameter(property = "adminPassword", required = true)
    private String adminPassword;

    @Parameter(property = "opencmsHome", required = true)
    private String opencmsHome;

    @Parameter(property = "vfsSitesDefaut", defaultValue = "${project.basedir}/vfs/sites/default")
    private String vfsSitesDefaut;

    @Parameter(property = "vfsSystem", defaultValue = "${project.basedir}/vfs/system")
    private String vfsSystem;

    @Parameter(property = "moduleName")
    private String moduleName;

    @Parameter(property = "warDir")
    private String warDir;

    @Parameter(property = "syncTarget")
    private String syncTarget;

    @Parameter(property = "remoteServer")
    private String remoteServer;

    public void execute() throws MojoExecutionException, MojoFailureException {
        MojoCMG.init(getLog());
        ApplicationConfiguration configuration = new ApplicationConfiguration();
        configuration.setRemoteServer(remoteServer);
        configuration.setBaseURL(webdavBaseUrl);
        configuration.setUsername(adminUsername);
        configuration.setPassword(adminPassword);
        configuration.setOpencmsHome(opencmsHome);
        getLog().info("WebDav URL: " + webdavBaseUrl);
        getLog().info("Use Admin account: " + adminUsername);
        getLog().info("OpenCMS Home: " + opencmsHome);
        SyncManager syncManager = new SyncManager(configuration,
                vfsSitesDefaut, vfsSystem, warDir, moduleName,
                mavenProject.getBasedir(), sourceDirectory, outputDirectory,
                syncTarget,mavenProject.getBuild().getResources(), true);
        syncManager.execute();

    }


}

