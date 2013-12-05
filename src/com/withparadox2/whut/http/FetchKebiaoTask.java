package com.withparadox2.whut.http;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.os.AsyncTask;

import com.withparadox2.whut.util.GlobalConstant;

public class FetchKebiaoTask extends AsyncTask<Void, Void, String[][]>{
    
	private Callback callback;

	public interface Callback{
		void onPostExecute(String[][] result);
	}
    
	public FetchKebiaoTask (Callback callback){
		this.callback = callback;
	}
    
	@Override
    protected String[][] doInBackground(Void... params) {
	    // TODO Auto-generated method stub
		HttpGet httpGetTemp, httpGet;
		HttpResponse response = null;
		DefaultHttpClient httpClient = HttpHelper.getHttpClient();
		httpGetTemp = new HttpGet(GlobalConstant.KEBIAO_TEMP_URL);
        httpGet = new HttpGet(GlobalConstant.KEBIAO_URL);
        try {
        	response = httpClient.execute(httpGetTemp);
    		response = httpClient.execute(httpGet);
			int statusCode = response.getStatusLine().getStatusCode();
    		if (statusCode == GlobalConstant.HTTP_STATUS_OK) {
                return getKeBiaoData(EntityUtils.toString(response.getEntity()));
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
    protected void onPostExecute(String[][] result) {
	    // TODO Auto-generated method stub
        callback.onPostExecute(result);
    }



	private String[][] getKeBiaoData(String html) throws IndexOutOfBoundsException {
        System.out.println(html);
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

}
