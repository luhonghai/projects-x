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
import com.cmg.plugins.opencmsbuilder.util.Logger;
import com.github.sardine.DavResource;
import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Hai Lu
 */
public class VFSConnector {

    private final ApplicationConfiguration configuration;

    private Sardine sardine;

    private List<String> directories = new ArrayList<String>();

    public VFSConnector(ApplicationConfiguration configuration) {
        this.configuration = configuration;
    }

    public void open() throws IOException {
        sardine = SardineFactory.begin(configuration.getUsername(),configuration.getPassword());
        sardine.list(configuration.getBaseURL());
    }

    public void mkdir(String dir) throws IOException {
        String url = configuration.getBaseURL() + dir;
        if (!directories.contains(url)) {
            if (!sardine.exists(url)) {
                Logger.getLogger().info("Create directory " + url);
                sardine.createDirectory(url);
            }
            List<DavResource> resources = sardine.list(url);
            Logger.getLogger().info("Found dir " + dir + ". Children size: " + resources.size());
            directories.add(dir);
        }
    }

    public void upload(File file, String des) throws IOException {
        String url = configuration.getBaseURL() + des;
        if (sardine == null) {
            open();
        }
        if (file.exists()) {
            if (file.isDirectory()) {
                if (!directories.contains(des)) {
                    if (!sardine.exists(url)) {
                        Logger.getLogger().info("Create directory " + des);
                        sardine.createDirectory(url);
                    }
                    directories.add(des);
                }
            } else {
                InputStream is = null;
                try {
                    is = new FileInputStream(file);
                    Logger.getLogger().info("Upload file to " + des);
                    sardine.put(url, is);
                } catch (IOException ex) {
                    throw ex;
                } finally {
                    try {
                        if (is!=null)
                            is.close();
                    } catch (IOException ex) {
                        throw ex;
                    }
                }
            }
        }
    }


}
