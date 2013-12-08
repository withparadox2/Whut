package com.withparadox2.whut.http;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
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
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.withparadox2.whut.dao.WhutGlobal;
import com.withparadox2.whut.ui.SearchBookActivity;

public class HttpSearchThread extends Thread {
	private HttpGet httpGet;
	private HttpClient httpClient = new DefaultHttpClient();
	private HttpResponse response;
	private String html;
	private int page;

	private Message msg;

	private Handler myHandler;
	private int groupClickPosition;

	public HttpSearchThread(Handler handler, int page) {
		this.myHandler = handler;
		this.page = page;
	}

	public HttpSearchThread(Handler handler, int groupClickPosition, Context ctx) {
		this.myHandler = handler;
		this.groupClickPosition = groupClickPosition;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		if (WhutGlobal.WhichAction == SearchBookActivity.UPDATE_GROUP_THREAD) {
			getGroupData();
		} else {
			getChildData();
		}
	}

	private void sendMyMessage(int arg) {
		msg = myHandler.obtainMessage();
		msg.arg1 = arg;
		msg.sendToTarget();
	}

	private void getGroupData() {
		try {
			getSearchBookResultHtml();
			if (getBookListData() == 0) {
				sendMyMessage(SearchBookActivity.NO_BOOKS);
			} else {
				sendMyMessage(SearchBookActivity.UPDATE_GROUP);
			}
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

	private void getSearchBookResultHtml() throws ClientProtocolException,
	        IOException {
		httpGet = new HttpGet("http://ms.lib.whut.edu.cn:8080/search?kw="
		        + URLEncoder.encode(WhutGlobal.SEARCH_TITLE) + "&page=" + page
		        + "&searchtype=" + WhutGlobal.SEARCH_METHOD);
		HttpResponse response;
		response = httpClient.execute(httpGet);
		HttpEntity entity = response.getEntity();
		html = EntityUtils.toString(entity, "UTF-8");
	}

	/**
	 * 加载时添加图书到全局列表中，并返回此次图书的数量
	 * 
	 * @return
	 */
	private int getBookListData() {
		Document document = Jsoup.parse(html);
		Elements books = document.select("ul li");
		StringBuilder row;
		String bookNum;
		String item[];
		ArrayList<String[]> childArrayList;
		for (Element element : books) {
			row = new StringBuilder("书        名：");
			row.append(element.select("a").text().replaceAll("^\\s*\\d*、 ", ""))
			        .append("\n")
			        .append(element.select("div").html().replace(" ", "")
			                .replace("&nbsp;&nbsp;", "\n")
			                .replace("著者", "著        者")
			                .replace("出版社：", "出  版  社："));

			bookNum = getBookNumber(element.select("a").attr("href"));
			item = new String[2];
			item[0] = row.toString();
			item[1] = bookNum;
			WhutGlobal.BOOKLIST.add(item);
			WhutGlobal.CLICK_GROUP_FLAG.add(false);
			childArrayList = new ArrayList<String[]>();
            String[] init = {"正在查询...", ""}; 
            childArrayList.add(init);
			WhutGlobal.CHILDLIST.add(childArrayList);
		}
		return books.size();
	}

	private String getBookNumber(String sourceUrl) {
		System.out.println(sourceUrl);
		Pattern pattern = Pattern.compile("%2f(\\d+)%3f");
		Matcher matcher = pattern.matcher(sourceUrl);
		if (matcher.find()) {
			return matcher.group(1);
		} else {
			return "";
		}
	}

	private String getChildXml() throws ClientProtocolException, IOException {
		String url = "http://202.114.89.11/opac/book/holdingpreview/"
		        + WhutGlobal.BOOK_CODE;
		httpGet = new HttpGet(url);
		HttpResponse response;
		response = httpClient.execute(httpGet);
		HttpEntity entity = response.getEntity();
		return EntityUtils.toString(entity, "UTF-8");
	}

	private void getChildData() {
		Document xmlParse = null;
        List<String[]> childList = WhutGlobal.CHILDLIST.get(groupClickPosition);
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
		for (int i = 0; i < callnos.size(); i++) {
			s = new String[2];
			s[0] = callnos.get(i).text();
			s[1] = locations.get(i).text();
			childList.add(s);
		}

		if (callnos.size() == 0) {
            childList.remove(0);
            String[] noBooks = {"未查询到书籍...", ""};
            childList.add(noBooks);
			sendMyMessage(SearchBookActivity.NO_BOOKS);
		} else {
            childList.remove(0);
			sendMyMessage(SearchBookActivity.UPDATE_CHILD);
		}
	}
}