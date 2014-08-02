/*
 * Copyright (c) 2014 CMG Ltd All rights reserved.
 *
 * This software is the confidential and proprietary information of CMG
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with CMG.
 */

package com.cmg.plugins.opencmsbuilder.publisher;

import java.io.File;

/**
 * @author Hai Lu
 */
public class VfsMappingObject {

    private File file;

    private String destination;

    public String getVfsPath() {
        return vfsPath;
    }

    public void setVfsPath(String vfsPath) {
        this.vfsPath = vfsPath;
    }

    private String vfsPath;

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getDestination() {
        if (destination == null)
            destination = "";
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof VfsMappingObject)) {
            return false;
        }
        return this.getDestination().equals(((VfsMappingObject) obj).getDestination());
    }
}
