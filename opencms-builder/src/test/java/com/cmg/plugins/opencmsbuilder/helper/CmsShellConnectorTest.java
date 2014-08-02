package com.cmg.plugins.opencmsbuilder.helper;

import com.cmg.plugins.opencmsbuilder.ApplicationConfiguration;
import junit.framework.Test;
import junit.framework.TestSuite; 
import junit.framework.TestCase; 

/** 
* CmsShellConnector Tester. 
* 
* @author <Authors name> 
* @since <pre>08/01/2014</pre> 
* @version 1.0 
*/ 
public class CmsShellConnectorTest extends TestCase { 
public CmsShellConnectorTest(String name) { 
super(name); 
} 

public void setUp() throws Exception { 
super.setUp(); 
} 

public void tearDown() throws Exception { 
super.tearDown(); 
} 

/** 
* 
* Method: open() 
* 
*/ 
public void testOpen() throws Exception {
    ApplicationConfiguration configuration = new ApplicationConfiguration();
    configuration.setUsername("Admin");
    configuration.setPassword("P3nsions0");
    configuration.setOpencmsHome("D:\\PL\\[JBoss] Pensionline\\server\\default\\deploy\\opencms857.war");
   // CmsShellConnector shellConnector = new CmsShellConnector(configuration);
   // shellConnector.open();
   // shellConnector.executeCommand("publishResource", new String[] {"/sites/default/_login_handler.jsp"});
}

/** 
* 
* Method: executeCommand(String name, String[] parameters) 
* 
*/ 
public void testExecuteCommand() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: close() 
* 
*/ 
public void testClose() throws Exception { 
//TODO: Test goes here... 
} 


/** 
* 
* Method: init() 
* 
*/ 
public void testInit() throws Exception { 
//TODO: Test goes here... 
/* 
try { 
   Method method = CmsShellConnector.getClass().getMethod("init"); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/ 
} 


public static Test suite() { 
return new TestSuite(CmsShellConnectorTest.class); 
} 
} 
