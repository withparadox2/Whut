package com.withparadox2.whut.http;

import org.apache.http.impl.client.DefaultHttpClient;

public class SingleHttpClient {

    private static DefaultHttpClient httpClient;
    
    public static DefaultHttpClient getHttpClient(){
    	if(httpClient == null){
    		httpClient = new DefaultHttpClient();
    	}
    	return httpClient;
    }
}
