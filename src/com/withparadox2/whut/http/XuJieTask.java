package com.withparadox2.whut.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.withparadox2.whut.dao.WhutGlobal;

import android.os.AsyncTask;

public class XuJieTask extends AsyncTask<Void, Void, Boolean>{

    private Callback callback;
    private List<NameValuePair> nameValuePairs;
    
	public interface Callback{
		public void onPostExecute(boolean reuslt);
	}
	
    
	public XuJieTask(Callback callback, List<NameValuePair> nameValuePairs){
		this.callback = callback;
		this.nameValuePairs = nameValuePairs;
	}
	@Override
    protected Boolean doInBackground(Void... params) {
	    // TODO Auto-generated method stub
		HttpPost httpPost = new HttpPost("http://202.114.89.11/opac/loan/doRenew");
        DefaultHttpClient httpClient = HttpHelper.getHttpClient();
		try {
	        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
    		HttpResponse response = httpClient.execute(httpPost);
    		HttpEntity entity = response.getEntity();
    		return EntityUtils.toString(entity, "GB2312").contains("Ðø½è³É¹¦");
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
        return false;
    }
    
	
	@Override
    protected void onPostExecute(Boolean result) {
	    // TODO Auto-generated method stub
        callback.onPostExecute(result);
    }
	
	

}
