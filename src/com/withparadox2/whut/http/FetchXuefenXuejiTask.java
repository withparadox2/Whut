package com.withparadox2.whut.http;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.withparadox2.whut.util.GlobalConstant;

import android.os.AsyncTask;

public class FetchXuefenXuejiTask extends AsyncTask<Void, Void, ArrayList<String[]>>{

    
    private Callback callback;
    private ArrayList<String[]> list = new ArrayList<String[]>();
   
    public interface Callback{
    	public void onPostExecute(ArrayList<String[]> result);
    }
    
    public FetchXuefenXuejiTask(Callback callback){
    	this.callback = callback;
    }
	@Override
    protected ArrayList<String[]> doInBackground(Void... params) {
	    // TODO Auto-generated method stub
		HttpGet httpGetTemp, httpGet;
		HttpResponse response = null;
		DefaultHttpClient httpClient = HttpHelper.getHttpClient();
		httpGetTemp = new HttpGet(GlobalConstant.CHENGJI_TEMP_URL);
        httpGet = new HttpGet(GlobalConstant.XUEFEN_URL);
        try {
        	response = httpClient.execute(httpGetTemp);
    		response = httpClient.execute(httpGet);
			int statusCode = response.getStatusLine().getStatusCode();
    		if (statusCode == GlobalConstant.HTTP_STATUS_OK) {
                return getXueFenData(EntityUtils.toString(response.getEntity()));
             } else {
                 throw new IOException(response.getStatusLine().getReasonPhrase());
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
		}finally{
        	if(response != null){
			    HttpHelper.releaseConnection(response);
			}
        }
	    return null;
    }
    
	@Override
    protected void onPostExecute(ArrayList<String[]> result) {
	    // TODO Auto-generated method stub
        callback.onPostExecute(result);
    }
	
	private ArrayList<String[]> getXueFenData(String html){
		String[] input;
		Document document = Jsoup.parse(html);
		Elements jidians = document.select("input");
		input = new String[3];
		input[0] = jidians.get(0).attr("value");
		input[1] = jidians.get(1).attr("value");
		input[2] = "";
        list.add(input);
		input = new String[3];
		input[0] = jidians.get(2).attr("value");
		input[1] = jidians.get(3).attr("value");
		input[2] = "";
        list.add(input);
        
        Elements trs = document.select("tr[target=sid_user]");
        Elements tds;
        String[] row;
        for(Element tr:trs){
        	tds = tr.select("td");
        	row = new String[3];
        	row[0] = tds.get(0).text();
            row[1] = tds.get(1).text();
            row[2] = tds.get(2).text();
            list.add(row);
        }
		return list;
	}

    
}
