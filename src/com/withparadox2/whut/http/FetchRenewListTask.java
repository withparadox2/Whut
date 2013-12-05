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
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.os.AsyncTask;

public class FetchRenewListTask extends AsyncTask<Void, Void, String[][]>{

    private Callback callback;
    
    public interface Callback{
    	public void onPostExecute(String[][] result);
    }
    
    public FetchRenewListTask(Callback callback){
    	this.callback = callback;
    }
    
	@Override
    protected String[][] doInBackground(Void... params) {
		HttpPost httpPost = new HttpPost("http://202.114.89.11/opac/loan/renewList");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
		nameValuePairs.add(new BasicNameValuePair("rows", "30"));
		DefaultHttpClient httpClient = HttpHelper.getHttpClient();
        try {
	        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
    		HttpResponse response = httpClient.execute(httpPost);
    		return getXuJieData(EntityUtils.toString(response.getEntity()));
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
	    // TODO Auto-generated method stub
    }
    
	public String[][] getXuJieData(String html) throws IndexOutOfBoundsException,
	        NegativeArraySizeException {
		Document document = Jsoup.parse(html);
		Elements trs = document.select("#contentTable").select("tr");
		String[][] myResult = null;
		if (trs.size() != 0) {
    		myResult = new String[trs.size() - 1][9];
		}
		for (int i = 1; i < trs.size(); i++) {
			Elements tds;
			tds = trs.get(i).select("td");
			for (int j = 1; j < 10; j++) {
				Element td = tds.get(j);
				myResult[i - 1][j - 1] = td.text();
			}
		}
		return myResult;
	}
	

	@Override
    protected void onPostExecute(String[][] result) {
	    // TODO Auto-generated method stub
		callback.onPostExecute(result);
    }
    

}
