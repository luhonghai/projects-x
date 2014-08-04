package com.cmg.plugins.opencmsbuilder.helper;

import com.cmg.plugins.opencmsbuilder.ApplicationConfiguration;
import com.cmg.plugins.opencmsbuilder.command.ServiceCommand;
import junit.framework.Test;
import junit.framework.TestSuite; 
import junit.framework.TestCase; 

/** 
* ServiceCommand Tester. 
* 
* @author <Authors name> 
* @since <pre>08/04/2014</pre> 
* @version 1.0 
*/ 
public class ServiceCommandTest extends TestCase { 
public ServiceCommandTest(String name) { 
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
* Method: execute() 
* 
*/ 
public void testExecute() throws Exception {
    ServiceCommand command = new ServiceCommand(ServiceCommand.START, "jboss_pensionline", "http://localhost:1919");
    //command.execute();

} 



public static Test suite() { 
return new TestSuite(ServiceCommandTest.class); 
} 
} 
