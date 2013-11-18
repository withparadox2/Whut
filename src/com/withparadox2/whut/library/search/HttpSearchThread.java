package com.withparadox2.whut.library.search;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import org.jsoup.parser.Parser;
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
		if(WhutGlobal.WhichAction == SearchBookActivity.UPDATE_GROUP_THREAD){
			getGroupData();
		}else{
			getChildData();
		}
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
    }
    
    private void getBookListDataNew(){
    	 Document document = Jsoup.parse(html);
	     Elements books = document.select("ul li");
	     StringBuilder row;
	     String bookNum;
	     String item[];
	     ArrayList<String[]> childArrayList;
	     for(Element element : books){
	    	 row = new StringBuilder("��        ����");
	    	 row.append(element.select("a").text().replaceAll("^\\s*\\d*�� ", "")).
	    			 append("\n").
	    			 append(element.select("div").html().replace(" ", "").
	    					 							 replace("&nbsp;&nbsp;", "\n").
	    					 							 replace("����", "��        ��").
	    					 							 replace("�����磺", "��  ��  �磺"));
	    	 
	    	 bookNum = getBookNumber(element.select("a").attr("href"));
	    	 item = new String[2];
	    	 item[0] = row.toString();
	    	 item[1] = bookNum;
		     WhutGlobal.BOOKLIST.add(item);
		     WhutGlobal.CLICK_GROUP_FLAG.add(false);
		     childArrayList = new ArrayList<String[]>();
             WhutGlobal.CHILDLIST.add(childArrayList);
	     }
    }
    
    private String getBookNumber(String sourceUrl){
    	System.out.println(sourceUrl);
    	Pattern pattern = Pattern.compile("%2f(\\d+)%3f");
    	Matcher matcher = pattern.matcher(sourceUrl);
    	if(matcher.find()) {
            return matcher.group(1);
        }else{
        	return "";
        }
    }
    
    private void getChildHtml(String bookNum) throws ClientProtocolException, IOException{
    	httpGet = new HttpGet("http://ms.lib.whut.edu.cn:8080/search?d=http%3a%2f%2f202.114.89.11%2fopac%2fbook%2f"+bookNum);
	    HttpResponse response;
		response = httpClient.execute(httpGet);
        HttpEntity entity = response.getEntity();
        html = EntityUtils.toString(entity, "UTF-8");    	
        System.out.println(html);
    }
    
    private void getChildData1(){
    	Document document = Jsoup.parse(html);
    	Elements elements = document.select("tbody");
    	Elements tds;
    	String[] childItem;
    	for(Element e:elements){
    		childItem = new String[3];
    		tds = e.select("td");
    		childItem[0] = tds.get(0).text();
    		childItem[1] = tds.get(2).text();
    		childItem[2] = tds.get(4).text();
    		WhutGlobal.CHILDLIST.get(WhutGlobal.BOOK_CODE_POS).add(childItem);
    	}
    	sendMyMessage(SearchBookActivity.UPDATE_CHILD);
    }
    
    private String getChildXml() throws ClientProtocolException, IOException{
        String url = "http://202.114.89.11/opac/book/holdingpreview/" + WhutGlobal.BOOK_CODE;
        httpGet = new HttpGet(url);
        HttpResponse response;
        response = httpClient.execute(httpGet);
	    HttpEntity entity = response.getEntity();
    return EntityUtils.toString(entity, "UTF-8");   
}

private void getChildData(){
        Document xmlParse = null;
        String s[];
            try {
                    xmlParse = Jsoup.parse(getChildXml(), "", Parser.xmlParser());
            } catch (ClientProtocolException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
            } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
            }
        Elements callnos = xmlParse.select("callno");
        Elements locations = xmlParse.select("curlocalName");
        for(int i=0; i<callnos.size(); i++){
                s = new String[2];
                s[0] = callnos.get(i).text();
                s[1] = locations.get(i).text();
                WhutGlobal.CHILDLIST.get(WhutGlobal.BOOK_CODE_POS).add(s);
        }
        sendMyMessage(SearchBookActivity.UPDATE_CHILD);
}
}
