package com.withparadox2.whut.library.search;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import android.os.Handler;
import android.os.Message;

import com.withparadox2.whut.dao.WhutGlobal;

public class HttpSearchThread extends Thread{
	private HttpGet httpGet;
	private HttpClient httpClient = new DefaultHttpClient();
	private HttpResponse response;
	private String html;
	private int page;
	
	private static List<String> bookRecnoNumList = new ArrayList<String>();
	private static Map<String, String> bookRecnoNumValueMap = new HashMap<String, String>();//from bookRecnoNumList get value
	private Message msg;
	
	private Handler myHandler;
	
	public HttpSearchThread(Handler handler, int page){
		this.myHandler = handler;
		this.page = page;
	}
	public HttpSearchThread(Handler handler){
		this.myHandler = handler;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
			getGroupData();
	}
	
	private void sendMyMessage(int arg){
		msg = myHandler.obtainMessage();
		msg.arg1 = arg;
		msg.sendToTarget();
	}
	
	private void getGroupData(){
		try {
			sendMyMessage(SearchBookActivity.GET_HTML);
			getSearchBookResultHtml();
//			getBookListData();
			getBookListDataNew();
			sendMyMessage(SearchBookActivity.UPDATE_GROUP);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IndexOutOfBoundsException e) {
			// TODO: handle exception
			e.printStackTrace();
			sendMyMessage(SearchBookActivity.NO_BOOKS);
		}
	}
	
    private void getSearchBookResultHtml() throws ClientProtocolException, IOException{
//     	httpGet = new HttpGet("http://202.114.89.11/opac/search?rows=30&searchWay=title&q="+URLEncoder.encode(WhutGlobal.SEARCH_TITLE)+"&page="+page);
     	httpGet = new HttpGet("http://ms.lib.whut.edu.cn:8080/search?kw="+URLEncoder.encode(WhutGlobal.SEARCH_TITLE)+"&page="+page);
	    HttpResponse response;
		response = httpClient.execute(httpGet);
        HttpEntity entity = response.getEntity();
        html = EntityUtils.toString(entity, "UTF-8");    	
        System.out.println(html);
    }
    
    private void getBookListDataNew(){
    	 Document document = Jsoup.parse(html);
	     Elements books = document.select("ul li");
	     StringBuilder row;
	     for(Element element : books){
	    	 row = new StringBuilder("书        名：");
	    	 row.append(element.select("a").text().replaceAll("^\\s*\\d*、 ", "")).
	    			 append("\n").
	    			 append(element.select("div").html().replace(" ", "").
	    					 							 replace("&nbsp;&nbsp;", "\n").
	    					 							 replace("著者", "著        者").
	    					 							 replace("出版社：", "出  版  社："));
		     WhutGlobal.BOOKLIST.add(row.toString());
	     }
    }
    
}
