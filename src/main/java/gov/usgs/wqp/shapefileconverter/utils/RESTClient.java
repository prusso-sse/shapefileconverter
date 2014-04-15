package gov.usgs.wqp.shapefileconverter.utils;

import java.io.File;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

public class RESTClient {
	public static String get(String uri, String user, String pass, String mediaType) {
		Client client = Client.create();
		
		if(user != null) {
			client.addFilter(new com.sun.jersey.api.client.filter.HTTPBasicAuthFilter(user, pass));
		}
		
		WebResource service = client.resource(uri);
		
		String response;
		try {
			response = service.type(mediaType).accept(mediaType).get(String.class);
		} catch(UniformInterfaceException e) {
			response = "UniformInterfaceException Exception [" + e.getMessage() + "]";
        	System.out.println(response);
		} catch(ClientHandlerException e) {
			response = "ClientHandlerException Exception [" + e.getMessage() + "]";
        	System.out.println(response);
		}
		
		return response;
	}
	
	public static String post(String uri, String data, String user, String pass, String mediaType) {
		Client client = Client.create();
		
		if(user != null) {
			client.addFilter(new com.sun.jersey.api.client.filter.HTTPBasicAuthFilter(user, pass));
		}
		
		WebResource service = client.resource(uri);
		
		String response;
		try {
			response = service.type(mediaType).accept(mediaType).post(String.class, data);
		} catch(UniformInterfaceException e) {
			response = "UniformInterfaceException Exception [" + e.getMessage() + "]";
        	System.out.println(response);
		} catch(ClientHandlerException e) {
			response = "ClientHandlerException Exception [" + e.getMessage() + "]";
        	System.out.println(response);
		}
		
		return response;
	}
	
	public static String putDataFile(String uri, String user, String pass, String mediaType, String filename) {		
		ClientConfig config = new DefaultClientConfig(); 
        Client client = Client.create(config);
		
		if(user != null) {
			client.addFilter(new com.sun.jersey.api.client.filter.HTTPBasicAuthFilter(user, pass));
		}
    
        WebResource service = client.resource(uri); 
        File file = new File(filename);
        
        String response;
        if(file.exists()) {
        	try {
        		response = service.type(mediaType).accept(mediaType).put(String.class, file);
        	} catch(UniformInterfaceException e) {
    			response = "UniformInterfaceException Exception [" + e.getMessage() + "]";
            	System.out.println(response);
    		} catch(ClientHandlerException e) {
    			response = "ClientHandlerException Exception [" + e.getMessage() + "]";
            	System.out.println(response);
    		}
        } else {
        	response = "File of type [" + mediaType + "] DOES NOT EXIST";
        	System.out.println(response);
        }
        
        return response;
	}
	
	public static String delete(String uri, String user, String pass, String mediaType) {
		Client client = Client.create();
		
		if(user != null) {
			client.addFilter(new com.sun.jersey.api.client.filter.HTTPBasicAuthFilter(user, pass));
		}
		
		WebResource service = client.resource(uri);
		
		String response;
		try {
			response = service.type(mediaType).accept(mediaType).delete(String.class);
		} catch(UniformInterfaceException e) {
			response = "UniformInterfaceException Exception [" + e.getMessage() + "]";
        	System.out.println(response);
		} catch(ClientHandlerException e) {
			response = "ClientHandlerException Exception [" + e.getMessage() + "]";
        	System.out.println(response);
		}
		
		return response;
	}
}
