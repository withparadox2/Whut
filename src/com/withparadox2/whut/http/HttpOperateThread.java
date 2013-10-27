package com.withparadox2.whut.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.withparadox2.whut.dao.WhutGlobal;

public class HttpOperateThread extends Thread{
	private Context context;
	private Handler myHandler;
	private HttpOperation httpOperation;
	private List<NameValuePair> nameValuePairs;
	private final String TAG = "HttpOperateThread"; 
	private Message msg;

	

	public HttpOperateThread(Context context, Handler myHandler, HttpOperation httpOperation) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.myHandler = myHandler;
		this.httpOperation = httpOperation;
		WhutGlobal.JUMP_OR_NOT = true;
	}
	
	public HttpOperateThread(Context context, Handler myHandler) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.myHandler = myHandler;
		httpOperation = new HttpOperation(context);
		WhutGlobal.JUMP_OR_NOT = true;
	}
	
	public HttpOperateThread(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
		httpOperation = new HttpOperation(context);
		WhutGlobal.JUMP_OR_NOT = true;
	}
	

	@Override
	public void run() {
		// TODO Auto-generated method stub
		switch(WhutGlobal.WhichAction){
		case 1:
			loginJiaoSubmit();
			break;
		case 2:
			keBiaoSubmit();
			break;
		case 3:
			geRenKeBiaoSubmit();
			break;
		case 4:
			chengJiSubmit();
			break;
		case 5:
			pingJiaoSubmit();
			break;
		case 6:
			pingJiaoSave();
			break;
		case 7:
			pingJiaoTiJiao();
			break;
		case 8:
			geRenKeBiaoPost();
			break;
		case 9:
			loginTuSubmit();
			break;
		case 10:
			jieYueChaXunSubmit();
			break;
		case 11:
			xuJieSubmit();
			break;
		case 12:
			xuJieSingle();
			break;
		}
	}
	
	private boolean urlHeaderExist(Context ctx){
		//如果存在就不获取了
		SharedPreferences share = ctx.getSharedPreferences("AppInfo", Activity.MODE_PRIVATE);
		String s = share.getString("URL_HEADER_STR", "empty");
		if(s.equals("empty")){
			return false;
		}else{
			WhutGlobal.URL_HEADER_STR = s;
			return true;
		}
	}
	
	private void loginJiaoSubmit(){
		int statusCode;
		int loginSuccessStatus;
		try {
			if(urlHeaderExist(context)){
				sendMyMessage(0);
			}else{
				sendMyMessage(1);
				httpOperation.getUrlHeader();
				sendMyMessage(2);
			}
			statusCode = httpOperation.loginJiao(WhutGlobal.USER_ID, WhutGlobal.USER_PASSWORD);
			loginSuccessStatus = httpOperation.ifLoginSuccessStatus();
			if((loginSuccessStatus==1)&&(statusCode==302)){
				sendMyMessage(3);
				httpOperation.getMethod(WhutGlobal.USER_ID);
				sendMyMessage(4);
				String userInfo[] = httpOperation.getUserInfo().split(" ");
				WhutGlobal.USER_ID = userInfo[0];
				WhutGlobal.USER_NAME = userInfo[1].replaceAll("同学", "");
				WhutGlobal.PINGJIAO_URLS = httpOperation.getPingJiaoUrls();
				sendMyMessage(5);	
			}else if(loginSuccessStatus==0){
				//网络有问题，wifi连上却没反应
				WhutGlobal.JUMP_OR_NOT = false;
				sendMyMessage(7);	
			}else{
				WhutGlobal.JUMP_OR_NOT = false;
				sendMyMessage(5);	
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			Log.i(TAG, "出现异常1。。。");
		} catch (IOException e) {
			WhutGlobal.JUMP_OR_NOT = false;
			sendMyMessage(6);	
			Log.i(TAG, "出现异常2。。。");
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
			//excute this after cancelling the httpUrlConnection
			Log.i(TAG, "停止网络");
		} catch (IndexOutOfBoundsException e) {
			//get the wrong info of user, often web is wrong
			WhutGlobal.JUMP_OR_NOT = false;
			sendMyMessage(6);	
		}
		keBiaoSubmit();
	}
	
	
	private void keBiaoSubmit(){
		sendMyMessage(1);
		try {
			httpOperation.getHtml("http://202.114.90.176:8080/DailyMgt/");
			httpOperation.getHtml("http://202.114.90.176:8080/DailyMgt/kbcx.do");
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
			WhutGlobal.htmlData = httpOperation.getKeBiaoData();
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
			Log.i(TAG, "html数据处理出现异常");
			WhutGlobal.JUMP_OR_NOT = false;
			sendMyMessage(100);
		}
		
		sendMyMessage(3);
	}
	
	private void geRenKeBiaoSubmit(){
		sendMyMessage(1);
		try {
			httpOperation.getHtml(httpOperation.getGeRenKeBiaoGetString());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		sendMyMessage(2);
		
		try{
			WhutGlobal.htmlData = httpOperation.getGeRenKeBiaoData();
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
			Log.i(TAG, "html数据处理出现异常");
			WhutGlobal.JUMP_OR_NOT = false;
			sendMyMessage(100);
		}
		sendMyMessage(3);
	}
	
	
	
	private void geRenKeBiaoPost(){
		sendMyMessage(1);
		try {
			httpOperation.geRenKeBiaoPost(nameValuePairs);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			Log.i(TAG, "html数据处理出现异常1");
		} catch (ParseException e) {
			Log.i(TAG, "html数据处理出现异常2");
			e.printStackTrace();
		} catch (IOException e) {
			Log.i(TAG, "html数据处理出现异常3");
			e.printStackTrace();
		}
		sendMyMessage(2);
		try{
			WhutGlobal.htmlData = httpOperation.getGeRenKeBiaoData();
		} catch (Exception e) {
			e.printStackTrace();
			Log.i(TAG, "html数据处理出现异常");
			WhutGlobal.JUMP_OR_NOT = false;
			sendMyMessage(100);
		}
		sendMyMessage(3);
	}
	
	private void chengJiSubmit(){
		sendMyMessage(1);
		try {
			httpOperation.getChengJiHtml();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		sendMyMessage(2);
		try{
			WhutGlobal.htmlData = httpOperation.getChengJiData();
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
			Log.i(TAG, "html数据处理出现异常");
			WhutGlobal.JUMP_OR_NOT = false;
			sendMyMessage(100);
		}
		sendMyMessage(3);
	}
	
	private void pingJiaoSubmit(){
		sendMyMessage(1);
		try {
			httpOperation.getPingJiaoHtml(WhutGlobal.URL_HEADER_STR + WhutGlobal.PINGJIAO_URL);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		sendMyMessage(2);
		try{
			WhutGlobal.htmlData = httpOperation.getPingJiaoData();
		}catch (IndexOutOfBoundsException e) {
			WhutGlobal.JUMP_OR_NOT = false;
			sendMyMessage(100);
		}
		sendMyMessage(3);
	}
	
	private void pingJiaoSave(){
	    sendMyMessage(1);
		try {
			httpOperation.pingJiaoSave(nameValuePairs);
		} catch (ClientProtocolException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		sendMyMessage(2);
		try{
			WhutGlobal.htmlData = httpOperation.getPingJiaoData();
		}catch (IndexOutOfBoundsException e) {
			WhutGlobal.JUMP_OR_NOT = false;
			sendMyMessage(100);
		}
		sendMyMessage(3);
	}
	
	private void pingJiaoTiJiao(){
		sendMyMessage(1);
		try {
			httpOperation.pingJiaoSave(nameValuePairs);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		sendMyMessage(2);
		try{
			WhutGlobal.htmlData = httpOperation.getPingJiaoData();
		} catch (Exception e) {
			WhutGlobal.JUMP_OR_NOT = false;
			sendMyMessage(100);
		}
		sendMyMessage(4);
		WhutGlobal.TIJIAO_SUCCESS = checkPingJiaoTiJiao();
		sendMyMessage(5);
	}
	
	public void setPostParas(List<NameValuePair> nameValuePairs){
		this.nameValuePairs = nameValuePairs;
	}
	
	private boolean checkPingJiaoTiJiao(){
		boolean result = false;
		try {
			httpOperation.getMethod(WhutGlobal.USER_ID);
			WhutGlobal.PINGJIAO_URLS = httpOperation.getPingJiaoUrls();
			result = (WhutGlobal.PINGJIAO_URLS.length==0);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	private void sendMyMessage(int arg){
		msg = myHandler.obtainMessage();
		msg.arg1 = arg;
		msg.sendToTarget();
	}
	
	private void loginTuSubmit(){
		sendMyMessage(10);
		try {
			httpOperation.loginTu();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sendMyMessage(4);
		try{
			httpOperation.getLoginTuData();
			if(WhutGlobal.USER_NAME.trim().equals("")) WhutGlobal.JUMP_OR_NOT = false;
		} catch (IndexOutOfBoundsException e) {
			// TODO: handle exception
			WhutGlobal.JUMP_OR_NOT = false;
			sendMyMessage(100);
		}
		sendMyMessage(5);
	}
	
	private void jieYueChaXunSubmit(){
		sendMyMessage(1);
		try {
			httpOperation.getJieYueHtml();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sendMyMessage(2);
		try{
			WhutGlobal.htmlData = httpOperation.getJieYueData();
		} catch (IndexOutOfBoundsException e) {
			// TODO: handle exception
			WhutGlobal.JUMP_OR_NOT = false;
			sendMyMessage(100);
		} catch (NegativeArraySizeException e) {
			// TODO: handle exception
			WhutGlobal.JUMP_OR_NOT = false;
			sendMyMessage(100);
		}
		sendMyMessage(3);
	}
	
	private void xuJieSubmit(){
		//进入续借界面
		sendMyMessage(1);
		try {
			httpOperation.getXuJieHtml();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sendMyMessage(2);
		try{
			WhutGlobal.htmlData = httpOperation.getXuJieData();
		} catch (NegativeArraySizeException e) {
			WhutGlobal.JUMP_OR_NOT = false;
			sendMyMessage(100);
		} catch (IndexOutOfBoundsException e) {
			WhutGlobal.JUMP_OR_NOT = false;
			sendMyMessage(100);
		}
		sendMyMessage(3);
	}
	
	private void xuJieSingle(){
		//提交一次续借
		WhutGlobal.CANCEL_DOWNLOAD_FLAG = false;
		sendMyMessage(1);//正在提交数据
		try {
			httpOperation.getXuJieSingleHtml(nameValuePairs);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		sendMyMessage(2);//正在处理结果
		httpOperation.xuJieSuccessFlag();
		
		if(!WhutGlobal.CANCEL_DOWNLOAD_FLAG){
			sendMyMessage(3);//续借成功，正在下载新数据
			try {
				httpOperation.getXuJieHtml();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			sendMyMessage(4);//续借成功，正在处理数据
			try{
				WhutGlobal.htmlData = httpOperation.getXuJieData();
			}  catch (NegativeArraySizeException e) {
				WhutGlobal.JUMP_OR_NOT = false;
				sendMyMessage(100);
			} catch (IndexOutOfBoundsException e) {
				WhutGlobal.JUMP_OR_NOT = false;
				sendMyMessage(100);
			}
		}
		sendMyMessage(5);
	}
}
