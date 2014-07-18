package com.luhonghai.haminium.ide.docs;

import junit.framework.TestCase;

import java.io.File;

/** 
* ApiDoc Tester.
* 
* @author Hai Lu
* @since <pre>Jul 18, 2014</pre> 
* @version 1.0 
*/ 
public class ApiDocTest extends TestCase {

public void before() throws Exception { 
} 

public void after() throws Exception { 
} 

/** 
* 
* Method: toXml(ApiDoc doc, File output) 
* 
*/
public void testToXml() throws Exception {
    ApiDoc doc = new ApiDoc();
    ApiDoc.Function function = doc.newFunction();
    function.setName("takeScreenshoot");
    function.setComment("<p>This method (command) use for saves the entire contents" +
            " of the current window canvas" +
            "to a PNG file." +
            "<p>By default, the root folder of screen shots is " +
            "[PROJECT ROOT]/target/artifact/screenshots</p>");
    ApiDoc.Function.Parameter param = function.newParameter();
    param.setName("folderName");
    param.setComment("the folder name inside root folder. " +
            "Leave empty this will take " +
            "Test suite name");
    param = function.newParameter();
    param.setName("fileName");
    param.setComment("the file name of screen shoot. " +
            "Leave empty this will take" +
            " Test case name");


    for (ApiDoc.Function f : doc.getFunctions()) {
        System.out.println("=========== FUNCTION ===============");
        System.out.println("name: " + f.getName());
        System.out.println("comment: " + f.getComment());
        for (ApiDoc.Function.Parameter p : f.getParameters()) {
            System.out.println(">>> PARAMS");
            System.out.println("name: " + p.getName());
            System.out.println("comment: " + p.getComment());
        }
    }
    doc.toXml(new File("C:/test.xml"));
} 

/** 
* 
* Method: fromXml(File source) 
* 
*/
public void testFromXml() throws Exception { 
//TODO: Test goes here... 
} 


} 
