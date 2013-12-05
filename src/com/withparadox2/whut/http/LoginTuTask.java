package com.withparadox2.whut.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

import android.os.AsyncTask;
import com.withparadox2.whut.util.GlobalConstant;

public class LoginTuTask extends AsyncTask<String, Void, String>{
    
    private Callback callback;
	public interface Callback{
		void onPostExecute(String result);
	}
	
	public LoginTuTask(Callback callback){
		this.callback = callback;
	}

	@Override
    protected String doInBackground(String... params) {
	    // TODO Auto-generated method stub
		HttpPost httpPost = new HttpPost(GlobalConstant.LOGIN_TU_URL);
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
		nameValuePairs.add(new BasicNameValuePair("rdid", params[0]));
		nameValuePairs.add(new BasicNameValuePair("rdPasswd", params[1]));
		nameValuePairs.add(new BasicNameValuePair("returnUrl", ""));
		DefaultHttpClient httpClient = HttpHelper.getHttpClient();

		httpPost.setHeader("Referer", GlobalConstant.LOGIN_TU_REFERER);
		httpPost.setHeader("Accept-Language", "zh-CN");
		try {
	        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
    		HttpResponse response = httpClient.execute(httpPost);
            return getLoginTuData(EntityUtils.toString(response.getEntity()));
        } catch (UnsupportedEncodingException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        } catch (ClientProtocolException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        } catch (IOException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        } catch (IndexOutOfBoundsException e) {
			// TODO: handle exception
            e.printStackTrace();
            return "";
		}
	    return null;
    }

	@Override
    protected void onPostExecute(String result) {
	    // TODO Auto-generated method stub
	    callback.onPostExecute(result);
    }
    
	public String getLoginTuData(String html) throws IndexOutOfBoundsException{
        if(html.contains("用户名或密码不存在")){
        	return "";
        }else{
    		Document document = Jsoup.parse(html);
    		String s = document.select("div.navbar_info").html().toString();
    		Pattern pattern = Pattern.compile("欢迎您：(.*?)&nbsp");
    		Matcher matcher = pattern.matcher(s);
    		String name = "";
    		if (matcher.find())
    			name = matcher.group(1);
    		return name;
        }
	}
	

}
