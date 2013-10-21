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
		BasicHttpParams params = new BasicHttpParams();
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		final SSLSocketFactory sslSocketFactory = SSLSocketFactory.getSocketFactory();
		schemeRegistry.register(new Scheme("https", sslSocketFactory, 443));
		ClientConnectionManager cm = new ThreadSafeClientConnManager(params, schemeRegistry);
		httpClient = new DefaultHttpClient(cm, params); 
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
	

	    
	public void getUrlHeader() throws ClientProtocolException, IOException, IllegalStateException{
        httpGet = new HttpGet("http://202.114.90.117/");
        HttpContext context = new BasicHttpContext(); 
        HttpResponse response;
        response = httpClient.execute(httpGet, context);
		if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
             throw new IOException(response.getStatusLine().toString());
         HttpUriRequest currentReq = (HttpUriRequest) context.getAttribute( 
                 ExecutionContext.HTTP_REQUEST);
         HttpHost currentHost = (HttpHost)  context.getAttribute( 
                 ExecutionContext.HTTP_TARGET_HOST);
         String currentUrl = (currentReq.getURI().isAbsolute()) ? currentReq.getURI().toString() : (currentHost.toURI() + currentReq.getURI());
         WhutGlobal.URL_HEADER_STR = currentUrl.substring(0, 49);
	}
	
	public String getLoginParameters(String userIdText, String userPasswordText) throws UnsupportedEncodingException{
			  return      "__VIEWSTATE=" + URLEncoder.encode("dDwtMTIwMTU3OTE3Nzs7PoO2novI+sOYb1lmb3QMa5KLuwhZ", "UTF-8") +
			        "&lbLanguage=" + URLEncoder.encode("", "UTF-8")+
			        "&RadioButtonList1=" + URLEncoder.encode("%D1%A7%C9%FA", "UTF-8")+
			        "&TextBox1=" + URLEncoder.encode(userIdText, "UTF-8")+
			        "&TextBox2=" + URLEncoder.encode(userPasswordText, "UTF-8")+
			        "&Button1=" + URLEncoder.encode("", "UTF-8");
	}
	
	
	public int postMethod(String urlParameters) throws MalformedURLException,ProtocolException, IOException, NullPointerException{
		
		 URL url = new URL(WhutGlobal.URL_HEADER_STR+"Default2.aspx");
		 urlConnection = (HttpURLConnection) url.openConnection();
		 urlConnection.setDoOutput(true);
		 urlConnection.setDoInput(true);
		 urlConnection.setRequestMethod("POST");
		 urlConnection.setRequestProperty("Connection", "Keep-Alive");
		 urlConnection.setRequestProperty("Accept-Encoding", "gzip");
		 urlConnection.setInstanceFollowRedirects(false);
		 DataOutputStream wr = new DataOutputStream (
				   urlConnection.getOutputStream ());
	     wr.writeBytes (urlParameters);
	     wr.flush ();
	     wr.close ();
	     InputStream is = urlConnection.getInputStream();
	      BufferedReader rd = new BufferedReader(new InputStreamReader(is));
	      String line1;
	      StringBuffer response4 = new StringBuffer(); 
	      while((line1 = rd.readLine()) != null) {
	        response4.append(line1);
	        response4.append('\r');
	      }
	      html = response4.toString();
	      System.out.println(urlConnection.getResponseCode()+"sdfsdffsdf"+response4.toString()+"asdfasdf");
	      rd.close();
	     return urlConnection.getResponseCode();
	}
	
	public int ifLoginSuccessStatus(){
		if(html.length()<5){
			return 0;//wifi有问题
		}else if(html.contains(WhutGlobal.USER_ID)){
			return 1;//看看有没有出现错误链接，redirect中含有用户id
		}else{
			return 3;
		}
	}
	
	
	public void getMethod(String userId) throws MalformedURLException,ProtocolException, IOException, NullPointerException{
	     URL url = new URL(WhutGlobal.URL_HEADER_STR+"xs_main.aspx?xh=" + userId);
		 urlConnection = (HttpURLConnection) url.openConnection();
		 urlConnection.setDoOutput(true);
		 urlConnection.setDoInput(true);
		 urlConnection.setRequestMethod("GET");
		 urlConnection.setRequestProperty("Connection", "Keep-Alive");
		 urlConnection.setRequestProperty("Accept-Encoding", "gzip");
		 urlConnection.connect();
		 InputStream is = urlConnection.getInputStream();
	     BufferedReader rd = new BufferedReader(new InputStreamReader(is,"GBK"));
	     String line11;
	     StringBuffer response = new StringBuffer(); 
	     while((line11 = rd.readLine()) != null) {
	       response.append(line11);
	       response.append('\r');
	     }
	     rd.close();
	     html = response.toString();
	}
	
	
	public void pingJiaoSave(List<NameValuePair> nameValuePairs) throws ClientProtocolException, IOException{
        httpPost = new HttpPost(WhutGlobal.URL_HEADER_STR + WhutGlobal.PINGJIAO_URL);
        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        httpPost.setHeader("Referer", WhutGlobal.URL_HEADER_STR + WhutGlobal.PINGJIAO_URL);
        HttpResponse response = httpClient.execute(httpPost);
        HttpEntity entity = response.getEntity();
        html = EntityUtils.toString(entity, "GB2312");
	}
	
	public void geRenKeBiaoPost(List<NameValuePair> nameValuePairs) throws ParseException, IOException{
        httpPost = new HttpPost(getGeRenKeBiaoGetString());
        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        httpPost.setHeader("Referer", getGeRenKeBiaoGetString());
        HttpResponse response = httpClient.execute(httpPost);
        HttpEntity entity = response.getEntity();
        html = EntityUtils.toString(entity, "GB2312");
	}
	
	
	
	public void getHtml(String getString) throws ClientProtocolException, UnsupportedEncodingException, IOException{
	     httpGet = new HttpGet(getString);
	     Log.i("TAG1", "innerside httpGet==null"+(httpGet==null));
	     httpGet.setHeader("Referer", WhutGlobal.URL_HEADER_STR +"xs_main.aspx?xh="+ WhutGlobal.USER_ID);
	     HttpResponse response;
		 response = httpClient.execute(httpGet);
         HttpEntity entity = response.getEntity();
         html = EntityUtils.toString(entity, "GB2312");
	}
	
	public void getChengJiHtml() throws ClientProtocolException, UnsupportedEncodingException, IOException{
	    httpPost = new HttpPost(WhutGlobal.URL_HEADER_STR + "xscj_gc.aspx?xh=" + WhutGlobal.USER_ID + "&xm=" + URLEncoder.encode(WhutGlobal.USER_NAME, "utf-8") + "&gnmkdm=N121605");
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
        nameValuePairs.add(new BasicNameValuePair("__VIEWSTATE", "dDwxNjgwNjIxMzEzO3Q8cDxsPHhoOz47bDwwMTIwOTAyMDkwMTMwOz4+O2w8aTwxPjs+O2w8dDw7bDxpPDE+O2k8Mz47aTw1PjtpPDc+O2k8OT47aTwxMT47aTwxMz47aTwxNj47aTwyNj47aTwyNz47aTwyOD47aTwzND47aTwzNj47aTwzOD47aTwzOT47aTw0MT47aTw0Mj47aTw0ND47aTw0Nj47aTw0OD47aTw2MD47aTw2NT47PjtsPHQ8cDxwPGw8VGV4dDs+O2w85a2m5Y+377yaMDEyMDkwMjA5MDEzMDs+Pjs+Ozs+O3Q8cDxwPGw8VGV4dDs+O2w85aeT5ZCN77ya6LS15LuV5Li5Oz4+Oz47Oz47dDxwPHA8bDxUZXh0Oz47bDzlrabpmaLvvJrkuqTpgJrlrabpmaI7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOS4k+S4mu+8mjs+Pjs+Ozs+O3Q8cDxwPGw8VGV4dDs+O2w86Ii56Ii25LiO5rW35rSLKOivleeCuSk7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOihjOaUv+ePre+8muiIueiItnN5MDkwMTs+Pjs+Ozs+O3Q8cDxwPGw8VGV4dDs+O2w8MjAwOTQxNDE7Pj47Pjs7Pjt0PHQ8O3Q8aTwxMz47QDxcZTsyMDAxLTIwMDI7MjAwMi0yMDAzOzIwMDMtMjAwNDsyMDA0LTIwMDU7MjAwNS0yMDA2OzIwMDYtMjAwNzsyMDA3LTIwMDg7MjAwOC0yMDA5OzIwMDktMjAxMDsyMDEwLTIwMTE7MjAxMS0yMDEyOzIwMTItMjAxMzs+O0A8XGU7MjAwMS0yMDAyOzIwMDItMjAwMzsyMDAzLTIwMDQ7MjAwNC0yMDA1OzIwMDUtMjAwNjsyMDA2LTIwMDc7MjAwNy0yMDA4OzIwMDgtMjAwOTsyMDA5LTIwMTA7MjAxMC0yMDExOzIwMTEtMjAxMjsyMDEyLTIwMTM7Pj47Pjs7Pjt0PHA8O3A8bDxvbmNsaWNrOz47bDx3aW5kb3cucHJpbnQoKVw7Oz4+Pjs7Pjt0PHA8O3A8bDxvbmNsaWNrOz47bDx3aW5kb3cuY2xvc2UoKVw7Oz4+Pjs7Pjt0PHA8cDxsPFZpc2libGU7PjtsPG88dD47Pj47Pjs7Pjt0PEAwPDs7Ozs7Ozs7Ozs+Ozs+O3Q8QDA8Ozs7Ozs7Ozs7Oz47Oz47dDxAMDw7Ozs7Ozs7Ozs7Pjs7Pjt0PEAwPDs7Ozs7Ozs7Ozs+Ozs+O3Q8QDA8Ozs7Ozs7Ozs7Oz47Oz47dDxAMDw7Ozs7Ozs7Ozs7Pjs7Pjt0PEAwPDs7Ozs7Ozs7Ozs+Ozs+O3Q8QDA8cDxwPGw8VmlzaWJsZTs+O2w8bzxmPjs+Pjs+Ozs7Ozs7Ozs7Oz47Oz47dDxAMDxwPHA8bDxWaXNpYmxlOz47bDxvPGY+Oz4+Oz47Ozs7Ozs7Ozs7Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPFxlOz4+Oz47Oz47dDxAMDw7Ozs7Ozs7Ozs7Pjs7Pjs+Pjs+Pjs+JsvyDM1SXaNiOiyp7t6sIVBUcHE="));
        nameValuePairs.add(new BasicNameValuePair("Button1", "按学期查询"));   
        nameValuePairs.add(new BasicNameValuePair("ddXN", ""));
        nameValuePairs.add(new BasicNameValuePair("ddXQ", ""));
        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        httpPost.setHeader("Referer", WhutGlobal.URL_HEADER_STR + "xscj_gc.aspx?xh=" + WhutGlobal.USER_ID + "&xm=" + URLEncoder.encode(WhutGlobal.USER_NAME, "utf-8") + "&gnmkdm=N121605");
        HttpResponse response = httpClient.execute(httpPost);
        HttpEntity entity = response.getEntity();
        html = EntityUtils.toString(entity, "GB2312");
	   }
	
	public String getKeBiaoGetString() throws UnsupportedEncodingException{
		return WhutGlobal.URL_HEADER_STR + "tjkbcx.aspx?xh=" + WhutGlobal.USER_ID + "&xm=" + URLEncoder.encode(WhutGlobal.USER_NAME, "utf-8") +"&gnmkdm=N121601";
	}
	
	public String getGeRenKeBiaoGetString() throws UnsupportedEncodingException{
		return WhutGlobal.URL_HEADER_STR + "xsxkqk.aspx?xh=" + WhutGlobal.USER_ID + "&xm=" + URLEncoder.encode(WhutGlobal.USER_NAME, "utf-8") + "&gnmkdm=N121615";
	}
	
	public boolean isNameAndPassRight(String html){
		Document document = Jsoup.parse(html);
		return !(document.select("#lbMm").size()>0);
	}
	
	public String getUserInfo(){
		Document document = Jsoup.parse(html);
		Elements s = document.select("span[id=xhxm]");
		return s.get(0).html().toString();
	}
	
	//testtesttest
	public String[][] getPingJiaoUrls(){
		Document document = Jsoup.parse(html);
		Elements a_s = document.select("a.top_link+ul").get(2).select("a");
		String[][] var = new String[a_s.size()][2];
		for(int i=0; i<a_s.size(); i++){
			var[i][0] = a_s.get(i).attr("href").toString();
			var[i][1] = a_s.get(i).html().toString();
		}
		return var;
	}
	
	public void getPingJiaoHtml(String getString) throws ClientProtocolException, UnsupportedEncodingException, IOException{
			getHtml(getString);
	}
	
	public String[][] getPingJiaoData1(){
		Document document = Jsoup.parse(html);
		Elements trs = document.select("#DataGrid1").select("tr");
		String[][] myResult = new String[trs.size()][4];
		for(int i=0; i<trs.size(); i++){
			Elements tds = trs.get(i).select("td");
			for(int j=0; j<4; j++){
				if(j!=3){
					myResult[i][j] = tds.get(j).html().toString();
				}else{
					myResult[i][j] = tds.get(j).select("option[selected=selected]").html().toString();
				}
			}
		}
		return myResult;
	}
	
	public String[][] getPingJiaoData() throws IndexOutOfBoundsException{
		Document document = Jsoup.parse(html);
		WhutGlobal.TIJIAO = document.select("#Button2").attr("value");
		WhutGlobal.DROPDOWN_LIST_STR = document.select("select[name*=DropDownList]").select("option").attr("value");
	    WhutGlobal.VIEW_STATE = document.select("input[name=__VIEWSTATE]").attr("value");
	    WhutGlobal.PART_URL = document.select("#pjkc").select("option[selected=selected]").attr("value");
		System.out.println("ppaarrttuurrll"+WhutGlobal.PART_URL);

		Elements table = document.select("#DataGrid1");
		String[][] myResult;
		if(table.get(0).select("table").size()==1){
			Elements trs = table.select("tr");
			myResult = new String[trs.size()][4];
			for(int i=0; i<trs.size(); i++){
				Elements tds = trs.get(i).select("td");
				for(int j=0; j<4; j++){
					if(j!=3){
						myResult[i][j] = tds.get(j).html().toString();
					}else{
						if(i!=0){
							myResult[i][j] = tds.get(j).select("option[selected=selected]").html().toString();
						}else{
							myResult[i][j] = tds.get(j).html().toString();
						}
					}
				}
			}
			System.out.println("姓名1："+myResult[0][3]);
		}else{
			Elements trs = table.select("tr");
			myResult = new String[trs.size()-1][4];
			myResult[0][0] = "一级指标";
			myResult[0][1] = "评价号";
			myResult[0][2] = "评价内容";
			for(int i=1; i<trs.size(); i++){
				Elements tds = trs.get(i).select("td");
				if(i>1){
					for(int j=0; j<4; j++){
						if(j!=3){
							myResult[i-1][j] = tds.get(j).html().toString();
						}else{
							myResult[i-1][j] = tds.get(j).select("option[selected=selected]").html().toString();
						}
					}
				}else{
					myResult[0][3] = trs.get(i).select("td").get(0).html().toString();
					System.out.println("姓名2："+myResult[0][3]);
				}
			}
		}
		return myResult;
	}
	

	

	
	public String[][] getKeBiaoData() throws IndexOutOfBoundsException{
	     Document document = Jsoup.parse(html);
	     Element table = document.select("Table").get(1);
	     Elements trs =  table.select("tr");
	     String fuck;
	 	 String[][] result = new String[4][5] ;
	     for(int i=1; i<5; i++){
	     	Element tr = trs.get(2*i);
	     	Elements tds = tr.select("td");
	     	if(tds.size()==9) tds.remove(0); 
	     	for(int j=1; j<6; j++){
	     		fuck = tds.get(j).html().toString().replaceAll("<br />", "\n").replaceAll("&nbsp;", "").replaceAll("\n\n\n", "\n\n");
	     		result[i-1][j-1] = fuck.endsWith("\n") ? fuck.substring(0, fuck.length()-1) : fuck;
	     	}
	     }
		return result;
	}
	
	
	public String[][] getGeRenKeBiaoData() throws IndexOutOfBoundsException{
		Document document = Jsoup.parse(html);
		Elements selects = document.select("#ddlXN");
		Elements options = selects.select("option");
		String dateLists[] = new String[options.size()];
		for(int i=0; i<options.size(); i++){
			dateLists[i] = options.get(i).attr("value");
		}
		WhutGlobal.DATE_LIST = dateLists;
		WhutGlobal.SELECTED_DATE = selects.select("option[selected=selected]").attr("value");
		WhutGlobal.SELECTED_TERM = document.select("#ddlXQ").select("option[selected=selected]").attr("value");
	    WhutGlobal.VIEW_STATE = document.select("input[name=__VIEWSTATE]").attr("value");

		Elements trs = document.select("Table").get(0).select("tr");
		String fuck;
		String[][] myResult = new String[trs.size()][8];
		for(int i=0; i<trs.size(); i++){
			Elements tds = trs.get(i).select("td");
			for(int j=1; j<9; j++ ){
				Element td = tds.get(j);
				fuck = td.select("a").size()==0 ? td.html().toString() : td.select("a").html().toString();
				myResult[i][j-1] = fuck.replaceAll("&nbsp;", "");
			}
		}
		return myResult;
	}
	
	
	public String[][] getChengJiData() throws IndexOutOfBoundsException{
		Document document = Jsoup.parse(html);
		Elements trs = document.select("Table").get(0).select("tr");
		String fuck;
		String[][] myResult = new String[trs.size()][15];
		for(int i=0; i<trs.size(); i++){
			Elements tds = trs.get(i).select("td");
			for(int j=0; j<15; j++ ){
				Element td = tds.get(j);
				fuck = td.html().toString() ;
				myResult[i][j] = fuck.replaceAll("&nbsp;", "");
			}
		}
		return myResult;
	}
	
    public void loginTu() throws ClientProtocolException, IOException{
        httpPost = new HttpPost("http://202.114.89.11/opac/reader/doLogin");
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
        nameValuePairs.add(new BasicNameValuePair("rdid", "0000139397"));
        nameValuePairs.add(new BasicNameValuePair("rdPasswd", "164515"));   
        nameValuePairs.add(new BasicNameValuePair("returnUrl", ""));  
        BasicHttpParams basicHttpParams = new BasicHttpParams();
        basicHttpParams.setParameter("rdid", "vikaspatidar");
        basicHttpParams.setParameter("rdPasswd", "patidar");
        basicHttpParams.setParameter("returnUrl", "");
        //httpPost.setParams(basicHttpParams);
        
        httpPost.setHeader("Referer", "http://202.114.89.11/opac/reader/login");
        httpPost.setHeader("Host", "202.114.89.11");
        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
        httpPost.setHeader("Accept-Language", "zh-CN");
        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
        HttpResponse response = httpClient.execute(httpPost);
        List<Cookie> cookies = ((AbstractHttpClient) httpClient).getCookieStore().getCookies();    
        if (cookies.isEmpty()) {    
            System.out.println("None");    
        } else {    
            for (int i = 0; i < cookies.size(); i++) {
            	if(cookies.get(i).getName().toString().equals("JSESSIONID")){
            		WhutGlobal.JSESSIONID = cookies.get(i).getValue().toString();
            	}
                Log.i(TAG,"JessionId is" + WhutGlobal.JSESSIONID ); 
            }    
        } 
		Log.i(TAG,"oooooooooooooo"+ response.getStatusLine().getStatusCode());
        HttpEntity entity = response.getEntity();
        //html = EntityUtils.toString(entity, "GB2312");
        //System.out.println("hh是大法师打发httttmmmmlol"+html);
        getLoginTuHtml();
   }
    
    public void loginTu1(){
		 try{
		 URL url = new URL("http://202.114.89.11/opac/reader/doLogin");
		 urlConnection = (HttpURLConnection) url.openConnection();
		 urlConnection.setDoOutput(true);
		 urlConnection.setDoInput(true);
		 urlConnection.setRequestMethod("POST");
		 urlConnection.setRequestProperty("Connection", "Keep-Alive");
		 urlConnection.setRequestProperty("Accept-Encoding", "gzip");
		 DataOutputStream wr = new DataOutputStream (
				   urlConnection.getOutputStream ());
		 String urlParameters = 
				 	"rdid="       + URLEncoder.encode("0000139397", "UTF-8")+
			        "&rdPasswd=" + URLEncoder.encode("164515", "UTF-8")+
			        "&returnUrl="         + URLEncoder.encode(" ", "UTF-8");
	     wr.writeBytes (urlParameters);
	     wr.flush ();
	     wr.close ();
	     InputStream is = urlConnection.getInputStream();
	      BufferedReader rd = new BufferedReader(new InputStreamReader(is));
	      String line1;
	      StringBuffer response4 = new StringBuffer(); 
	      while((line1 = rd.readLine()) != null) {
	        response4.append(line1);
	        response4.append('\r');
	      }
	      html = response4.toString();
	      System.out.println(urlConnection.getResponseCode()+"sdfsdffsdf"+response4.toString()+"asdfasdf");
	      rd.close();
		 }catch(Exception e){
			 
		 }
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
	
	//search books in the library system
    public void getSearchBookResultHtml() throws ClientProtocolException, IOException{
    	httpGet = new HttpGet("http://202.114.89.11/opac/index");
	    HttpResponse response;
		response = httpClient.execute(httpGet);
        List<Cookie> cookies = ((AbstractHttpClient) httpClient).getCookieStore().getCookies();    
        if (cookies.isEmpty()) {    
            System.out.println("None");    
        } else {    
            for (int i = 0; i < cookies.size(); i++) {
            	if(cookies.get(i).getName().toString().equals("JSESSIONID")){
            		WhutGlobal.JSESSIONID = cookies.get(i).getValue().toString();
            	}
                Log.i(TAG,"JessionId is" + WhutGlobal.JSESSIONID ); 
            }    
        } 
        httpGet.setHeader("Referer", "http://202.114.89.11/opac/index");
         httpGet.setHeader("Cookie", "JSESSIONID="+WhutGlobal.JSESSIONID);
    	httpGet = new HttpGet("http://202.114.89.11/opac/search?rows=10&searchWay0=marc&q0=&logical0=AND&q=matlab&searchWay=title&searchSource=readert");
		response = httpClient.execute(httpGet);
        HttpEntity entity = response.getEntity();
        html = EntityUtils.toString(entity, "GB2312");    	
        System.out.println( "-----------"+html);
    }
	
}
