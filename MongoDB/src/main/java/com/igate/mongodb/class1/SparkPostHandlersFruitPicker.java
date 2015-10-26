package com.igate.mongodb.class1;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.Template;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

public class SparkPostHandlersFruitPicker {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//Combine Helloworld and HelloWorldFreeMarker
		
		Configuration configuration = new Configuration();

		// Defining the folder where to look for FTL files. / represents root folder
        configuration.setClassForTemplateLoading(
        		SparkPostHandlersFruitPicker.class, "/");
        StringWriter writer = new StringWriter();
       // port(5679);
		
		Spark.get(new Route("/") {
            @Override
            public Object handle(final Request request,
                                 final Response response) {
            	// Add code used in HellowWorldFreeMarker and return the writer.
            	try {
                	//Defining the folder where to look for FTL files. / represents root folder and hello.ftl is the filename.
                    Template fruitPickerTemplate = configuration.getTemplate("fruitPicker.ftl");
                    
                    Map<String, Object> fruitPickerMap = new HashMap<String, Object>();
                   // <#list fruits as fruit>  --> In FTL you are declaring fruits which is array, similar to java it is then passed to fruit
                    //Dont get confused with Fruits and Fruit here. Fruits is like for loop and map. Fruit is used in next method of post.
                    fruitPickerMap.put("fruits", Arrays.asList("Apple","Orange","Banana"));

                    fruitPickerTemplate.process(fruitPickerMap, writer);

                    System.out.println(writer);

                } catch (Exception e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                return writer;
            }
        });
		//Run this application and test it by giving http://localhost:4567/   --> By invoking this hello.ftl is called with proper values inside it.
		
		
		
		// Handling the post request after submitting the page. Declare same post from submit button
		Spark.post(new Route("/favorite_fruit") {
			
			@Override
            public Object handle(final Request request,
                                 final Response response) {
            	// Add code used in HellowWorldFreeMarker and return the writer.
            	
                	String selectedFruit=request.queryParams("fruit");
                	if(selectedFruit==null)
                	{
                		return "Why don't you select one fruit";
                	}
                	else
                		return "You have selected Fruit "+selectedFruit;

                 
            }
        });
    }

	
	
	
	

}
