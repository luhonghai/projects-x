package com.cmg.plugins.opencmsbuilder.publisher;

import junit.framework.Test; 
import junit.framework.TestSuite; 
import junit.framework.TestCase;

import java.io.File;
import java.util.List;

/** 
* VfsContent Tester. 
* 
* @author <Authors name> 
* @since <pre>08/01/2014</pre> 
* @version 1.0 
*/ 
public class VfsContentTest extends TestCase { 
public VfsContentTest(String name) { 
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
* Method: init() 
* 
*/ 
public void testInit() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: getMappings() 
* 
*/ 
public void testGetMappings() throws Exception {
//    VfsMapping vfsContent = new VfsMapping(new File("D:\\GIT\\pensionline-repo\\core\\vfs\\system"), new File("D:\\GIT\\pensionline-repo\\core\\vfs\\system"), "/system/");
//    vfsContent.init();
//    List<VfsMappingObject> list = vfsContent.getMappings();
//    for (VfsMappingObject obj : list) {
//        System.out.println(">>>");
//        System.out.println("Des: " + obj.getDestination());
//        System.out.println("File: " + obj.getFile());
//    }
} 


/** 
* 
* Method: generate(File file) 
* 
*/ 
public void testGenerate() throws Exception { 
//TODO: Test goes here... 
/* 
try { 
   Method method = VfsContent.getClass().getMethod("generate", File.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/ 
} 


public static Test suite() { 
return new TestSuite(VfsContentTest.class); 
} 
} 
