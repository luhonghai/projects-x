package com.cmg.maven.plugins.cmgium;

import com.cmg.maven.plugins.cmgium.utils.PropertiesHelper;
import com.cmg.maven.plugins.cmgium.utils.ScriptGenerator;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by Hai Lu on 26/05/2014.
 */

@Mojo(defaultPhase = LifecyclePhase.GENERATE_TEST_SOURCES, name = "init-plugin")
public class InitPluginMojo extends AbstractMojo {
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
    @Parameter(property = "testFunctionsFolder", defaultValue = "${project.basedir}\\functions")
    private String testFunctionsFolder;
    /**
     * The test script folder
     */
    @Parameter(property = "testScriptFolder", defaultValue = "${project.basedir}\\scripts")
    private String testScriptFolder;
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

    public void execute() throws MojoExecutionException, MojoFailureException {
        generateSeleniumProperties();
        File functionDir = new File(testFunctionsFolder);
        if (!functionDir.exists() || !functionDir.isDirectory()) {
            functionDir.mkdirs();
        }
        ScriptGenerator sg = new ScriptGenerator(outputDirectory,testSourceDirectory, functionDir, testPackage);
        try {
            sg.generate();
        } catch (Exception e) {
            throw new MojoExecutionException("Error when generate function folder", e);
        }
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
}
