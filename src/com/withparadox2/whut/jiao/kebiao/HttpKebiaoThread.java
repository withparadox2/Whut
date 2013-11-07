package com.withparadox2.whut.jiao.kebiao;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.withparadox2.whut.dao.WhutGlobal;

public class HttpKebiaoThread extends Thread{
	private HttpGet httpGet;
	private HttpClient httpClient = new DefaultHttpClient();
	private HttpResponse response;
	private String html;
	private Message msg;
	
	private Handler myHandler;
	
	public HttpKebiaoThread(Handler handler){
		this.myHandler = handler;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		keBiaoSubmit();
	}
	
	private void sendMyMessage(int arg){
		msg = myHandler.obtainMessage();
		msg.arg1 = arg;
		msg.sendToTarget();
	}
	private void keBiaoSubmit(){
		sendMyMessage(1);
		try {
			getKebiaoHtml("http://202.114.90.176:8080/DailyMgt/kbcx.do");
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sendMyMessage(2);
		try {
			WhutGlobal.htmlData = getKeBiaoData();
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
			WhutGlobal.JUMP_OR_NOT = false;
			sendMyMessage(100);
		}
		sendMyMessage(3);
	}

	private void getKebiaoHtml(String getString) throws ClientProtocolException, UnsupportedEncodingException, IOException{
	     httpGet = new HttpGet(getString);
	     Log.i("TAG1", "innerside httpGet==null"+(httpGet==null));
	     httpGet.setHeader("Cookie", WhutGlobal.JSESSIONID);
		 response = httpClient.execute(httpGet);
         HttpEntity entity = response.getEntity();
         html = EntityUtils.toString(entity, "GB2312");
         System.out.println(html);
	}
	
	private String[][] getKeBiaoData() throws IndexOutOfBoundsException{
	     Document document = Jsoup.parse(html);
	     Elements trs = document.select("#weekTable tbody tr");
	     String tempStr;
	 	 String[][] result = new String[4][5] ;
	     for(int i=0; i<4; i++){
	     	Elements tds = trs.get(i).select("td");
	     	for(int j=1; j<6; j++){
	     		tempStr = tds.get(j).select("div").html().toString().replaceAll("¡ó", "\n").replaceAll("&nbsp;", "");
	     		result[i][j-1] = tempStr;
	     	}
	     }
		return result;
	}
	
	
}
