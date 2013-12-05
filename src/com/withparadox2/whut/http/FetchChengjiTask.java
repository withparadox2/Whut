package com.withparadox2.whut.http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;
import android.os.AsyncTask;

import com.withparadox2.whut.util.GlobalConstant;
import com.withparadox2.whut.util.Helper;

public class FetchChengjiTask extends AsyncTask<Void, Void, String[][]>{

	private Callback callback;
	private Context context;
	public interface Callback{
		public void onPostExecute(String[][] result);

	}

	public FetchChengjiTask(Context context, Callback callback){
		this.callback = callback;
		this.context = context;
	}

	@Override
	protected String[][] doInBackground(Void... params) {
		// TODO Auto-generated method stub
		HttpGet httpGet = new HttpGet(GlobalConstant.CHENGJI_TEMP_URL);
		HttpPost httpPost = new HttpPost(GlobalConstant.CHENGJI_URL);
		HttpResponse httpResponse = null;
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
		nameValuePairs.add(new BasicNameValuePair("numPerPage", "500"));
		nameValuePairs.add(new BasicNameValuePair("pageNum", "1"));
		nameValuePairs.add(new BasicNameValuePair("xh", Helper.getValueInSharePreference(context, GlobalConstant.SP_LOCAL_TEMP, GlobalConstant.USER_ID, "")));
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
			DefaultHttpClient defaultHttpClient = HttpHelper.getHttpClient();
			HttpParams httpParams = defaultHttpClient.getParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, GlobalConstant.TIMEOUT_SECONDS * 1000);
			HttpConnectionParams.setSoTimeout(httpParams, GlobalConstant.TIMEOUT_SECONDS * 1000);
			defaultHttpClient.execute(httpGet);
			httpResponse = defaultHttpClient.execute(httpPost);
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode == GlobalConstant.HTTP_STATUS_OK) {
				return getChengjiData(EntityUtils.toString(httpResponse.getEntity()));
			} else {
				throw new IOException(httpResponse.getStatusLine().getReasonPhrase());
			}
		}catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IndexOutOfBoundsException e) {
			// TODO: handle exception
            e.printStackTrace();
		}finally{
			HttpHelper.releaseConnection(httpResponse);
		}
		return null;
	}


	@Override
	protected void onPostExecute(String[][] result) {
		// TODO Auto-generated method stub
		callback.onPostExecute(result);
	}

	private String[][] getChengjiData(String html){
		Document document = Jsoup.parse(html);
		Elements trs = document.select("tr[target=sid_cj_id]");
		int size = trs.size();
		String[][] result = new String[size][6];
		for(int i=0; i<size; i++){
			Elements tds = trs.get(i).select("td");
			result[i][0] = tds.get(2).text();
			result[i][1] = tds.get(0).text();
			result[i][2] = tds.get(3).text();
			result[i][3] = tds.get(4).text();
			result[i][4] = tds.get(5).text();
			result[i][5] = tds.get(10).text();
		}
		return result;
	}

}
