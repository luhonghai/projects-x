package com.cmg.maven.plugins.cmgium.utils;

import com.cmg.maven.plugins.cmgium.CmgiumMethod;
import com.cmg.maven.plugins.cmgium.CmgiumTestMethod;
import com.cmg.maven.plugins.cmgium.exception.CmgiumException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

/**
 * Created by Hai Lu on 03/06/2014.
 */

public class ScriptGenerator {
    private static final String USER_EXTENSIONS = "user-extensions.js";

    private final File buildDir;
    private final File testSourceDir;
    private final File functionsDir;
    private final String testPackage;
    private final File userExt;


    public ScriptGenerator(File buildDir,
                           File testSourceDir,
                           File functionsDir,
                           String testPackage) {
        this.buildDir = buildDir;
        this.testSourceDir = testSourceDir;
        this.functionsDir = functionsDir;
        this.testPackage = testPackage;
        if (this.buildDir != null) {
            if (!buildDir.exists() || !buildDir.isDirectory()) {
                buildDir.mkdirs();
            }
            if (!testSourceDir.exists() || !testSourceDir.isDirectory()) {
                testSourceDir.mkdirs();
            }
            userExt = new File(buildDir, USER_EXTENSIONS);
        } else {
            userExt = null;
        }
    }

    public void generate() throws IOException, SAXException, ParserConfigurationException, CmgiumException {
        recycle();
        appendScript(generateCmgiumProperties());
        appendScript(FileUtils.readResourceContent(USER_EXTENSIONS));
        if (functionsDir != null && functionsDir.exists() && functionsDir.isDirectory()) {
            File[] list = functionsDir.listFiles();
            if (list != null && list.length > 0) {
                for (File f : list) {
                    if (!f.isDirectory()) {
                        List<CmgiumTestMethod> testMethods = Generator.parseTestMethod(f);
                        if (testMethods != null && testMethods.size() > 0) {
                            for (CmgiumTestMethod tm :testMethods) {
                                String[] tmpSplit = f.getName().split("\\.");
                                tm.setName(tmpSplit[0]);
                                appendScript(generateFunction(tm));
                            }
                        }
                    }
                }
            }
        }
    }

    private String generateFunction(CmgiumTestMethod testMethod) {
        String name = StringUtils.upperCaseFirstChar(testMethod.getName());
        List<CmgiumMethod> methods  = testMethod.getMethods();
        StringBuffer sb = new StringBuffer();
        sb.append("\nSelenium.prototype.do")
                .append(name)
                .append(" = function(params) {");
        if (methods != null && methods.size() > 0) {
            for (CmgiumMethod method : methods) {
                if (method.getName().equalsIgnoreCase("setParameter")) {
                    sb.append("\n\tSelenium[\"prototype\"][\"doSetParameter\"].call(this,params);");
                } else if (method.getName().equalsIgnoreCase("andWait")) {
                    sb.append("\n\tSelenium[\"prototype\"][\"doWaitForPageToLoad\"].call(this,\"30000\");");
                } else {
                    sb.append("\n\tSelenium[\"prototype\"][\"do")
                            .append(StringUtils.upperCaseFirstChar(method.getName()))
                            .append("\"].call(this,");
                    List<String> params = method.getParams();
                    for (int i = 0; i < params.size(); i ++) {
                        sb.append("\"").append(StringUtils.escapeJavaString(params.get(i))).append("\"");
                        if (i != params.size() - 1) {
                            sb.append(",");
                        }
                    }
                    sb.append(");");
                }
            }
        }
        sb.append("\n};");
        return sb.toString();
    }


    private String generateCmgiumProperties() {
        StringBuffer bf = new StringBuffer();
        bf.append("var Cmgium = {};\n" +
                "Cmgium.properties = ");
        JSONObject list = new JSONObject();
        Properties prop = PropertiesHelper.getProperties();
        for (String key : prop.stringPropertyNames()) {
            list.put(key, prop.getProperty(key, ""));
        }
        bf.append(list.toJSONString()).append(";\n");
        return bf.toString();
    }

    private void appendScript(String data) throws IOException {
        FileWriter fileWritter = new FileWriter(userExt,true);
        BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
        try {
            bufferWritter.write(data);
        } catch (IOException ioe) {
            throw  ioe;
        } finally {
            try {
                bufferWritter.close();
            } catch (IOException ioe) {
                throw  ioe;
            }
        }
    }

    private void recycle() throws IOException {
        if (userExt.exists()) {
            userExt.delete();
        }
        userExt.createNewFile();
    }
}
