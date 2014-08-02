/*
 * Copyright (c) 2014 CMG Ltd All rights reserved.
 *
 * This software is the confidential and proprietary information of CMG
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with CMG.
 */

package com.cmg.plugins.opencmsbuilder.publisher;

import com.cmg.plugins.opencmsbuilder.util.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Hai Lu
 */
public class VfsMapping {

    private final File target;

    private final File dir;

    private String root = "/";

    private String rootPath;

    private List<VfsMappingObject> mappings = new ArrayList<VfsMappingObject>();

    public VfsMapping(File dir, File target) {
       this.dir = dir;
       this.target = target;
    }

    public VfsMapping(File dir, File target, String root) {
        this(dir, target);
        this.root = root;
    }

    public void init() throws FileNotFoundException {
        if (dir == null || !dir.isDirectory()) {
            throw new FileNotFoundException(dir + " not found!");
        }
        rootPath = dir.getAbsolutePath();
        generate(target);
    }

    private void generate(File file) throws FileNotFoundException {
        String currentPath = file.getAbsolutePath();
        if ((!file.getAbsolutePath().equals(rootPath) && !mappings.contains(currentPath)) || !file.isDirectory()) {
            VfsMappingObject mapping = new VfsMappingObject();
            mapping.setDestination(root + StringUtils.trimDavPath(currentPath.substring(rootPath.length(), currentPath.length())));
            mapping.setVfsPath(root == "/" ? ("/sites/default" + mapping.getDestination()) : mapping.getDestination());
            mapping.setFile(file);
            mappings.add(mapping);
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files.length > 0)
                for (File f : files)
                    generate(f);
        }
    }

    public List<VfsMappingObject> getMappings() {
        return mappings;
    }
}
