package com.withparadox2.whut.http;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.withparadox2.whut.dao.WhutGlobal;

public class HttpOperation {
	private Context mContex;
	private HttpClient httpClient;
	private String html = "";
	private HttpURLConnection urlConnection;
	private HttpPost httpPost;
	private HttpGet httpGet;
	private final String TAG = "HttpOperation"; 
	
	public HttpOperation(Context context){
		this.mContex = context;
		
		httpClient = new DefaultHttpClient(); 
	}
	
	public void closeHttpUrl(){
		urlConnection.disconnect();
	}
	
	public void closeHttpPost(){
	    Log.i(TAG, "outside httpPost==null"+(httpPost==null));
		httpPost.abort();
		Log.i(TAG, "执行httpost。abort");
	}
	
	public void closeHttpGet(){
	    Log.i(TAG, "outside httpGet==null"+(httpGet==null));
		httpGet.abort();
	}
	

	 public boolean isConnect(Context context) { 
	    try { 
	        ConnectivityManager connectivity = (ConnectivityManager) context 
	                .getSystemService(Context.CONNECTIVITY_SERVICE); 
	        if (connectivity != null) {
	            NetworkInfo info = connectivity.getActiveNetworkInfo(); 
	            if (info != null&& info.isConnected()) { 
	                if (info.getState() == NetworkInfo.State.CONNECTED) { 
	                    return true; 
	                } 
	            } 
	        } 
	    } catch (Exception e) { 
	    	Log.v("error",e.toString()); 
	    } 
        return false; 
    } 
	

    public int loginJiao(String userIdText, String userPasswordText) throws ClientProtocolException, IOException{
        httpPost = new HttpPost("http://sso.jwc.whut.edu.cn/Certification/login.do");
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
        nameValuePairs.add(new BasicNameValuePair("password", userPasswordText));
        nameValuePairs.add(new BasicNameValuePair("userName", userIdText));   
        nameValuePairs.add(new BasicNameValuePair("type", "xs"));  
        
        httpPost.setHeader("Referer", "http://sso.jwc.whut.edu.cn/Certification/login.do");
        httpPost.setHeader("Accept-Language", "zh-CN");
        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
        HttpResponse response = httpClient.execute(httpPost);
        List<Cookie> cookies = ((AbstractHttpClient) httpClient).getCookieStore().getCookies();    
        if (cookies.isEmpty()) {    
            System.out.println("None");    
        } else {    
            for (int i = 0; i < cookies.size(); i++) {
            	if(cookies.get(i).getName().toString().equals("JSESSIONID")){
            		WhutGlobal.JSESSIONID = "JSESSIONID=" + cookies.get(i).getValue().toString();
            	}
                Log.i(TAG,"JessionId is" + WhutGlobal.JSESSIONID ); 
            }    
        } 
        HttpEntity entity = response.getEntity();
        html = EntityUtils.toString(entity, "GB2312");
       // System.out.println("hh是大法师打发httttmmmmlol"+html);
        return response.getStatusLine().getStatusCode();
   }
    
	
	public int ifLoginSuccessStatus(){
		return 1;
	}

	
	public void getKebiaoHtml(String getString) throws ClientProtocolException, UnsupportedEncodingException, IOException{
	     httpGet = new HttpGet(getString);
	     Log.i("TAG1", "innerside httpGet==null"+(httpGet==null));
	     httpGet.setHeader("Cookie", WhutGlobal.JSESSIONID);
	     HttpResponse response;
		 response = httpClient.execute(httpGet);
		 List<Cookie> cookies = ((AbstractHttpClient) httpClient).getCookieStore().getCookies();    
	        if (cookies.isEmpty()) {    
	            System.out.println("None");    
	        } else {    
	            for (int i = 0; i < cookies.size(); i++) {
	            	if(cookies.get(i).getName().toString().equals("JSESSIONID")){
	            		WhutGlobal.JSESSIONID = "JSESSIONID=" + cookies.get(i).getValue().toString();
	            	}
	            }    
                Log.i(TAG,"get html JessionId is" + WhutGlobal.JSESSIONID ); 
	        } 
         HttpEntity entity = response.getEntity();
         html = EntityUtils.toString(entity, "GB2312");
         //System.out.println("kkkkkkkkbbbbbbbbbb==="+html);
	}
	
	

	
	public boolean isNameAndPassRight(String html){
		Document document = Jsoup.parse(html);
		return !(document.select("#lbMm").size()>0);
	}
	
	public String getUserInfo(){
		Document document = Jsoup.parse(html);
		Elements s = document.select("div.nav td .font2");
		return s.get(0).html().toString();
	}
	
	
    public void loginTu() throws ClientProtocolException, IOException{
        httpPost = new HttpPost("http://202.114.89.11/opac/reader/doLogin");
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
        nameValuePairs.add(new BasicNameValuePair("rdid", WhutGlobal.USER_ID));
        nameValuePairs.add(new BasicNameValuePair("rdPasswd", WhutGlobal.USER_PASSWORD));   
        nameValuePairs.add(new BasicNameValuePair("returnUrl", ""));  
        BasicHttpParams basicHttpParams = new BasicHttpParams();
        basicHttpParams.setParameter("rdid", "vikaspatidar");
        basicHttpParams.setParameter("rdPasswd", "patidar");
        basicHttpParams.setParameter("returnUrl", "");
        
        httpPost.setHeader("Referer", "http://202.114.89.11/opac/reader/login");
        httpPost.setHeader("Accept-Language", "zh-CN");
        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
        HttpResponse response = httpClient.execute(httpPost);
        getCookie(httpClient);
        HttpEntity entity = response.getEntity();
        //html = EntityUtils.toString(entity, "GB2312");
        //System.out.println("hh是大法师打发httttmmmmlol"+html);
        getLoginTuHtml();
   }
    
   
    public void getLoginTuHtml(){
    	 httpGet = new HttpGet("http://202.114.89.11/opac/reader/space");
         httpGet.setHeader("Cookie", "JSESSIONID="+WhutGlobal.JSESSIONID);
         httpGet.setHeader("Referer", "http://202.114.89.11/opac/reader/login");
         HttpResponse response;
 	    try {
			response = httpClient.execute(httpGet);
			Log.i(TAG,"iiiiiiiiiiii"+ response.getStatusLine().getStatusCode());
            HttpEntity entity = response.getEntity();
            html = EntityUtils.toString(entity, "GB2312");
            System.out.println(html);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	public void getLoginTuData(){
		Document document = Jsoup.parse(html);
		String s = document.select("div.navbar_info").html().toString();
		Pattern pattern = Pattern.compile("欢迎您：(.*?)&nbsp");
		Matcher matcher = pattern.matcher(s);
		String name = "";
		if(matcher.find()) name = matcher.group(1);
		WhutGlobal.USER_NAME = name;
	}
	
	public void getJieYueHtml() throws ClientProtocolException, UnsupportedEncodingException, IOException{
	     httpGet = new HttpGet("http://202.114.89.11/opac/loan/currentLoanList");
	     httpGet.setHeader("Cookie", "JSESSIONID="+WhutGlobal.JSESSIONID);
	     HttpResponse response;
		 response = httpClient.execute(httpGet);
         HttpEntity entity = response.getEntity();
         html = EntityUtils.toString(entity, "GB2312");
	}
	
	public String[][] getJieYueData() throws IndexOutOfBoundsException{
		Document document = Jsoup.parse(html);
		Elements trs = document.select("#contentTable").select("tr");
		String[][] myResult;
		if(trs.size()==0){
			myResult = new String[1][9];
		}else{
			myResult = new String[trs.size()][8];
		}
		for(int i=0; i<trs.size(); i++){
			Elements tds;
			if(i==0){
				tds = trs.get(i).select("th");
			}else{
				tds = trs.get(i).select("td");
			}
			for(int j=0; j<8; j++ ){
				Element td = tds.get(j);
				Log.i(TAG, i+","+j+"  "+td.text());
				myResult[i][j] = td.text() ; 
			}
		}
//		myResult[3][7]="2012-12-17";
//		myResult[4][7]="2012-12-18";
//		myResult[8][7]="2011-12-17";
//		myResult[9][7]="2012-12-19";
		return myResult;
	}
	
	public void getXuJieHtml() throws ClientProtocolException, UnsupportedEncodingException, IOException{
        httpGet = new HttpGet("http://202.114.89.11/opac/loan/renewList");
        httpGet.setHeader("Cookie", "JSESSIONID="+WhutGlobal.JSESSIONID);
        HttpResponse response;
	    response = httpClient.execute(httpGet);
        HttpEntity entity = response.getEntity();
        html = EntityUtils.toString(entity, "GB2312");
	}
	
	public String[][] getXuJieData() throws IndexOutOfBoundsException, NegativeArraySizeException{
		Document document = Jsoup.parse(html);
		Elements trs = document.select("#contentTable").select("tr");
		String[][] myResult;
		if(trs.size()==0){
			myResult = new String[1][9];
		}else{
			myResult = new String[trs.size()][9];
		}
		for(int i=0; i<trs.size(); i++){
			Elements tds;
			if(i==0){
				tds = trs.get(i).select("th");
			}else{
				tds = trs.get(i).select("td");
			}
			for(int j=1; j<10; j++ ){
				Element td = tds.get(j);
				myResult[i][j-1] = td.text() ; 
			}
		}
//		myResult[3][7]="2012-12-17";
//		myResult[4][7]="2012-12-18";
//		myResult[8][7]="2011-12-17";
//		myResult[9][7]="2012-12-19";
		return myResult;
	}
	
	
    public void getXuJieSingleHtml(List<NameValuePair> nameValuePairs) throws ClientProtocolException, IOException{
    	//点击续借按钮
        httpPost = new HttpPost("http://202.114.89.11/opac/loan/doRenew");
        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
        httpPost.setHeader("Cookie", "JSESSIONID="+WhutGlobal.JSESSIONID);
        httpPost.setHeader("Referer", "http://202.114.89.11/opac/loan/renewList");
        HttpResponse response = httpClient.execute(httpPost);
        HttpEntity entity = response.getEntity();
        html = EntityUtils.toString(entity, "GB2312");
//      System.out.println("hhhttttmmmmlol"+html);
    }
    
    public void xuJieSuccessFlag(){
    	Document document = Jsoup.parse(html);
		String s = document.select("#content").html().toString();
		WhutGlobal.XUJIE_SUCCESS_FLAG = s.contains("图书续借成功");
    }
	
    public static void getCookie(HttpClient httpClient){
		 List<Cookie> cookies = ((AbstractHttpClient) httpClient).getCookieStore().getCookies();    
	        if (cookies.isEmpty()) {    
	            System.out.println("None");    
	        } else {    
	            for (int i = 0; i < cookies.size(); i++) {
	            	if(cookies.get(i).getName().toString().equals("JSESSIONID")){
	            		WhutGlobal.JSESSIONID =  cookies.get(i).getValue().toString();
	            		Log.i("HttpOperation", WhutGlobal.JSESSIONID);
	            	}
	            }    
	        } 
	}	
}
