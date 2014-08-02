package com.cmg.plugins.opencmsbuilder.helper;

import junit.framework.Test; 
import junit.framework.TestSuite; 
import junit.framework.TestCase; 

/** 
* ServiceHelper Tester. 
* 
* @author Hai Lu
* @since <pre>07/31/2014</pre> 
* @version 1.0 
*/ 
public class ServiceHelperTest extends TestCase { 
public ServiceHelperTest(String name) { 
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
* Method: isRunning(String serviceName) 
* 
*/ 
public void testIsRunning() throws Exception {
    //AppHostSvc
  //  WindowsService ws =new WindowsService("jboss_pensionline");
  //  System.out.print(ws.start());
} 



public static Test suite() { 
return new TestSuite(ServiceHelperTest.class); 
} 
} 
