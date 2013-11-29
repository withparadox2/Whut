package com.withparadox2.whut.util;

import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.AbstractHttpClient;

import com.withparadox2.whut.dao.WhutGlobal;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

public class Helper {
    public static boolean isNetworkConnected(Context context) {
        if (context == null) {
            return false;
        }
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        return (activeNetInfo != null) && (activeNetInfo.isConnected());
    }
    
	public static void getCookie(HttpClient httpClient) {
		List<Cookie> cookies = ((AbstractHttpClient) httpClient).getCookieStore().getCookies();
		if (cookies.isEmpty()) {
			System.out.println("None");
		} else {
			for (int i = 0; i < cookies.size(); i++) {
				if (cookies.get(i).getName().toString().equals("JSESSIONID")) {
					WhutGlobal.JSESSIONID = cookies.get(i).getValue().toString();
					Log.i("HttpOperation", WhutGlobal.JSESSIONID);
				}
			}
		}
	}
    
    public static void showToast(Context context, String message, int duration) {
        Toast.makeText(context, message, duration).show();
    }

    public static void showShortToast(Context context, String message) {
        showToast(context, message, Toast.LENGTH_SHORT);
    }

    public static void showLongToast(Context context, String message) {
        showToast(context, message, Toast.LENGTH_LONG);
    }
    
    public static void saveValueInSharePreference(Context ctx, String spName, String Key, String value){
		SharedPreferences sharedPreferences = ctx.getSharedPreferences(spName, Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putString(Key, value);
		editor.commit();
    }
    
    public static String getValueInSharePreference(Context ctx, String spName, String Key, String value){
		SharedPreferences sharedPreferences = ctx.getSharedPreferences(spName, Context.MODE_PRIVATE);
        return sharedPreferences.getString(Key, value);
    }
}
