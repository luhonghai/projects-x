/*
 * Copyright (c) 2014 CMG Ltd All rights reserved.
 *
 * This software is the confidential and proprietary information of CMG
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with CMG.
 */

package com.cmg.plugins.opencmsbuilder.mojo;

import com.cmg.plugins.opencmsbuilder.util.FileUtils;
import com.cmg.plugins.opencmsbuilder.util.Logger;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Hai Lu
 */
public class MojoCMG {
    public static void init(final org.apache.maven.plugin.logging.Log log) {
        Logger.getLogger().setMojoLog(log);

        log.info("------------------------------------------------------------------------");
        log.info("OpenCMS Builder");
        log.info("Author: Hai Lu");
        log.info("Copyright (c) 2014 CMG Ltd All rights reserved.");
        log.info("");
        log.info("This software is the confidential and proprietary information of CMG");
        log.info("(\"Confidential Information\"). You shall not disclose such Confidential");
        log.info("Information and shall use it only in accordance with the terms of the");
        log.info("init agreement you entered into with CMG.");
        log.info("------------------------------------------------------------------------");
    }



    public static void loadClassPath(final MavenProject mavenProject) {
        try {
            Set<URL> urls = new HashSet<URL>();
            List<String> elements = new ArrayList<String>();
            elements.addAll(mavenProject.getCompileClasspathElements());
            elements.addAll(mavenProject.getSystemClasspathElements());
            for (String element : elements) {
                urls.add(new File(element).toURI().toURL());
            }

            ClassLoader contextClassLoader = URLClassLoader.newInstance(
                    urls.toArray(new URL[0]),
                    Thread.currentThread().getContextClassLoader());

            Thread.currentThread().setContextClassLoader(contextClassLoader);

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (DependencyResolutionRequiredException e) {
            throw new RuntimeException(e);
        }
    }
}
