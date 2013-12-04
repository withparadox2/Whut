package com.withparadox2.whut.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.withparadox2.whut.dao.WhutGlobal;

import android.os.AsyncTask;

public class LoginTuTask extends AsyncTask<String, Void, String>{
    
    private Callback callback;
	public interface Callback{
		void onPostExecute(String result);
	}
	
	public LoginTuTask(Callback callback){
		this.callback = callback;
	}

	@Override
    protected String doInBackground(String... params) {
	    // TODO Auto-generated method stub
		HttpPost httpPost = new HttpPost("http://202.114.89.11/opac/reader/doLogin");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
		nameValuePairs.add(new BasicNameValuePair("rdid", params[0]));
		nameValuePairs.add(new BasicNameValuePair("rdPasswd", params[1]));
		nameValuePairs.add(new BasicNameValuePair("returnUrl", ""));
		DefaultHttpClient httpClient = SingleHttpClient.getHttpClient();

		httpPost.setHeader("Referer", "http://202.114.89.11/opac/reader/login");
		httpPost.setHeader("Accept-Language", "zh-CN");
		try {
	        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
    		HttpResponse response = httpClient.execute(httpPost);
            return getLoginTuData(EntityUtils.toString(response.getEntity()));
        } catch (UnsupportedEncodingException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
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
	    callback.onPostExecute(result);
    }
    
	public String getLoginTuHtml() {
		HttpGet httpGet = new HttpGet("http://202.114.89.11/opac/reader/space");
		HttpResponse response;
		try {
            DefaultHttpClient httpClient = SingleHttpClient.getHttpClient();
			response = httpClient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			return EntityUtils.toString(entity, "GB2312");
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
    
	private String getRenewList() throws ClientProtocolException, IOException{
		HttpPost httpPost = new HttpPost("http://202.114.89.11/opac/loan/renewList");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
		nameValuePairs.add(new BasicNameValuePair("rows", "30"));
		DefaultHttpClient httpClient = SingleHttpClient.getHttpClient();
        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
		HttpResponse response = httpClient.execute(httpPost);
		return EntityUtils.toString(response.getEntity());
	}
    
	public String getLoginTuData(String html) {
		Document document = Jsoup.parse(html);
		String s = document.select("div.navbar_info").html().toString();
		Pattern pattern = Pattern.compile("»¶Ó­Äú£º(.*?)&nbsp");
		Matcher matcher = pattern.matcher(s);
		String name = null;
		if (matcher.find())
			name = matcher.group(1);
		return name;
	}
	

}
