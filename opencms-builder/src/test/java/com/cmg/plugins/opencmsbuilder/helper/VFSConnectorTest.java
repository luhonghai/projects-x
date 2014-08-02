package com.cmg.plugins.opencmsbuilder.helper;

import com.cmg.plugins.opencmsbuilder.ApplicationConfiguration;
import junit.framework.Test;
import junit.framework.TestSuite; 
import junit.framework.TestCase; 

/** 
* VFSConnector Tester. 
* 
* @author <Authors name> 
* @since <pre>07/31/2014</pre> 
* @version 1.0 
*/ 
public class VFSConnectorTest extends TestCase { 
public VFSConnectorTest(String name) { 
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
    configuration.setBaseURL("http://localhost:9080/content/webdav");
    configuration.setOpencmsHome("D:\\PL\\[JBoss] Pensionline\\server\\default\\deploy\\opencms857.war");
   // VFSConnector vfsConn = new VFSConnector(configuration);
   // vfsConn.open();
} 



public static Test suite() { 
return new TestSuite(VFSConnectorTest.class); 
} 
} 
