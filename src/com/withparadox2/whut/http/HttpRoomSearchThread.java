package com.withparadox2.whut.http;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

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
import org.jsoup.select.Elements;

import android.os.Handler;
import android.os.Message;

import com.withparadox2.whut.dao.WhutGlobal;
import com.withparadox2.whut.ui.KongJiaoShiActivity;

public class HttpRoomSearchThread extends Thread {
	private HttpGet httpGet;
	private HttpClient httpClient = new DefaultHttpClient();
	private HttpResponse httpResponse;
	private HttpEntity entity;

	private String html;

	private Handler handler;
	private Message msg;

	public HttpRoomSearchThread(Handler handler) {
		// TODO Auto-generated constructor stub
		this.handler = handler;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		try {
			sendMyMessage(KongJiaoShiActivity.START_DOWNLOADING);
			getHtml();
			sendMyMessage(KongJiaoShiActivity.START_PARSING);
			parseHtml();
			sendMyMessage(KongJiaoShiActivity.GET_DATA_SUCCESS);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String getUrl(String buildingLoc, int time) {
		String s = (time == KongJiaoShiActivity.MORNING) ? "" : "2";
		return "http://chajiaoshi.com/free" + s + ".php?freelocation="
		        + URLEncoder.encode(buildingLoc);
	}

	private void getHtml() throws ClientProtocolException, IOException {
		httpGet = new HttpGet(getUrl("гр1", KongJiaoShiActivity.MORNING));
		httpResponse = httpClient.execute(httpGet);
		entity = httpResponse.getEntity();
		html = EntityUtils.toString(entity, "UTF-8");
	}

	private void parseHtml() {
		Document doc = Jsoup.parse(html);
		Elements navs = doc.select(".nav");
		Elements clears = doc.select(".clear");
		List<String> childListItem;
		Elements slibings = navs.get(0).siblingElements();

		int fistIndex, lastIndex;
		for (int i = 0; i < navs.size() - 1; i++) {
			fistIndex = (i == 0 ? 2 : slibings.indexOf(clears.get(i - 1)) + 2);
			lastIndex = slibings.indexOf(clears.get(i));
			WhutGlobal.ROOM_GROUP_LIST.add(navs.get(i).html().toString());
			List<Element> result = slibings.subList(fistIndex, lastIndex);
			childListItem = new ArrayList<String>();
			for (Element e : result)
				childListItem.add(e.html().toString());
			WhutGlobal.ROOM_CHILD_LIST.add(converList(childListItem));
		}
	}

	private void sendMyMessage(int arg) {
		msg = handler.obtainMessage();
		msg.arg1 = arg;
		msg.sendToTarget();
	}

	private List<String[]> converList(List<String> list) {
		int size = (int) Math.ceil(list.size() / 3.);
		int yuShu = size * 3 - list.size();
		List<String[]> listHolder = new ArrayList<String[]>();
		String[] s;
		switch (yuShu) {
		case 0:
			break;
		case 1:
			list.add("");
			break;
		case 2:
			list.add("");
			list.add("");
			break;
		}
		for (int i = 0; i < size; i++) {
			s = new String[3];
			s[0] = list.get(i * 3);
			s[1] = list.get(i * 3 + 1);
			s[2] = list.get(i * 3 + 2);
			listHolder.add(s);
		}
		return listHolder;
	}

}
