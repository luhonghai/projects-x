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
import com.cmg.plugins.opencmsbuilder.client.CommandRequester;
import com.cmg.plugins.opencmsbuilder.command.CustomCommands;
import com.cmg.plugins.opencmsbuilder.helper.CmsShellConnector;
import com.cmg.plugins.opencmsbuilder.helper.VFSConnector;
import com.cmg.plugins.opencmsbuilder.remote.RemoteCommand;
import com.cmg.plugins.opencmsbuilder.util.FileUtils;
import com.cmg.plugins.opencmsbuilder.util.Logger;
import com.google.gson.Gson;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Hai Lu
 */
public class SyncManager {

    public class LookupMapping {
        private File root;
        private File target;
        private String destination;
        private String[] ignoreExtensions;

        public LookupMapping(File root, File target, String destination) {
            this.root = root;
            this.target = target;
            this.destination = destination;
        }

        public LookupMapping(File root, File target, String destination,String[] ignoreExtensions) {
            this(root, target, destination);
            this.ignoreExtensions = ignoreExtensions;
        }

        public boolean isExist() {
            return root != null && root.exists() && target != null && target.exists();
        }

        public File getRoot() {
            return root;
        }

        public void setRoot(File root) {
            this.root = root;
        }

        public File getTarget() {
            return target;
        }

        public void setTarget(File target) {
            this.target = target;
        }

        public String getDestination() {
            return destination;
        }

        public void setDestination(String destination) {
            this.destination = destination;
        }

        public String[] getIgnoreExtensions() {
            if (ignoreExtensions == null)
                ignoreExtensions = new String[] {};
            return ignoreExtensions;
        }

        public void setIgnoreExtensions(String[] ignoreExtensions) {
            this.ignoreExtensions = ignoreExtensions;
        }
    }

    private final ApplicationConfiguration configuration;
    private final String vfsSitesDefault;
    private final String vfsSystem;
    private final String warDir;
    private final String moduleName;
    private final File outputDir;
    private final File sourceDir;
    private String syncTarget;
    private final File baseDir;
    private final List<Resource> resources;
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
                       String syncTarget,
                       List<Resource> resources) {
        this.configuration = configuration;
        this.vfsSitesDefault = vfsSitesDefault;
        this.vfsSystem = vfsSystem;
        this.warDir = warDir;
        this.moduleName = moduleName;
        this.baseDir = baseDir;
        this.sourceDir = sourceDir;
        this.outputDir = outputDir;
        this.syncTarget = syncTarget;
        this.resources = resources;
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
                       List<Resource> resources,
                       boolean isPublish) {
        this(configuration, vfsSitesDefault, vfsSystem, warDir, moduleName,baseDir, sourceDir, outputDir, syncTarget, resources);
        this.isPublish = isPublish;
    }

    public void execute() throws  MojoExecutionException {
        // sites/default is WebDav root
        List<LookupMapping> lookupList = new ArrayList<LookupMapping>();
        generator = new VfsMappingGenerator();
        try {
            File selectedFile= null;
            if (syncTarget == null || syncTarget.length() == 0 || syncTarget.equals(".")) {
                selectedFile = baseDir;
                Logger.getLogger().info("Looking up module root " + selectedFile.getAbsolutePath());
            } else {
                selectedFile = new File(baseDir, syncTarget);
                Logger.getLogger().info("Looking for target: " + selectedFile.getAbsolutePath());
            }
            File classesRoot = null;
            if (moduleName != null && moduleName.length() > 0) {
                moduleRoot = "/system/modules/" + moduleName + "/";
                classesRoot = new File(outputDir, "classes");
            } else {
                Logger.getLogger().warn("Missing property \"moduleName\". Plugin will skip sync all classes of module");
            }

            if (selectedFile.exists()) {
                if (com.cmg.plugins.opencmsbuilder.util.FileUtils.isContain(vfsSitesDefault, selectedFile)) {
                    lookupList.add(new LookupMapping(new File(vfsSitesDefault), selectedFile, "/"));
                } else if (com.cmg.plugins.opencmsbuilder.util.FileUtils.isContain(vfsSystem, selectedFile)) {
                    lookupList.add(new LookupMapping(new File(vfsSystem), selectedFile, "/system/"));
                } else if (com.cmg.plugins.opencmsbuilder.util.FileUtils.isContain(sourceDir, selectedFile)) {
                    if (classesRoot != null && classesRoot.exists() && classesRoot.isDirectory() && classesRoot.listFiles().length > 0) {
                        String subPath = selectedFile.getAbsolutePath().substring(sourceDir.getAbsolutePath().length(),
                                selectedFile.getAbsolutePath().length());
                        subPath = FileUtils.validatePath(subPath);
                        if (subPath.startsWith("/") && subPath.length() > 0) {
                            subPath = subPath.substring(1, subPath.length());
                        }
                        if (!selectedFile.isDirectory() && subPath.endsWith(".java")) {
                            subPath = subPath.substring(0, subPath.length() - 5) + ".class";
                        }
                        Logger.getLogger().info("Detect java source " + subPath);
                        lookupList.add(new LookupMapping(classesRoot, new File(classesRoot, subPath), moduleRoot + "classes/"));
                    } else {
                        Logger.getLogger().warn("No classes found. Have you compile module?");
                    }
                } else if (com.cmg.plugins.opencmsbuilder.util.FileUtils.isContain(warDir, selectedFile)) {

                } else {

                    if (com.cmg.plugins.opencmsbuilder.util.FileUtils.isContain(selectedFile, vfsSitesDefault)) {
                        lookupList.add(new LookupMapping(new File(vfsSitesDefault), new File(vfsSitesDefault), "/"));
                    }

                    if (com.cmg.plugins.opencmsbuilder.util.FileUtils.isContain(selectedFile, vfsSystem)) {
                        lookupList.add(new LookupMapping(new File(vfsSystem), new File(vfsSystem), "/system/"));
                    }

                    if (com.cmg.plugins.opencmsbuilder.util.FileUtils.isContain(selectedFile, sourceDir)
                            && moduleName != null && moduleName.length() > 0) {
                        if (classesRoot != null && classesRoot.exists() && classesRoot.isDirectory() && classesRoot.listFiles().length > 0) {
                            lookupList.add(new LookupMapping(classesRoot, classesRoot, moduleRoot + "classes/"));
                        } else {
                            Logger.getLogger().warn("No classes found. Have you compile module?");
                        }
                    }
                    if (resources.size() > 0 && classesRoot != null && classesRoot.exists() && classesRoot.isDirectory() && classesRoot.listFiles().length > 0) {
                        for (Resource res: resources) {
                            if (com.cmg.plugins.opencmsbuilder.util.FileUtils.isContain(selectedFile, res.getDirectory())
                                    && moduleName != null && moduleName.length() > 0) {
                                lookupList.add(new LookupMapping(classesRoot, classesRoot, moduleRoot + "classes/",
                                                                    new String[] {"class"}));
                            }
                            if (com.cmg.plugins.opencmsbuilder.util.FileUtils.isContain(res.getDirectory(), selectedFile)) {
                                String subPath = selectedFile.getAbsolutePath().substring(res.getDirectory().length(),
                                        selectedFile.getAbsolutePath().length());
                                subPath = FileUtils.validatePath(subPath);
                                if (subPath.startsWith("/") && subPath.length() > 0) {
                                    subPath = subPath.substring(1, subPath.length());
                                }
                                Logger.getLogger().info("Detect java resource " + subPath);
                                lookupList.add(new LookupMapping(classesRoot, new File(classesRoot, subPath), moduleRoot + "classes/",
                                        new String[] {"class"}));
                            }
                        }
                    }
                }
            } else {
                throw new MojoExecutionException("Unable to process request", new FileNotFoundException("File " + selectedFile + " not found"));
            }

            if (lookupList.size() > 0) {
                mappingObjects = generator.generate(lookupList);
                sync();
            } else {
                Logger.getLogger().warn("No lookup mapping found");
            }
        } catch (IOException e) {
            throw new MojoExecutionException("Could not sync content", e);
        } finally {
            generator.close();
        }

    }

    private void sync() throws IOException, MojoExecutionException {
        if (mappingObjects != null && mappingObjects.size() > 0) {
            Gson gson = new Gson();
            CustomCommands.CommandPackage commandPackage = new CustomCommands.CommandPackage();
            List<String> vfsResources = new ArrayList<String>();
            VFSConnector connector = new VFSConnector(configuration);
            connector.open();
            if (moduleRoot != null && moduleRoot.length() > 0) {
                connector.mkdir(moduleRoot);
                connector.mkdir(moduleRoot + "classes");
            }
            for (VfsMappingObject mappingObject : mappingObjects) {
                connector.upload(mappingObject.getFile(), mappingObject.getDestination());
                vfsResources.add(mappingObject.getVfsPath());
            }

            if (isPublish) {
                commandPackage.setList(vfsResources);
                if (configuration.getRemoteServer().length() > 0) {
                    CommandRequester requester = new CommandRequester(configuration);
                    RemoteCommand remoteCommand = new RemoteCommand();
                    remoteCommand.setName("publishResourcesList");
                    remoteCommand.setParameters(new String[]{gson.toJson(commandPackage)});
                    remoteCommand.setType(RemoteCommand.CMS_SHELL);
                    requester.addCommand(remoteCommand);
                    try {
                        requester.execute();
                    } catch (MojoExecutionException e) {
                        throw e;
                    }
                } else {
                    CmsShellConnector shellConnector = new CmsShellConnector(configuration);
                    shellConnector.open();
                    shellConnector.executeCommand(
                            "publishResourcesList",
                            new String[]{gson.toJson(commandPackage)});
                    shellConnector.close();
                }
            }
        } else {
            Logger.getLogger().warn("Skip sync. No selected files found.");
        }
    }
}
