package com.igate.mongodb.class1;

import java.io.ObjectInputStream.GetField;

import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

public class HelloWorld {

	public static void main(String[] args) {
		
	        Spark.get(new Route("/") {
	            @Override
	            public Object handle(final Request request,
	                                 final Response response) {
	                return "Hello World From Spark\n";
	            }
	        });
	    }

}
