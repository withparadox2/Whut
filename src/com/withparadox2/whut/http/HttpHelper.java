package com.withparadox2.whut.http;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.impl.client.DefaultHttpClient;

public class HttpHelper {

    private static DefaultHttpClient httpClient;
    
    public static DefaultHttpClient getHttpClient(){
    	if(httpClient == null){
    		httpClient = new DefaultHttpClient();
    	}
    	return httpClient;
    }
    
	public static void releaseConnection(HttpResponse response){
		if( response.getEntity() != null ) {
			try {
				response.getEntity().consumeContent();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}//if
	}    
}
