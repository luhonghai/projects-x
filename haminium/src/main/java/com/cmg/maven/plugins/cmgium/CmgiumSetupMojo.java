/**
 * Copyright (c) CMG Ltd All rights reserved.
 *
 * This software is the confidential and proprietary information of CMG
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with CMG.
 */

package com.cmg.maven.plugins.cmgium;

import java.util.Iterator;
import java.util.List;

import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;

/** 
 * DOCME
 * 
 * @Creator Hai Lu
 * @author $Author$
 * @version $Revision$
 * @Last changed: $LastChangedDate$
 */

@Mojo(defaultPhase = LifecyclePhase.TEST_COMPILE, name = "test")
public class CmgiumSetupMojo extends AbstractMojo  {
	
	@Parameter(defaultValue = "${project}", required = true)
	private MavenProject mavenProject;

	/**
	 * The webdriver class
	 */
	@Parameter(property = "webdriver", defaultValue = Cmgium.DEFAULT_WEBDRIVER_CLASS)
	private String webdriver;

	/**
	 * The base URL for testing
	 */
	@Parameter(property = "baseURL", required = true)
	private String baseURL;
	
	/**
	 * (non-Javadoc)
	 * @see org.apache.maven.plugin.Mojo#execute() 
	 */
	public void execute() throws MojoExecutionException, MojoFailureException {
		changeSurefirePluginConfiguration();
	}

	protected void changeSurefirePluginConfiguration()
			throws MojoExecutionException {

		Plugin surefirePlugin = lookupPlugin("org.apache.maven.plugins",
				"maven-surefire-plugin");
		if (surefirePlugin == null) {
			throw new MojoExecutionException(
					"Could not found maven-surefire-plugin");
		}
		getLog().info(
				"Found maven-surefire-plugin verion "
						+ surefirePlugin.getVersion());
		Xpp3Dom include = new Xpp3Dom("include");
		include.setValue("**/AllTests.java");

		Xpp3Dom configuration = (Xpp3Dom) surefirePlugin.getConfiguration();
		if (configuration == null) {
			getLog().info("Configuration not found. Create new one");
			configuration = new Xpp3Dom("configuration");
			Xpp3Dom includes = new Xpp3Dom("includes");
			includes.addChild(include);
			configuration.addChild(includes);
			surefirePlugin.setConfiguration(configuration);
		} else {
			getLog().info("Found configuration");
			Xpp3Dom includes = configuration.getChild("includes");
			if (includes == null) {
				getLog().info("Includes not found. Create new one");
				includes = new Xpp3Dom("includes");
				includes.addChild(include);
				configuration.addChild(includes);
			} else {
				Xpp3Dom sub = includes.getChild("include");
				if (sub == null) {
					includes.addChild(include);
				} else {
					getLog().info(
							"Current include properties is " + sub.getValue());
				}
			}
		}
	}

	private Plugin lookupPlugin(String groupId, String artifactId) {
		List plugins = mavenProject.getBuildPlugins();

		for (Iterator iterator = plugins.iterator(); iterator.hasNext();) {
			Plugin plugin = (Plugin) iterator.next();

			if (artifactId.equalsIgnoreCase(plugin.getArtifactId())
					&& groupId.equalsIgnoreCase(plugin.getGroupId()))
				return plugin;
		}
		return null;
	}
}
