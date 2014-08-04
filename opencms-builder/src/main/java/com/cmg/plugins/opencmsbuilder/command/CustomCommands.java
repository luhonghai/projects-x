/*
 * Copyright (c) 2014 CMG Ltd All rights reserved.
 *
 * This software is the confidential and proprietary information of CMG
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with CMG.
 */

package com.cmg.plugins.opencmsbuilder.command;

import com.cmg.plugins.opencmsbuilder.helper.OpenCmsPublisher;
import com.cmg.plugins.opencmsbuilder.util.Logger;
import com.google.gson.Gson;
import org.opencms.file.CmsObject;
import org.opencms.main.CmsShell;
import org.opencms.main.I_CmsShellCommands;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Hai Lu
 */
public class CustomCommands implements I_CmsShellCommands {

    private CmsObject object;
    private CmsShell shell;

    public void initShellCmsObject(CmsObject cmsObject, CmsShell cmsShell) {
        this.object = cmsObject;
        this.shell = cmsShell;
    }

    public void publishResourcesList(String dataPackage) {
        Gson gson = new Gson();
        CommandPackage commandPackage = gson.fromJson(dataPackage, CommandPackage.class);
        if (commandPackage != null && commandPackage.getList().size() > 0) {
            OpenCmsPublisher.publishResources(commandPackage.getList(), object);
        }
    }

    public void publishResources(String filePath) {
        InputStream fis = null;
        BufferedReader br = null;
        String line;
        List<String> resourceList = new ArrayList<String>();
        try {
            fis = new FileInputStream(filePath);
            br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));
            while ((line = br.readLine()) != null) {
                resourceList.add(line.trim());
            }
            Logger.getLogger().info("Start publishing resources. Length: " + resourceList.size());
            if (resourceList.size() > 0) {
                OpenCmsPublisher.publishResources(resourceList, object);
            }
        } catch (Exception ex) {
            Logger.getLogger().error("Could not publishing resources", ex);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    Logger.getLogger().error("Could not close reader", e);
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    Logger.getLogger().error("Could not close stream", e);
                }
            }
        }
    }

    public void publishResource(String resource) {
        List<String> resources = new ArrayList<String>();
        resources.add(resource);
        OpenCmsPublisher.publishResources(resources, object);
    }

    public void shellExit() {

    }

    public void shellStart() {

    }

    static public class CommandPackage {
        private List<String> list;

        public List<String> getList() {
            if (list == null) {
                list = new ArrayList<String>();
            }
            return list;
        }

        public void setList(List<String> list) {
            this.list = list;
        }
    }
}
