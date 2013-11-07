package com.withparadox2.whut.library;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.AbstractAction;
import com.markupartist.android.widget.ActionBar.IntentAction;
import com.withparadox2.whut.JieYueChaXunActivity;
import com.withparadox2.whut.LoginActivity;
import com.withparadox2.whut.R;
import com.withparadox2.whut.XuJieActivity;
import com.withparadox2.whut.R.drawable;
import com.withparadox2.whut.R.id;
import com.withparadox2.whut.R.layout;
import com.withparadox2.whut.dao.WhutGlobal;
import com.withparadox2.whut.http.HttpOperateThread;
import com.withparadox2.whut.http.HttpOperation;

public class WelcomeTuActivity extends Activity{
	private Button jieyueChaxunButton;
	private Button tushuXujieButton;
	private ProgressDialog progressDialog;
	private HttpOperateThread myThread;
	private UpdateUIHandler myHandler;
	private ActionBar actionBar;
	private boolean cancelDialogByHand = false;
	
	private HttpOperation httpOperation;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome_tu);
		if(savedInstanceState != null){
			WhutGlobal.USER_NAME = savedInstanceState.getString("USER_NAME");
			WhutGlobal.JSESSIONID = savedInstanceState.getString("JSESSIONID");
		}
		jieyueChaxunButton = (Button) findViewById(R.id.jieyue_chaxun_button);
		tushuXujieButton = (Button) findViewById(R.id.tushu_xujie_button);
		actionBar = (ActionBar) findViewById(R.id.welcometu_actionbar);
        actionBar.setHomeActionPic(new IntentAction(this, createIntent(this), R.drawable.ic_actionbar_whut));
        actionBar.setTitle("欢迎您："+ WhutGlobal.USER_NAME+"同学");
        actionBar.addAction(new ExitAppAction());
		myHandler = new UpdateUIHandler(Looper.myLooper());
        jieyueChaxunButton.setOnClickListener(new JieYueOnClickListener());
        tushuXujieButton.setOnClickListener(new XuJieOnClickListener());
		httpOperation = new HttpOperation(this);
	}
	
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putString("USER_NAME", WhutGlobal.USER_NAME);
		outState.putString("JSESSIONID", WhutGlobal.JSESSIONID);
	}
	
   public static Intent createIntent(Context context) {
        Intent i = new Intent(context, WelcomeTuActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return i;
    }
   
	public class ExitAppAction extends AbstractAction{

		public ExitAppAction() {
			super(R.drawable.ic_actionbar_exit);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void performAction(View view) {
			// TODO Auto-generated method stub
			exitActivity();
		}
		
	}
   
   class JieYueOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(httpOperation.isConnect(WelcomeTuActivity.this)){
				WhutGlobal.WhichAction = 10;
				cancelDialogByHand = false;
				progressDialog = new ProgressDialog(WelcomeTuActivity.this);
				progressDialog.setOnCancelListener(new DialogCancelListener());
				myThread = new HttpOperateThread(WelcomeTuActivity.this, myHandler, httpOperation);
				myThread.start();
			}else{
				Toast.makeText(WelcomeTuActivity.this, "貌似没有联网...", Toast.LENGTH_LONG).show();
			}
		}
	   
   }
   
   class XuJieOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(httpOperation.isConnect(WelcomeTuActivity.this)){
				WhutGlobal.WhichAction = 11;
				cancelDialogByHand = false;
				progressDialog = new ProgressDialog(WelcomeTuActivity.this);
				progressDialog.setOnCancelListener(new DialogCancelListener());
				myThread = new HttpOperateThread(WelcomeTuActivity.this, myHandler, httpOperation);
				myThread.start();
			}else{
				Toast.makeText(WelcomeTuActivity.this, "貌似没有联网...", Toast.LENGTH_LONG).show();
			}
		}
	   
   }
   
	private class DialogCancelListener implements OnCancelListener{   

		@Override
		public void onCancel(DialogInterface dialog) {
			// TODO Auto-generated method stub
			cancelDialogByHand = true;
			switch(WhutGlobal.WhichAction){
			case 10:
				httpOperation.closeHttpGet();
				break;
			case 11:
				httpOperation.closeHttpGet();
				break;
			}
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
						case 10:
							i.setClass(WelcomeTuActivity.this, JieYueChaXunActivity.class);
							startActivity(i);
							break;
						case 11:
							i.setClass(WelcomeTuActivity.this, XuJieActivity.class);
							startActivity(i);
							break;
						}
					}
					break;
				case 100:
					if(!cancelDialogByHand){
						Toast.makeText(WelcomeTuActivity.this, "不好意思，数据有问题...", Toast.LENGTH_LONG).show();
					}
					break;
				}
	        }
	}	
   	
	public void exitActivity(){
		Intent i = new Intent();
		i.setClass(WelcomeTuActivity.this, LoginActivity.class);
		startActivity(i);
		WelcomeTuActivity.this.finish();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode==KeyEvent.KEYCODE_BACK){
			exitActivity();
		}
		return true;
	}
	
}
