package com.igate.mongodb.class1;

import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public class HelloWorldFreeMarker {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// Add filename hello.ftl in resources folder of maven. This will act as JSP page.
		
		
		Configuration configuration = new Configuration();

		// Defining the folder where to look for FTL files. / represents root folder
        configuration.setClassForTemplateLoading(
        		HelloWorldFreeMarker.class, "/");

        try {
        	//Defining the folder where to look for FTL files. / represents root folder and hello.ftl is the filename.
            Template helloTemplate = configuration.getTemplate("hello.ftl");
            StringWriter writer = new StringWriter();
            Map<String, Object> helloMap = new HashMap<String, Object>();
            helloMap.put("name", "Freemarker Output in FTL Parmater");

            helloTemplate.process(helloMap, writer);

            System.out.println(writer);

        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

	}

}
