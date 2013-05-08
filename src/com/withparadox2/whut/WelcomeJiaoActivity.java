package com.withparadox2.whut;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.AbstractAction;
import com.markupartist.android.widget.ActionBar.IntentAction;
import com.withparadox2.whut.dao.SaveTwoDimArray;
import com.withparadox2.whut.dao.WhutGlobal;
import com.withparadox2.whut.http.HttpOperateThread;
import com.withparadox2.whut.http.HttpOperation;

public class WelcomeJiaoActivity extends Activity{
	
	private Button timeTableButton;
	private Button geRenKeBiaoButton;
	private Button pingjiaoButton;
	private Button chengJiButton;
	private ProgressDialog progressDialog;
	private HttpOperateThread myThread;
	private UpdateUIHandler myHandler;
	private ActionBar actionBar;
	private final String TAG = "WelcomeJiaoActivity";
	private boolean cancelDialogByHand = false;
	
	private HttpOperation httpOperation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);
		if(savedInstanceState != null){
			SaveTwoDimArray mySave = (SaveTwoDimArray) savedInstanceState.getSerializable(SaveTwoDimArray.NAME);
			WhutGlobal.PINGJIAO_URLS = mySave.getPingJiaoUrls();
			WhutGlobal.URL_HEADER_STR = savedInstanceState.getString("URL_HEADER_STR");
			WhutGlobal.USER_ID = savedInstanceState.getString("USER_ID");
			WhutGlobal.USER_NAME = savedInstanceState.getString("USER_NAME");	
			Log.i(TAG, WhutGlobal.USER_NAME);
		}
		saveUrlHeaderOnFirstLogin();
		timeTableButton = (Button) findViewById(R.id.time_table_button);
		geRenKeBiaoButton = (Button) findViewById(R.id.gerenkebiao_button);
		chengJiButton = (Button) findViewById(R.id.chengji_button);
		pingjiaoButton = (Button) findViewById(R.id.pingjiao_button);
		actionBar = (ActionBar) findViewById(R.id.welcomen_actionbar);
        actionBar.setHomeActionPic(new IntentAction(this, createIntent(this), R.drawable.ic_actionbar_whut));
        actionBar.setTitle("欢迎您："+ WhutGlobal.USER_NAME+"同学");
        actionBar.addAction(new ExitAppAction());
		myHandler = new UpdateUIHandler(Looper.myLooper());
		
		timeTableButton.setOnClickListener(new TimeTableOnClickListener());
		geRenKeBiaoButton.setOnClickListener(new GeRenKeBiaoOnClickListener());
		chengJiButton.setOnClickListener(new ChengJiOnClickListener());
		pingjiaoButton.setOnClickListener(new PingJiaoOnClickListener());
		httpOperation = new HttpOperation(this);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		SaveTwoDimArray mySave = SaveTwoDimArray.getSingletonObject();
		mySave.setPingJiaoUrls(WhutGlobal.PINGJIAO_URLS);
		outState.putSerializable(SaveTwoDimArray.NAME, mySave);
		outState.putString("URL_HEADER_STR", WhutGlobal.URL_HEADER_STR);
		outState.putString("USER_ID", WhutGlobal.USER_ID);
		outState.putString("USER_NAME", WhutGlobal.USER_NAME);
		Log.i(TAG, "调用保存临时变量");
	}
	
	private void saveUrlHeaderOnFirstLogin(){
		SharedPreferences share = getSharedPreferences("AppInfo", Activity.MODE_PRIVATE);
		String url = share.getString("URL_HEADER_STR", "empty");
		if(url.equals("empty")){
			SharedPreferences.Editor edit = share.edit();
			edit.putString("URL_HEADER_STR", WhutGlobal.URL_HEADER_STR);
			edit.commit();
		}
	}
	
	public class ExitAppAction extends AbstractAction{

		public ExitAppAction() {
			super(R.drawable.ic_actionbar_exit);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void performAction(View view) {
			// TODO Auto-generated method stub
			exitDialog();
		}
		
	}
	
	   public static Intent createIntent(Context context) {
	        Intent i = new Intent(context, WelcomeJiaoActivity.class);
	        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        return i;
	    }
	   
	public class TimeTableOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			WhutGlobal.WhichAction = 2;
			cancelDialogByHand = false;
			progressDialog = new ProgressDialog(WelcomeJiaoActivity.this);
			progressDialog.setOnCancelListener(new DialogCancelListener());
			myThread = new HttpOperateThread(WelcomeJiaoActivity.this, myHandler, httpOperation);
			myThread.start();
		}
		
	}
	
	public class GeRenKeBiaoOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			WhutGlobal.WhichAction = 3;
			cancelDialogByHand = false;
			progressDialog = new ProgressDialog(WelcomeJiaoActivity.this);
			progressDialog.setOnCancelListener(new DialogCancelListener());
			myThread = new HttpOperateThread(WelcomeJiaoActivity.this, myHandler, httpOperation);
			myThread.start();
		}
		
	}
	
	public class ChengJiOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			WhutGlobal.WhichAction = 4;
			cancelDialogByHand = false;
			progressDialog = new ProgressDialog(WelcomeJiaoActivity.this);
			progressDialog.setOnCancelListener(new DialogCancelListener());
			myThread = new HttpOperateThread(WelcomeJiaoActivity.this, myHandler, httpOperation);
			myThread.start();
		}
		
	}
	
	public class PingJiaoOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent i = new Intent();
			i.setClass(WelcomeJiaoActivity.this, PrePingJiaoActivity.class);
			startActivity(i);
		}
		
	}
	
	public class UpdateUIHandler extends Handler{
		 public UpdateUIHandler(Looper looper){  
	            super(looper);  
	        }
		
	        @Override  
	        public void handleMessage(Message msg) {
	        	switch (msg.arg1) {
				case 1:
					progressDialog.setMessage("路漫漫其修远兮...");
					progressDialog.show();
					break;
				case 2:
					progressDialog.setMessage("正在咀嚼数据...");
					break;
				case 3:
					progressDialog.dismiss();
					if(WhutGlobal.JUMP_OR_NOT&&(!cancelDialogByHand)){
						Intent i = new Intent();
						switch(WhutGlobal.WhichAction){
						case 2:
							i.setClass(WelcomeJiaoActivity.this, ScrollTableActivity.class);
							startActivity(i);
							break;
						case 3:
							i.setClass(WelcomeJiaoActivity.this, GeRenKeBiaoActivity.class);
							startActivity(i);
							break;
						case 4:
							i.setClass(WelcomeJiaoActivity.this, ChengJiActivity.class);
							startActivity(i);
							break;
						case 5:
							i.setClass(WelcomeJiaoActivity.this, PrePingJiaoActivity.class);
							startActivity(i);
							break;
						}
					}
					break;
				case 100:
					if(!cancelDialogByHand){
						Toast.makeText(WelcomeJiaoActivity.this, "不好意思，数据有问题...", Toast.LENGTH_LONG).show();
					}
					break;
				}
	        }
	}
	
	private class DialogCancelListener implements OnCancelListener{

		@Override
		public void onCancel(DialogInterface dialog) {
			// TODO Auto-generated method stub
			cancelDialogByHand = true;
			switch(WhutGlobal.WhichAction){
			case 2:
				httpOperation.closeHttpGet();
				break;
			case 4:
				httpOperation.closeHttpPost();
				break;
			}
		}
	}
	
	public void exitDialog(){
		Dialog dialog = new AlertDialog.Builder(WelcomeJiaoActivity.this)
				.setIcon(R.drawable.ic_actionbar_whut)
				.setTitle("Hello!")
				.setMessage("你要去何处？")
				.setPositiveButton("注销", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						Intent i = new Intent();
						i.setClass(WelcomeJiaoActivity.this, LoginActivity.class);
						startActivity(i);
						WelcomeJiaoActivity.this.finish();
					}
				}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.cancel();
					}
				}).create();
		dialog.show();
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode==KeyEvent.KEYCODE_BACK){
			exitDialog();
		}
		return true;
	}
	

}
