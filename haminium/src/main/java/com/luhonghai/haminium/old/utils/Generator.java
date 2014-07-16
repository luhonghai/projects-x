/** The MIT License (MIT)
 *
 *   Copyright (c) 2004 Hai Lu luhonghai@gmail.com
 *
 *   Permission is hereby granted, free of charge, to any person obtaining a copy
 *   of this software and associated documentation files (the "Software"), to deal
 *   in the Software without restriction, including without limitation the rights
 *   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   copies of the Software, and to permit persons to whom the Software is
 *   furnished to do so, subject to the following conditions:
 *
 *   The above copyright notice and this permission notice shall be included in
 *   all copies or substantial portions of the Software.
 *
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *   THE SOFTWARE.
 */

package com.luhonghai.haminium.old.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.luhonghai.haminium.old.CommandMapping;
import com.luhonghai.haminium.utils.FileUtils;
import com.luhonghai.haminium.utils.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;

import com.luhonghai.haminium.old.Cmgium;
import com.luhonghai.haminium.old.CmgiumMethod;
import com.luhonghai.haminium.old.CmgiumTestMethod;
import com.luhonghai.haminium.old.exception.CmgiumException;
import com.thoughtworks.selenium.Selenium;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * DOCME
 * 
 * @Creator Hai Lu
 * @author $Author$
 * @version $Revision$
 * @Last changed: $LastChangedDate$
 */

public class Generator {
	public static final String TEST_SUITE_TEMPLATE = "AllTests.temp";
	public static final String TEST_CASE_TEMPLATE = "TestCase.temp";
	public static final String SUREFIRE_PLUGIN_TEMPLATE = "surefire-plugin-example";

	public static final String KEY_PACKAGE_NAME = "PACKAGE_NAME";

	public static final String KEY_CLASS_NAME = "CLASS_NAME";

	public static final String KEY_TEST_METHODS = "TEST_METHODS";

	public static final String KEY_ADD_TEST_SUITE = "ADD_TEST_SUITE";

	public static String generate(String resource, Map<String, String> params)
			throws IOException {
		String output = FileUtils.readResourceContent(resource);
		if (params != null) {
			Iterator<String> keys = params.keySet().iterator();
			while (keys.hasNext()) {
				String key = keys.next();
				output = output.replace("%" + key + "%", params.get(key));
			}
		}
		return output;
	}

	public static String generateAddTestSuiteMethod(List<String> classes) {
		StringBuffer sb = new StringBuffer();
		for (String clazz : classes) {
			sb.append("\t\tsuite.addTestSuite(").append(clazz)
					.append(".class);\n");
		}
		return sb.toString();
	}

	public static String generateTestMethod(List<CmgiumTestMethod> testMethods)
            throws MojoExecutionException, IOException {
		StringBuffer bf = new StringBuffer();
        CmgiumMethod tmp;
		for (CmgiumTestMethod testMethod : testMethods) {
			// bf.append("\t@Test\n");
			bf.append("\tpublic void ").append(testMethod.getName())
					.append("() throws Exception {\n");

			for (CmgiumMethod method : testMethod.getMethods()) {
				List<String> params = method.getParams();
				if ((tmp  = Validator.isValidMethod(Cmgium.class, method)) != null) {
					bf.append("\t\tcmgium().");
                    bf.append(tmp.toMethodString()).append("\n");
				} else if (( tmp = Validator.isValidMethod(Selenium.class, method)) != null) {
                    bf.append("\t\tselenium().");
                    bf.append(tmp.toMethodString()).append("\n");
                } else if (CommandMapping.getCommandMappings().containsKey(method.getName())) {
                    CommandMapping cm = CommandMapping.getCommandMappings().get(method.getName());
                    String template = cm.getTemplate();
                    for (int i = 0; i < params.size(); i++) {
                        template = template.replace("%p" + Integer.toString(i + 1) + "%", StringUtils.escapeJavaString(params.get(i)));
                    }
                    bf.append("\t\t").append(template).append("\n");
				} else {
					throw new MojoExecutionException("Method "
							+ method.getName() + " with " + params.size()
							+ " lang.java.String parameters not found");
				}
			}

			bf.append("\t}\n\n");
		}
		return bf.toString();
	}

    public static DocumentBuilderFactory getDocumentBuilderFactory() throws ParserConfigurationException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        dbFactory.setNamespaceAware(false);
        dbFactory.setValidating(false);
        dbFactory.setFeature("http://xml.org/sax/features/namespaces", false);
        dbFactory.setFeature("http://xml.org/sax/features/validation", false);
        dbFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
        dbFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        return dbFactory;
    }


    public static List<String> parseTestSuite(String subPackage, File source) throws CmgiumException, ParserConfigurationException, IOException, SAXException {
        String[] packages = subPackage.split("\\.");
        List<String> classes = new ArrayList<String>();

        DocumentBuilderFactory dbFactory = getDocumentBuilderFactory();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(source);
        doc.getDocumentElement().normalize();
        String href, nTestCase;
        NodeList nList = doc.getElementsByTagName("a");
        if (nList == null) {
            throw  new CmgiumException("Could not found <A> tag");
        }

        for (int i = 0; i < nList.getLength(); i++) {
            Node nNode = nList.item(i);
            if (nNode.getNodeName().equalsIgnoreCase("a") && nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                href = eElement.getAttribute("href");
                nTestCase = eElement.getTextContent();
                ArrayList<String> tmpPackages = new ArrayList<String>();
                for (int j = 0; j < packages.length; j++) {
                    tmpPackages.add(packages[j]);
                }
                String[] list = href.split("/");
                for (int j = 0; j < list.length; j++) {
                    String tmp = list[j];
                    if (tmp.equalsIgnoreCase("..")) {
                        tmpPackages.remove(tmpPackages.size() - 1);
                    } else if (j == list.length - 1) {
                        String[] tmpList = tmp.split("\\.");
                        tmpPackages.add(tmpList[0]);
                    } else {
                        tmpPackages.add(tmp.toLowerCase());
                    }
                }
                classes.add(org.apache.commons.lang3.StringUtils.join(tmpPackages, "."));
            }
        }

        return classes;
    }

	public static List<CmgiumTestMethod> parseTestMethod(File source)
            throws CmgiumException, ParserConfigurationException, IOException, SAXException {
		List<CmgiumTestMethod> testMethods = new ArrayList<CmgiumTestMethod>();
		CmgiumTestMethod testMethod = new CmgiumTestMethod();
		List<CmgiumMethod> lineMethod = new ArrayList<CmgiumMethod>();
        String name, method, p1, p2;

        DocumentBuilderFactory dbFactory = getDocumentBuilderFactory();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(source);
        doc.getDocumentElement().normalize();

        NodeList nList = doc.getElementsByTagName("thead");
        if (nList == null) {
            throw  new CmgiumException("Could not found table header");
        }
        nList = nList.item(0).getChildNodes();
        for (int i = 0; i < nList.getLength(); i++) {
            Node nNode = nList.item(i);
            if (nNode.getNodeName().equalsIgnoreCase("tr") && nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                name = eElement.getElementsByTagName("td").item(0).getTextContent();
                name = CmgiumTestMethod.TEST_METHOD_PREFIX + name.substring(0,1).toUpperCase() + name.substring(1, name.length());
                testMethod.setName(name);
            }
        }


        nList = doc.getElementsByTagName("tbody");
        if (nList == null) {
            throw  new CmgiumException("Could not found table body");
        }
        nList = nList.item(0).getChildNodes();
        for (int i = 0; i < nList.getLength(); i++) {
            Node nNode = nList.item(i);
            if (nNode.getNodeName().equalsIgnoreCase("tr") && nNode.getNodeType() == Node.ELEMENT_NODE) {
                boolean appendWait = false;
                Element eElement = (Element) nNode;
                NodeList tmpNodeList = eElement.getElementsByTagName("td");
                method = tmpNodeList.item(0).getTextContent().trim();
                p1 = tmpNodeList.item(1).getTextContent().trim();
                p2 = tmpNodeList.item(2).getTextContent().trim();
                if (method.length() == 0) {
                    continue;
                }
                CmgiumMethod cMethod = new CmgiumMethod();
                if (method.endsWith("AndWait")) {
                    appendWait = true;
                    method = method.substring(0, method.lastIndexOf("AndWait"));
                }
                if (method.equals("executeWithProperties")) {
                    String[] tmpSplit = p2.split("\\|");
                    method = tmpSplit[0].trim();
                    if (tmpSplit.length > 1) {
                        p2 = tmpSplit[1].trim();
                    } else {
                        p2 = "";
                    }
                    p1 = parseString(p1, PropertiesHelper.getProperties());
                    p2 = parseString(p2, PropertiesHelper.getProperties());
                }

                cMethod.setName(method);
                List<String> params = new ArrayList<String>();
                params.add(p1);
                params.add(p2);
                cMethod.setParams(params);
                lineMethod.add(cMethod);

                if (appendWait) {
                    cMethod = new CmgiumMethod();
                    cMethod.setName("andWait");
                    lineMethod.add(cMethod);
                }
            }
        }
        testMethod.setMethods(lineMethod);
        testMethods.add(testMethod);
        return testMethods;
	}

	public static String parseString(String source, Properties prod) {
		String regex = "\\$\\{([^}]*)\\}";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(source);
		while (matcher.find()) {
			String property = matcher.group(1);
			String value = prod.getProperty(property);
			if (value != null) {
				source = source.replace("${" + property + "}", value);
			}
		}
		return source;
	}
}
