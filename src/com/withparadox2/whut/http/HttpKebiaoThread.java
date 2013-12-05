package com.withparadox2.whut.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.withparadox2.whut.dao.WhutGlobal;

public class HttpKebiaoThread extends Thread {
	private HttpGet httpGet;
	private HttpClient httpClient = HttpHelper.getHttpClient();
	private HttpResponse response;
	private String html;
	private Message msg;

	private Handler myHandler;

	public HttpKebiaoThread(Handler handler) {
		this.myHandler = handler;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		keBiaoSubmit();
	}

	private void sendMyMessage(int arg) {
		msg = myHandler.obtainMessage();
		msg.arg1 = arg;
		msg.sendToTarget();
	}

	private void keBiaoSubmit() {
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

	private void getKebiaoHtml(String getString)
	        throws ClientProtocolException, UnsupportedEncodingException,
	        IOException {
        getJs();
		httpGet = new HttpGet(getString);
		Log.i("TAG1", "innerside httpGet==null" + (httpGet == null));
//		httpGet.setHeader("Cookie", "JSESSIONID=0E94D7F1C17CCBA25CF43D9C4511480F");
		response = httpClient.execute(httpGet);
		HttpEntity entity = response.getEntity();
		html = EntityUtils.toString(entity, "GB2312");
		System.out.println(html);
		getcj();
		getChengJiHtml("http://202.114.90.172:8080/Score/lscjList.do");
	}

	private String[][] getKeBiaoData() throws IndexOutOfBoundsException {
		Document document = Jsoup.parse(html);
		Elements trs = document.select("#weekTable tbody tr");
		String tempStr;
		String[][] result = new String[4][5];
		for (int i = 0; i < 4; i++) {
			Elements tds = trs.get(i).select("td");
			for (int j = 1; j < 6; j++) {
				tempStr = tds.get(j).select("div").html().toString()
				        .replaceAll("¡ó", "\n").replaceAll("&nbsp;", "");
				result[i][j - 1] = tempStr;
			}
		}
		return result;
	}
	
	public void getJs()throws ClientProtocolException,
    UnsupportedEncodingException, IOException {
        httpGet = new HttpGet("http://202.114.90.176:8080/DailyMgt/");
//        httpGet.setHeader("Cookie", "JSESSIONID="+"336463A125DFCEB62A6FF0F4AB4FEBE3");
        httpClient.execute(httpGet);
    }
    
	public void getcj()throws ClientProtocolException,
    UnsupportedEncodingException, IOException {
        httpGet = new HttpGet("http://202.114.90.172:8080/Score/");
//        httpGet.setHeader("Cookie", "JSESSIONID="+"336463A125DFCEB62A6FF0F4AB4FEBE3");
        httpClient.execute(httpGet);
    }
    
	private void getChengJiHtml(String getString)
	        throws ClientProtocolException, UnsupportedEncodingException,
	        IOException {
        getJs();
		httpGet = new HttpGet(getString);
		response = httpClient.execute(httpGet);
		HttpEntity entity = response.getEntity();
		html = EntityUtils.toString(entity, "GB2312");
		System.out.println(html);
	}
}
