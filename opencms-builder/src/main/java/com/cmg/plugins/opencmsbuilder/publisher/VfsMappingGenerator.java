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

    public List<VfsMappingObject> generate(List<SyncManager.LookupMapping> lookupList) throws IOException {
        List<VfsMappingObject> list = new ArrayList<VfsMappingObject>();
        for (SyncManager.LookupMapping lookupItem : lookupList) {
            if (lookupItem.isExist()) {
                List<VfsMappingObject> mappingObj = generate(lookupItem.getRoot(), lookupItem.getTarget(), lookupItem.getDestination());
                for (VfsMappingObject obj : mappingObj) {
                    if (lookupItem.getIgnoreExtensions().length > 0) {
                        boolean isValid = true;
                        for (String ext : lookupItem.getIgnoreExtensions()) {
                            if (!obj.getFile().isDirectory() && obj.getFile().getAbsolutePath().endsWith("." + ext)) {
                                isValid = false;
                                break;
                            }
                        }
                        if (isValid) {
                            if (!list.contains(obj))
                                list.add(obj);
                        }
                    } else {
                        if (!list.contains(obj))
                            list.add(obj);
                    }
                }
            } else {
                Logger.getLogger().warn("Directory not found! Skip looking for directory " + lookupItem.getTarget().getAbsolutePath());
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
