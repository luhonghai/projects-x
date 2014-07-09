/**
 * Copyright (c) CMG Ltd All rights reserved.
 *
 * This software is the confidential and proprietary information of CMG
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with CMG.
 */

package com.cmg.maven.plugins.cmgium;

import java.io.File;
import java.io.IOException;
import java.util.*;

import com.cmg.maven.plugins.cmgium.utils.*;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;


/**
 * Goal which generates test script
 *
 * @author Hai Lu
 */
@Mojo(defaultPhase = LifecyclePhase.GENERATE_TEST_SOURCES, name = "init")
public class TestScriptMojo extends AbstractMojo {

    @Parameter(defaultValue = "${session}", required = true, readonly = true)
    private MavenSession mavenSession;

    @Component
    private BuildPluginManager pluginManager;

    @Parameter(defaultValue = "${project}", required = true)
    private MavenProject mavenProject;

    @Parameter(property = "project.build.sourceDirectory", required = true)
    private final File sourceDirectory = new File("");

    @Parameter(property = "project.build.directory", required = true)
    private final File outputDirectory = new File("");

    @Parameter(property = "project.build.testSourceDirectory", required = true)
    private final File testSourceDirectory = new File("");

    @Parameter(property = "encoding", defaultValue = "${project.build.sourceEncoding}")
    private final String encoding = "UTF-8";

    /**
     * The test script folder
     */
    @Parameter(property = "testScriptFolder", defaultValue = "${project.basedir}\\scripts")
    private String testScriptFolder;

    /**
     * The test script folder
     */
    @Parameter(property = "testFunctionsFolder", defaultValue = "${project.basedir}\\functions")
    private String testFunctionsFolder;
    /**
     * The test script package
     */
    @Parameter(property = "testPackage", defaultValue = Cmgium.DEFAULT_TEST_PACKAGE)
    private String testPackage;

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

    private Map<String, List<String>> classMapping = new HashMap<String, List<String>>();

    public void execute() throws MojoExecutionException {
        getLog().info("baseURL: " + (baseURL == null ? "null" : baseURL));
        getLog().info("webdriver: " + (webdriver == null ? "null" : webdriver));
        getLog().info(
                "outputDirectory: "
                        + (outputDirectory == null ? "null" : outputDirectory
                        .getAbsolutePath()));
        getLog().info(
                "sourceDirectory: "
                        + (sourceDirectory == null ? "null" : sourceDirectory
                        .getAbsolutePath()));
        getLog().info(
                "testSourceDirectory: "
                        + (testSourceDirectory == null ? "null"
                        : testSourceDirectory.getAbsolutePath()));
        getLog().info(
                "testPackage: " + (testPackage == null ? "null" : testPackage));
        getLog().info(
                "testScriptFolder: "
                        + (testScriptFolder == null ? "null" : testScriptFolder));

        generateSeleniumProperties();
        generateSeleniumDriver();
        writeProperties();

        Validator.validateProperty(webdriver, "webdriver");
        Validator.validateProperty(baseURL, "baseURL");
        generateSeleniumProperties();
        Validator.validateProperty(testScriptFolder, "testScriptFolder");
        Validator.validateFolder(testScriptFolder);
        Validator.validateFolder(testSourceDirectory);
        Validator.validateProperty(testPackage, "testPackage");

        try {
            org.apache.commons.io.FileUtils
                    .deleteDirectory(testSourceDirectory);
        } catch (IOException e) {
            throw new MojoExecutionException("Error when delete directory "
                    + testSourceDirectory, e);
        }
        if (!testSourceDirectory.exists()) {
            testSourceDirectory.mkdirs();
        }
        initTestScript(new File(testScriptFolder));


        checkSurefirePluginConfiguration();
    }

    protected void checkSurefirePluginConfiguration()
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
        boolean valid = false;

        Xpp3Dom include = new Xpp3Dom("include");
        include.setValue("**/AllTests.java");

        Xpp3Dom configuration = (Xpp3Dom) surefirePlugin.getConfiguration();
        if (configuration == null) {
            getLog().error(
                    "Configuration not found. Please create new one. Example: \n");
            configuration = new Xpp3Dom("configuration");
            Xpp3Dom includes = new Xpp3Dom("includes");
            includes.addChild(include);
            configuration.addChild(includes);
            surefirePlugin.setConfiguration(configuration);
        } else {
            getLog().info("Found configuration");
            Xpp3Dom includes = configuration.getChild("includes");
            if (includes == null) {
                getLog().error(
                        "Includes not found. Please create new one. Example: \n");
                includes = new Xpp3Dom("includes");
                includes.addChild(include);
                configuration.addChild(includes);
            } else {
                Xpp3Dom sub = includes.getChild("include");
                if (sub == null) {
                    getLog().error(
                            "Include not found. Please create new one. Example: \n");
                    includes.addChild(include);
                } else {
                    valid = true;
                    getLog().info(
                            "Current include properties is " + sub.getValue());
                }
            }
        }
        if (!valid) {
            String temp = "";
            try {
                temp = Generator.generate(Generator.SUREFIRE_PLUGIN_TEMPLATE,
                        null);
                getLog().error(temp);
            } catch (IOException e) {

            }
            throw new MojoExecutionException(
                    "Please add maven-surefire-plugin configuration include **/**TestSuite.java . Example: \n"
                            + temp);
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

    protected  void writeProperties() throws MojoExecutionException {
        File resourceFolder = new File(mavenProject.getBasedir(),
                "src/test/resources");
        if (!resourceFolder.exists() || !resourceFolder.isDirectory()) {
            resourceFolder.mkdirs();
        }
        FileUtils.writeProperties(new File(resourceFolder,
                PropertiesHelper.DEFAULT_PROPERTIES), PropertiesHelper.getProperties());
    }

    protected void generateSeleniumProperties() throws MojoExecutionException {
        StringBuffer bf = new StringBuffer();
        Properties prod = mavenProject.getProperties();
        prod.setProperty(Cmgium.PROP_BASE_URL, baseURL);
        prod.setProperty(Cmgium.PROP_WEBDRIVER_CLASS, webdriver);
        prod.setProperty(Cmgium.PROP_PROJECT_BASE_DIR, mavenProject.getBasedir().getAbsolutePath());
        prod.setProperty(Cmgium.PROP_PROJECT_BUILD_DIR, outputDirectory.getAbsolutePath());
        prod.setProperty(Cmgium.PROP_TEST_PACKAGE, testPackage);
        PropertiesHelper.updateProperties(prod);

    }

    protected void initTestScript(File f) throws MojoExecutionException {
        if (f.isDirectory()) {
            File[] files = f.listFiles();
            if (files.length == 0) {
                getLog().info("Directory " + f + " is empty.");
            } else {
                for (File fChid : files) {
                    if (fChid.isDirectory()) {
                        initTestScript(fChid);
                    } else {
                        try {
                            generateTestSource(fChid);
                        } catch (Exception e) {
                            e.printStackTrace();
                            throw  new MojoExecutionException("Could not generate test script. Message: " + e.getMessage());
                        }
                    }
                }
            }
        }
    }

    protected void generateTestSource(File f) throws MojoExecutionException, IOException {
        String filePath = f.getAbsolutePath();
        String fileName = f.getName();
        String folderPath = filePath.substring(0,
                filePath.lastIndexOf(fileName));
        getLog().info(
                "Generate test script for " + fileName + " in directory "
                        + folderPath);
        folderPath = folderPath.replaceAll("\\\\", "\\/");
        String scriptPath = testScriptFolder.replaceAll("\\\\", "\\/");
        String subPackage = "";
        if (folderPath.equals(scriptPath)) {
            subPackage = Validator.validatePackage(testPackage);
        } else if (folderPath.contains(scriptPath)) {
            subPackage = folderPath.substring(scriptPath.length());
            subPackage = subPackage.replaceAll("\\/", ".");
            subPackage = Validator.validatePackage(Validator
                    .validatePackage(testPackage)
                    + "."
                    + Validator.validatePackage(subPackage));
        }

        if (subPackage == "")
            throw new MojoExecutionException(
                    "Could not init sub package for file " + f);

        String source = FileUtils.readFile(f, encoding);

        getLog().info("Complete read file source " + f);
        if (source == null || source.trim().length() == 0) {
            getLog().info("Skip empty script file " + f);
            return;
        }
        if (Validator.isTestSuite(source)) {
            generateTestSuite(f, fileName, subPackage);
        } else {
            generateTestCase(f, fileName, subPackage);
        }

    }

    protected void writeTestClass(String className, String output, String subPackage) throws MojoExecutionException {
        String subPackageFolderPath = generatePackageDir(subPackage);
        File subPackageFolder = new File(subPackageFolderPath);
        if (!subPackageFolder.exists() || !subPackageFolder.isDirectory()) {
            subPackageFolder.mkdirs();
            getLog().info(
                    "Create directory " + subPackageFolderPath
                            + " for test package " + subPackage);
        } else {
            getLog().info(
                    "Directory " + subPackageFolderPath
                            + " is existed. Skip mkdirs for test package "
                            + subPackage);
        }
        String outputPath = subPackageFolderPath + File.separator
                + className + Common.EXTENSION_JAVA_FILE;
        FileUtils.writeFile(outputPath, encoding, output);
        getLog().info("Complete write file " + outputPath);
    }

    protected void generateTestCase(File source, String fileName, String subPackage) throws MojoExecutionException, IOException {
        List<CmgiumTestMethod> testMethods;
        try {
            testMethods = Generator.parseTestMethod(source);
            getLog().info("Complete parse test case");
        } catch (Exception e) {
            throw new MojoExecutionException("Error when parse test case", e);
        }
        String[] tmpSplit = fileName.split("\\.");
        fileName = tmpSplit[0];
        Map<String, String> params = new HashMap<String, String>();
        params.put(Generator.KEY_CLASS_NAME, fileName);
        params.put(Generator.KEY_PACKAGE_NAME, subPackage);

        if (testMethods != null) {
            params.put(Generator.KEY_TEST_METHODS,
                    Generator.generateTestMethod(testMethods));
            getLog().info("Complete generate parameters");
        }
        try {
            String output = Generator.generate(Generator.TEST_CASE_TEMPLATE,
                    params);

            getLog().info("Complete generate test case template");

            writeTestClass(fileName, output, subPackage);

        } catch (IOException e) {
            throw new MojoExecutionException("Error when generate script file",
                    e);
        }
    }

    protected void generateTestSuite(File source, String fileName, String subPackage) throws MojoExecutionException {

        List<String> classes;
        try {
            classes = Generator.parseTestSuite(subPackage, source);
        } catch (Exception e) {
            throw new MojoExecutionException("Error when parse test suite", e);
        }
        if (classes == null|| classes.isEmpty())
            return;
        String[] tmpSplit = fileName.split("\\.");
        fileName = tmpSplit[0] + "TestSuite";
        Map<String, String> params = new HashMap<String, String>();
        params.put(Generator.KEY_CLASS_NAME, fileName);
        params.put(Generator.KEY_PACKAGE_NAME, subPackage);
        params.put(Generator.KEY_ADD_TEST_SUITE,
                Generator.generateAddTestSuiteMethod(classes));
        try {
            String output = Generator.generate(
                    Generator.TEST_SUITE_TEMPLATE, params);

            writeTestClass(fileName, output, subPackage);
        } catch (IOException e) {
            throw new MojoExecutionException(
                    "Error when write "  + fileName + ".java of package "
                            + subPackage, e);
        }

    }

    protected String generatePackageDir(String pk) {
        String[] packages = pk.split("\\.");
        StringBuffer bf = new StringBuffer(
                testSourceDirectory.getAbsolutePath());
        for (String p : packages) {
            bf.append(File.separator).append(p);
        }
        return bf.toString();
    }

    protected void generateSeleniumDriver() {
        File driverFolder = new File(outputDirectory, "drivers");
        if (!driverFolder.exists() || !driverFolder.isDirectory()) {
            driverFolder.mkdirs();
        }

        if (!PropertiesHelper.getProperties().containsKey(Cmgium.PROP_CHROME_WEBDRIVER)) {
            File desFile = new File(driverFolder, "chromedriver.exe");
            if (!desFile.exists()) {
                FileUtils.writeEmbeddedResourceToLocalFile("drivers/chromedriver.exe", desFile);
            }
            PropertiesHelper.getProperties().setProperty(Cmgium.PROP_CHROME_WEBDRIVER, desFile.getAbsolutePath());
        }

        if (!PropertiesHelper.getProperties().containsKey(Cmgium.PROP_IE_WEBDRIVER)) {
            File desFile = new File(driverFolder, "IEDriverServer.exe");
            if (!desFile.exists()) {
                FileUtils.writeEmbeddedResourceToLocalFile("drivers/IEDriverServer.exe", desFile);
            }
            PropertiesHelper.getProperties().setProperty(Cmgium.PROP_IE_WEBDRIVER, desFile.getAbsolutePath());
        }
    }

}
