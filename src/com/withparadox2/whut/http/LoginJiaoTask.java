package com.withparadox2.whut.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.withparadox2.whut.util.Helper;

import android.os.AsyncTask;

public class LoginJiaoTask  extends AsyncTask<String, Void, String>{
    
    private CallBack callBack;
    
    public interface  CallBack{
		void onPostExecute(String result);
	}
    
	public LoginJiaoTask(CallBack callBack){
		this.callBack = callBack;
	}


	@Override
    protected String doInBackground(String... params) {
	    // TODO Auto-generated method stub
        HttpPost httpPost = new HttpPost(HttpInformation.LoginJiaoUrl);
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
		nameValuePairs.add(new BasicNameValuePair("password", params[1]));
		nameValuePairs.add(new BasicNameValuePair("userName", params[0]));
		nameValuePairs.add(new BasicNameValuePair("type", "xs"));
		httpPost.setHeader("Referer", HttpInformation.LoginJiaoUrl);
		httpPost.setHeader("Accept-Language", "zh-CN");
		try {
	        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
	        DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
	        HttpParams httpParams = defaultHttpClient.getParams();
	        HttpConnectionParams.setConnectionTimeout(httpParams, HttpInformation.TIMEOUT_SECONDS * 1000);
	        HttpConnectionParams.setSoTimeout(httpParams, HttpInformation.TIMEOUT_SECONDS * 1000);
			HttpResponse httpResponse = defaultHttpClient.execute(httpPost);
            Helper.getCookie(defaultHttpClient);
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode == HttpInformation.HTTP_STATUS_OK) {
               return getUserInfo(EntityUtils.toString(httpResponse.getEntity()));
            } else {
                throw new IOException(httpResponse.getStatusLine().getReasonPhrase());
            }			
        } catch (ClientProtocolException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        } catch (IOException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }
		
	    return null;
    }

	@Override
    protected void onPostExecute(String result) {
	    // TODO Auto-generated method stub
	    callBack.onPostExecute(result);
    }
    
	private String getUserInfo(String html) {
		Document document = Jsoup.parse(html);
		Elements s = document.select("div.nav td .font2");
		return s.get(0).html().toString();
	}


}
