/*
 * Copyright (c) 2014 CMG Ltd All rights reserved.
 *
 * This software is the confidential and proprietary information of CMG
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with CMG.
 */

package com.cmg.plugins.opencmsbuilder.publisher;

import com.cmg.plugins.opencmsbuilder.util.FileUtils;
import com.cmg.plugins.opencmsbuilder.util.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * @author Hai Lu
 */
public class VfsMappingGenerator {
    private File publishingList;

    public VfsMappingGenerator() {
    }

    public List<VfsMappingObject> generate(Map<String, String> lookupList) throws IOException {

        List<VfsMappingObject> list = new ArrayList<VfsMappingObject>();
        Iterator<String> keys = lookupList.keySet().iterator();
        while (keys.hasNext()) {
            String lookupDir = keys.next();
            String dest = lookupList.get(lookupDir);
            VfsMapping mapping = new VfsMapping(new File(lookupDir), new File(lookupDir) , dest);
            Logger.getLogger().info("Looking for sync directory: " + lookupDir + ". WebDav destination: " + dest);
            try {
                mapping.init();
                for (VfsMappingObject obj : mapping.getMappings()) {
                    if (!list.contains(obj)) {
                        list.add(obj);
                        org.apache.commons.io.FileUtils.write(getPublishingList(), obj.getVfsPath() + "\n", "UTF-8", true);
                    }
                }
            } catch (FileNotFoundException e) {
                Logger.getLogger().warn("Skip sync directory " + lookupDir + ". Directory not found.");
            }
        }
        return list;
    }

    public List<VfsMappingObject> generate(File rootDir, File target, String dest) throws IOException {
        VfsMapping mapping = new VfsMapping(rootDir, target , dest);
        mapping.init();
        for (VfsMappingObject obj : mapping.getMappings()) {
            org.apache.commons.io.FileUtils.write(getPublishingList(), obj.getVfsPath() + "\n", "UTF-8", true);
        }
        return mapping.getMappings();
    }

    public File getPublishingList() {
        if (publishingList == null) {
            publishingList = new File(FileUtils.tmpDir(),"publishing-list-" + UUID.randomUUID().toString());
        }
        return publishingList;
    }

    public void close() {
        try {
            if (publishingList != null && publishingList.exists())
                org.apache.commons.io.FileUtils.forceDelete(getPublishingList());
        } catch (IOException e) {
            Logger.getLogger().error("Could not delete publishing list " + publishingList, e);
        }
    }
}
