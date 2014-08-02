/*
 * Copyright (c) 2014 CMG Ltd All rights reserved.
 *
 * This software is the confidential and proprietary information of CMG
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with CMG.
 */

package com.cmg.plugins.opencmsbuilder.helper;

import com.cmg.plugins.opencmsbuilder.ApplicationConfiguration;
import com.cmg.plugins.opencmsbuilder.command.CustomCommands;
import com.cmg.plugins.opencmsbuilder.remote.RemoteCommand;
import com.cmg.plugins.opencmsbuilder.util.Logger;
import com.cmg.plugins.opencmsbuilder.util.StringUtils;
import org.apache.commons.io.FileUtils;
import org.opencms.main.CmsShell;
import org.opencms.main.I_CmsShellCommands;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * @author Hai Lu
 */
public class CmsShellConnector {

    private final ApplicationConfiguration configuration;

    private CmsShell shell;

    private File tmpScriptDir;

    public CmsShellConnector(ApplicationConfiguration configuration) {
        this.configuration = configuration;
        init();
    }

    private void init() {
        tmpScriptDir = new File(com.cmg.plugins.opencmsbuilder.util.FileUtils.tmpDir(),"opencms-builder-" + UUID.randomUUID().toString());
        if (!tmpScriptDir.exists()|| !tmpScriptDir.isDirectory()) {
            tmpScriptDir.mkdirs();
        }
    }

    public void open() throws IOException {
        if (shell == null) {
            I_CmsShellCommands commands = new CustomCommands();
            shell = new CmsShell(
                    configuration.getWebInfPath(),
                    configuration.getServletMapping(),
                    configuration.getDefaultWebApp(),
                    "${user}@${project}:${siteroot}|${uri}>",
                    commands);
            Logger.getLogger().info("Login to OpenCMS system. Administration username: " + configuration.getUsername());
            executeCommand("login", new String[]{configuration.getUsername(), configuration.getPassword()});
        }
    }

    public String executeCommand(RemoteCommand remoteCommand) throws IOException {
        final String name = remoteCommand.getName();
        final String[] parameters = remoteCommand.getParameters();
        String log = "Execute command " + name;
        if (parameters.length == 0) {
            log += " with no parameter.";
        } else {
            for (int i = 0; i < parameters.length; i++) {
                log += ". Param " + (i + 1) + ": " + (remoteCommand.isPrivate() ? "*****" : parameters[i]);
            }
        }
        Logger.getLogger().info(log);
        executeCommand(name, parameters);
        return log;
    }

    public void executeCommand(String name, String[] parameters) throws IOException {
        File tmpScript = new File(tmpScriptDir, UUID.randomUUID().toString());
        FileUtils.write(tmpScript, name, "UTF-8", true);
        if (parameters != null && parameters.length > 0) {
            for (String param : parameters) {
                FileUtils.write(tmpScript, " \"" + StringUtils.escapeJavaString(param) + "\"", "UTF-8", true);
            }
        }
        FileInputStream fis  = null;
        try {
            fis = new FileInputStream(tmpScript);
            shell.start(fis);
        } catch (IOException ex) {
            throw ex;
        } finally {
            try {
                if (fis != null)
                    fis.close();
            } catch (IOException ex) {
                throw ex;
            }
            FileUtils.forceDelete(tmpScript);
        }
    }

    public void close() {
        try {
            if (shell != null)
                shell.exit();
        } catch (Exception ex) {
            Logger.getLogger().error("Could not exit shell", ex);
        }
        try {
            if (tmpScriptDir != null)
                FileUtils.deleteDirectory(tmpScriptDir);
        } catch (IOException e) {
            Logger.getLogger().error("Could not empty tmp directory " + tmpScriptDir, e);
        }
    }
}
