package com.igate.mongodb.class1;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.Template;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

public class HelloWorldSparkFreeMarker {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//Combine Helloworld and HelloWorldFreeMarker
		
		Configuration configuration = new Configuration();

		// Defining the folder where to look for FTL files. / represents root folder
        configuration.setClassForTemplateLoading(
        		HelloWorldFreeMarker.class, "/");
        StringWriter writer = new StringWriter();
       // port(5679);
		
		Spark.get(new Route("/") {
            @Override
            public Object handle(final Request request,
                                 final Response response) {
            	// Add code used in HellowWorldFreeMarker and return the writer.
            	try {
                	//Defining the folder where to look for FTL files. / represents root folder and hello.ftl is the filename.
                    Template helloTemplate = configuration.getTemplate("hello.ftl");
                    
                    Map<String, Object> helloMap = new HashMap<String, Object>();
                    helloMap.put("name", "Freemarker Output in FTL Parmater");

                    helloTemplate.process(helloMap, writer);

                    System.out.println(writer);

                } catch (Exception e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                return writer;
            }
        });
		//Run this application and test it by giving http://localhost:4567/   --> By invoking this hello.ftl is called with proper values inside it.
    }

	

}
