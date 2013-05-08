package com.withparadox2.whut;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.IntentAction;
import com.withparadox2.whut.dao.SaveTwoDimArray;
import com.withparadox2.whut.dao.WhutGlobal;
import com.withparadox2.whut.http.HttpOperateThread;
import com.withparadox2.whut.http.HttpOperation;

public class PrePingJiaoActivity extends Activity{
	private ListView listView;
	private PreAdapter myAdapter;
	private String[][] result;
	private HttpOperateThread httpOperateThread;
	private UpdateUIHandler myHandler;
	private ProgressDialog progressDialog;
	private SharedPreferences share;
	private ActionBar actionBar;
	private boolean cancelDialogByHand = false;
	private HttpOperation httpOperation;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.prepingjiao);
		if(savedInstanceState != null){
			SaveTwoDimArray mySave = (SaveTwoDimArray) savedInstanceState.getSerializable(SaveTwoDimArray.NAME);
			WhutGlobal.PINGJIAO_URLS = mySave.getPingJiaoUrls();
			WhutGlobal.URL_HEADER_STR = savedInstanceState.getString("URL_HEADER_STR");
			WhutGlobal.USER_ID = savedInstanceState.getString("USER_ID");
			WhutGlobal.USER_NAME = savedInstanceState.getString("USER_NAME");	
		}
		result = WhutGlobal.PINGJIAO_URLS;
		myHandler = new UpdateUIHandler(Looper.myLooper());
		listView = (ListView) findViewById(R.id.prepingjiao_listview);
		myAdapter = new PreAdapter(PrePingJiaoActivity.this);
		listView.setAdapter(myAdapter);
		listView.setOnItemClickListener(new MyItemClickListener());
		actionBar = (ActionBar)findViewById(R.id.prepingjiao_actionbar);
		actionBar.setHomeAction(new IntentAction(this, WelcomeJiaoActivity.createIntent(this), R.drawable.ic_actionbar_whut));			
		actionBar.setDisplayHomeAsUpEnabled(true);
		if(result.length==0){
			actionBar.setTitle("不能评教或者已经评完");	
		}else{
			actionBar.setTitle("评教");	
		}
			
		share = super.getSharedPreferences(WhutGlobal.USER_ID+"flagOfSaved", Activity.MODE_PRIVATE);
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
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		result = WhutGlobal.PINGJIAO_URLS;
		myAdapter.notifyDataSetChanged();
		if(result.length==0){
			actionBar.setTitle("不能评教或者已经评完");	
		}else{
			actionBar.setTitle("评教");	
		}
		super.onResume();
	}
	


	public class MyItemClickListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
			// TODO Auto-generated method stub
			WhutGlobal.PINGJIAO_URL_POSITION = position;
			WhutGlobal.PINGJIAO_URL = result[position][0];
			WhutGlobal.WhichAction = 5;
			cancelDialogByHand = false;
			progressDialog = new ProgressDialog(PrePingJiaoActivity.this);
			progressDialog.setOnCancelListener(new DialogCancelListener());
			httpOperateThread = new HttpOperateThread(PrePingJiaoActivity.this, myHandler, httpOperation);
			httpOperateThread.start();
		}
		
	}
	public class PreAdapter extends BaseAdapter{
		private Context context;
		private LayoutInflater myInflater;
		public PreAdapter(Context ctx) {
			// TODO Auto-generated constructor stub
			this.context = ctx;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return result.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder = new ViewHolder();
			if(convertView == null){
				synchronized (PrePingJiaoActivity.this) {
					myInflater = LayoutInflater.from(context);
					convertView = myInflater.inflate(R.layout.prepingjiao_item, null);
					holder.text1 = (TextView) convertView.findViewById(R.id.prepingjiao_text1);
					holder.text2 = (TextView) convertView.findViewById(R.id.prepingjiao_text2);
					convertView.setTag(holder);
				}
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.text1.setText(result[position][1]);
			if(share.getBoolean(""+position, false)){
				holder.text2.setText("已评");
			}else{
				holder.text2.setText("未评");
			}
			return convertView;
		}
		
		class ViewHolder{
			TextView text1;
			TextView text2;
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
					i.setClass(PrePingJiaoActivity.this, PingJiaoActivity.class);
					startActivity(i);
				}
				break;
			case 100:
				if(!cancelDialogByHand){
					Toast.makeText(PrePingJiaoActivity.this, "不好意思，数据有问题...", Toast.LENGTH_LONG).show();
				}
				break;
			}
        }
   }
	
    public static Intent createIntent(Context context) {
        Intent i = new Intent(context, PrePingJiaoActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return i;
    }
   
	private class DialogCancelListener implements OnCancelListener{

		@Override
		public void onCancel(DialogInterface dialog) {
			// TODO Auto-generated method stub
			cancelDialogByHand = true;
			httpOperation.closeHttpGet();
		}
	}
   
	   
}
