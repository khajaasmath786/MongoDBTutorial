package com.igate.mongodb.class1;

import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

public class SparkGetHandlers {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Spark.get(new Route("/") {
			@Override
			public Object handle(final Request request, final Response response) {
				return "Hello World From Spark\n";
			}
		});
	
	
	
	Spark.get(new Route("/hello") {
		@Override
		public Object handle(final Request request, final Response response) {
			return "Hello World From Spark with hello handler\n";
		}
	});



Spark.get(new Route("/test") {
	@Override
	public Object handle(final Request request, final Response response) {
		return "Hello World From Spark with test handler\n";
	}
});

//:thing is wildcard. what ever you pass through brower instead of :thing will be return. example localhos:/echo/cat ..cat is returned
Spark.get(new Route("/echo/:thing") {
	@Override
	public Object handle(final Request request, final Response response) {
		return request.params(":thing");
	}
});


}// End of Main Method


}// End of class


