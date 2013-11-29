package com.withparadox2.whut.ui;

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
import com.withparadox2.whut.R;
import com.withparadox2.whut.R.drawable;
import com.withparadox2.whut.R.id;
import com.withparadox2.whut.R.layout;
import com.withparadox2.whut.dao.SaveTwoDimArray;
import com.withparadox2.whut.dao.WhutGlobal;
import com.withparadox2.whut.http.FetchChengjiTask;
import com.withparadox2.whut.http.HttpKebiaoThread;
import com.withparadox2.whut.http.HttpOperateThread;
import com.withparadox2.whut.http.HttpOperation;
import com.withparadox2.whut.util.GlobalConstant;
import com.withparadox2.whut.util.Helper;
import com.withparadox2.whut.http.FetchChengjiTask;

public class WelcomeJiaoActivity extends Activity {

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

		timeTableButton = (Button) findViewById(R.id.time_table_button);
		geRenKeBiaoButton = (Button) findViewById(R.id.gerenkebiao_button);
		chengJiButton = (Button) findViewById(R.id.chengji_button);
		pingjiaoButton = (Button) findViewById(R.id.pingjiao_button);
		actionBar = (ActionBar) findViewById(R.id.welcomen_actionbar);
        actionBar.setHomeAction(new IntentAction(this, MainActivity.createIntent(this), R.drawable.ic_actionbar_whut));
		actionBar.setTitle("��ӭ����" + Helper.getValueInSharePreference(this, GlobalConstant.SP_LOCAL_TEMP, GlobalConstant.USER_NAME, "") + "ͬѧ");
		myHandler = new UpdateUIHandler(Looper.myLooper());

		timeTableButton.setOnClickListener(new TimeTableOnClickListener());
		geRenKeBiaoButton.setOnClickListener(new GeRenKeBiaoOnClickListener());
		chengJiButton.setOnClickListener(new ChengJiOnClickListener());
		pingjiaoButton.setOnClickListener(new PingJiaoOnClickListener());
		httpOperation = new HttpOperation(this);
	}


	public static Intent createIntent(Context context) {
		Intent i = new Intent(context, WelcomeJiaoActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		return i;
	}

	public class TimeTableOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
//			WhutGlobal.WhichAction = 2;
//			cancelDialogByHand = false;
//			progressDialog = new ProgressDialog(WelcomeJiaoActivity.this);
//			progressDialog.setOnCancelListener(new DialogCancelListener());
//			new HttpKebiaoThread(myHandler).start();
//            System.out.println("��½���ǿα�fuck");
            Intent i = new Intent();
            i.setClass(WelcomeJiaoActivity.this, KebiaoActivity.class);
            startActivity(i);
		}

	}

	public class GeRenKeBiaoOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
            Intent i = new Intent();
            i.setClass(WelcomeJiaoActivity.this, XueFenXueJiActivity.class);
            startActivity(i);		
		}

	}

	public class ChengJiOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent i = new Intent();
            i.setClass(WelcomeJiaoActivity.this, ChengJiActivity.class);
            startActivity(i);
		}

	}

	public class PingJiaoOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent i = new Intent();
			// i.setClass(WelcomeJiaoActivity.this, PrePingJiaoActivity.class);
			startActivity(i);
		}

	}

	public class UpdateUIHandler extends Handler {
		public UpdateUIHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.arg1) {
			case 1:
				progressDialog.setMessage("·��������Զ��...");
				progressDialog.show();
				break;
			case 2:
				progressDialog.setMessage("���ھ׽�����...");
				break;
			case 3:
				progressDialog.dismiss();
				if (WhutGlobal.JUMP_OR_NOT && (!cancelDialogByHand)) {
					Intent i = new Intent();
					switch (WhutGlobal.WhichAction) {
					case 2:
						i.setClass(WelcomeJiaoActivity.this,
						        KebiaoActivity.class);
						startActivity(i);
						break;
					case 3:
						// i.setClass(WelcomeJiaoActivity.this,
						// GeRenKeBiaoActivity.class);
						startActivity(i);
						break;
					case 4:
						// i.setClass(WelcomeJiaoActivity.this,
						// ChengJiActivity.class);
						startActivity(i);
						break;
					case 5:
						// i.setClass(WelcomeJiaoActivity.this,
						// PrePingJiaoActivity.class);
						startActivity(i);
						break;
					}
				}
				break;
			case 100:
				if (!cancelDialogByHand) {
					Toast.makeText(WelcomeJiaoActivity.this, "������˼������������...",
					        Toast.LENGTH_LONG).show();
				}
				break;
			}
		}
	}

	private class DialogCancelListener implements OnCancelListener {

		@Override
		public void onCancel(DialogInterface dialog) {
			// TODO Auto-generated method stub
			cancelDialogByHand = true;
			switch (WhutGlobal.WhichAction) {
			case 2:
				httpOperation.closeHttpGet();
				break;
			case 4:
				httpOperation.closeHttpPost();
				break;
			}
		}
	}



}
