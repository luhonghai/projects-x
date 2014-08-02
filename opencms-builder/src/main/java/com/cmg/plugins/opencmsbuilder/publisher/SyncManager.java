/*
 * Copyright (c) 2014 CMG Ltd All rights reserved.
 *
 * This software is the confidential and proprietary information of CMG
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with CMG.
 */

package com.cmg.plugins.opencmsbuilder.publisher;

import com.cmg.plugins.opencmsbuilder.ApplicationConfiguration;
import com.cmg.plugins.opencmsbuilder.helper.CmsShellConnector;
import com.cmg.plugins.opencmsbuilder.helper.VFSConnector;
import com.cmg.plugins.opencmsbuilder.util.Logger;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Hai Lu
 */
public class SyncManager {
    private final ApplicationConfiguration configuration;
    private final String vfsSitesDefault;
    private final String vfsSystem;
    private final String warDir;
    private final String moduleName;
    private final File outputDir;
    private final File sourceDir;
    private final String syncTarget;
    private final File baseDir;
    private boolean isPublish = false;

    private VfsMappingGenerator generator;
    private List<VfsMappingObject> mappingObjects;
    private String moduleRoot;

    public SyncManager(ApplicationConfiguration configuration,
                       String vfsSitesDefault,
                       String vfsSystem,
                       String warDir,
                       String moduleName,
                       File baseDir,
                       File sourceDir,
                       File outputDir,
                       String syncTarget) {
        this.configuration = configuration;
        this.vfsSitesDefault = vfsSitesDefault;
        this.vfsSystem = vfsSystem;
        this.warDir = warDir;
        this.moduleName = moduleName;
        this.baseDir = baseDir;
        this.sourceDir = sourceDir;
        this.outputDir = outputDir;
        this.syncTarget = syncTarget;
    }

    public SyncManager(ApplicationConfiguration configuration,
                       String vfsSitesDefault,
                       String vfsSystem,
                       String warDir,
                       String moduleName,
                       File baseDir,
                       File sourceDir,
                       File outputDir,
                       String syncTarget,
                       boolean isPublish) {
        this(configuration, vfsSitesDefault, vfsSystem, warDir, moduleName,baseDir, sourceDir, outputDir, syncTarget);
        this.isPublish = isPublish;
    }

    public void execute() throws  MojoExecutionException {
        // sites/default is WebDav root
        Map<String, String> lookupList = new HashMap<String, String>();
        if (vfsSitesDefault != null && vfsSitesDefault.length() > 0)
            lookupList.put(vfsSitesDefault, "/");
        if (vfsSystem != null && vfsSystem.length() > 0)
            lookupList.put(vfsSystem, "/system/");
        if (moduleName != null && moduleName.length() > 0) {
            moduleRoot = "/system/modules/" + moduleName + "/";
            File classesRoot = new File(outputDir, "classes");
            if (classesRoot.exists() && classesRoot.isDirectory() && classesRoot.listFiles().length > 0) {
                lookupList.put(classesRoot.getAbsolutePath(), moduleRoot + "classes/");
            } else {
                Logger.getLogger().warn("No classes found. Have you compile module?");
            }
        } else {
            Logger.getLogger().warn("Missing property \"moduleName\". Plugin will skip sync all classes of module");
        }

        generator = new VfsMappingGenerator();
        try {
            if (syncTarget == null
                    || syncTarget.length() == 0
                    || syncTarget.equals(".") // Root module
                    ) {
                if (lookupList.size() > 0) {
                    mappingObjects = generator.generate(lookupList);
                } else {
                    Logger.getLogger().warn("Skip sync. Lookup list is empty");
                }
            } else {
                File selectedFile = new File(baseDir, syncTarget);
                Logger.getLogger().info("Source directory: " + sourceDir.getAbsolutePath());
                if (selectedFile.exists()) {
                    if (com.cmg.plugins.opencmsbuilder.util.FileUtils.isContain(vfsSitesDefault, selectedFile)) {
                        mappingObjects = generator.generate(new File(vfsSitesDefault), selectedFile, "/");
                    } else if (com.cmg.plugins.opencmsbuilder.util.FileUtils.isContain(vfsSystem, selectedFile)) {
                        mappingObjects = generator.generate(new File(vfsSystem), selectedFile, "/system/");
                    } else if (com.cmg.plugins.opencmsbuilder.util.FileUtils.isContain(sourceDir, selectedFile)) {

                    } else if (com.cmg.plugins.opencmsbuilder.util.FileUtils.isContain(warDir, selectedFile)) {

                    } else {

                    }
                } else {
                    throw new MojoExecutionException("Unable to process request", new FileNotFoundException("File " + selectedFile + " not found"));
                }
            }
            sync();
        } catch (IOException e) {
            throw new MojoExecutionException("Could not sync content", e);
        } finally {
            generator.close();
        }

    }

    private void sync() throws IOException {
        if (mappingObjects != null && mappingObjects.size() > 0) {
            VFSConnector connector = new VFSConnector(configuration);
            connector.open();
            if (moduleRoot != null && moduleRoot.length() > 0) {
                connector.mkdir(moduleRoot);
                connector.mkdir(moduleRoot + "classes");
            }
            for (VfsMappingObject mappingObject : mappingObjects) {
                connector.upload(mappingObject.getFile(), mappingObject.getDestination());
            }

            if (isPublish) {
                CmsShellConnector shellConnector = new CmsShellConnector(configuration);
                shellConnector.open();
                shellConnector.executeCommand(
                        "publishResources",
                        new String[] {generator.getPublishingList().getAbsolutePath()});
                shellConnector.close();
            }
        } else {
            Logger.getLogger().warn("Skip sync. No selected files found.");
        }
    }
}
