package com.withparadox2.whut;

import java.util.ArrayList;
import java.util.List;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.AbstractAction;
import com.withparadox2.whut.dao.UserInfoAdapter;
import com.withparadox2.whut.dao.WhutGlobal;
import com.withparadox2.whut.http.HttpOperateThread;
import com.withparadox2.whut.http.HttpOperation;

public class LoginActivity extends Activity{ 
	
	private EditText userIdEdit;
	private EditText userPasswordEdit;
	private TextView userIdTextView;
	private String userIdText;
	private String userPasswordText;
	private Button submitButton;
	private Button liXianButton;
	private Button ToTuButton;
	private Button ToJiaoButton;
	private CheckBox checkBox;
	private ImageButton popupButton;
	private PopupWindow popupWindow;
	private boolean isPopupOrNot = false;
	private ListView popupListView;
	private List<String> userIdList;
	private List<String> userPasswordList;
	private UserInfoAdapter dbHelper;
	private PopupListAdapter popupListAdapter;
	private boolean isRememberOrNot = false;
	private ProgressDialog progressDialog;
	private HttpOperateThread myThread;
	private UpdateUIHandler myHandler;
	private HttpOperation httpOperation;
	private ActionBar actionBar;
	private boolean cancelDialogByHand = false;
	private int closeHttpFlag;//是关闭httpurl还是关闭httppost
	private boolean jiaoTuFlag = true;//true for jiaowuchu, false for library

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		actionBar = (ActionBar) findViewById(R.id.login_actionbar);
        actionBar.setHomeAction(new TuOrJiaoAction());
        actionBar.addAction(new AboutAction());
		dbHelper = new UserInfoAdapter(this);
		dbHelper.open();
		userIdList = new ArrayList<String>();
		userPasswordList = new ArrayList<String>();
		initialize();
		setForJiaoOrTu();
		submitButton.setOnClickListener(new SubmitOnClickListener());
		popupButton.setOnClickListener(new PopupOnClickListener());
		checkBox.setOnCheckedChangeListener(new RememberCheckedListener());
		liXianButton.setOnClickListener(new LiXianOnClickListener());
		ToTuButton.setOnClickListener(new ToTuOnClickListener());
		ToJiaoButton.setOnClickListener(new ToJiaoOnClickListener());
		userIdEdit.setOnClickListener(new EditClickListener());
		myHandler = new UpdateUIHandler(Looper.myLooper());
		httpOperation = new HttpOperation(this);
	}
	
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		dbHelper.open();
	}
	
	
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		dbHelper.close();
	}

	class EditClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(!jiaoTuFlag){
				if(userIdEdit.getText().length()==0){
					userIdEdit.setText("0000");
					userIdEdit.setSelection(4);
				}
			}
		}
		
	}

	public class ToTuOnClickListener implements OnClickListener{

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			if(jiaoTuFlag){
				jiaoTuFlag = !jiaoTuFlag;
				setForJiaoOrTu();
			}
		}
		
	}
	
	public class ToJiaoOnClickListener implements OnClickListener{

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			if(!jiaoTuFlag){
				jiaoTuFlag = !jiaoTuFlag;
				setForJiaoOrTu();
			}
		}
		
	}

	private void setForJiaoOrTu(){
		//选择教务处或者图书馆
		if(jiaoTuFlag){
	        actionBar.setTitle("教务处");
	        userIdTextView.setText("学号  ");
	        liXianButton.setText("离线课表");
	        userIdEdit.setHint("请输入学号");
			getListFromDb(UserInfoAdapter.DATABASE_JIAO_TABLE_NAME);
			ToTuButton.setBackgroundResource(R.drawable.tujieo_focused_bg);
			ToJiaoButton.setBackgroundResource(R.drawable.tujiao_normal_bg);
		}else{
			actionBar.setTitle("图书馆");
	        userIdTextView.setText("卡号  ");
	        liXianButton.setText("离线查询");
	        userIdEdit.setHint("请输入卡号(前加4个0)");
			getListFromDb(UserInfoAdapter.DATABASE_TU_TABLE_NAME);
			ToTuButton.setBackgroundResource(R.drawable.tujiao_normal_bg);
			ToJiaoButton.setBackgroundResource(R.drawable.tujieo_focused_bg);
		}
		userIdEdit.setText("");
		userPasswordEdit.setText("");
		 if(popupListAdapter!=null) 		 popupListAdapter.notifyDataSetChanged();
	}
	
	public class TuOrJiaoAction extends AbstractAction{

		public TuOrJiaoAction() {
			super( R.drawable.ic_actionbar_whut);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void performAction(View view) {
			// TODO Auto-generated method stub
			jiaoTuFlag = !jiaoTuFlag;
			setForJiaoOrTu();
		}
		
	}

	public class AboutAction extends AbstractAction{

		public AboutAction() {
			super(R.drawable.ic_actionbar_about);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void performAction(View view) {
			// TODO Auto-generated method stub
			Intent i = new Intent();
			i.setClass(LoginActivity.this, InformationActivity.class);
			startActivity(i);
		}
		
	}
	
	private class LiXianOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(jiaoTuFlag){
				getLiXianKeBiaoData();
			}else{
				getLiXianJieYueData();
			}
		}
		
	}
	
	private void getLiXianKeBiaoData(){
		boolean exist = true;
		String[][] kebiao = new String[4][5];
		SharedPreferences share = getSharedPreferences(WhutGlobal.ShARE_LIXIAN_KEBIAO_NAME, Activity.MODE_PRIVATE);
		for(int i=0; i<4; i++){
			for(int j=0; j<5; j++){
				kebiao[i][j] = share.getString(""+(j+5*i), "不存在");
				if(kebiao[i][j].equals("不存在")) exist = false;
			}
		}
		
		if(exist){
			WhutGlobal.htmlData = kebiao;
			Intent i = new Intent();
			i.setClass(LoginActivity.this, LiXianKeBiaoActivity.class);
			startActivity(i);
		}else{
			Toast.makeText(LoginActivity.this, "未下载离线课表！", Toast.LENGTH_LONG).show();
		}
	}
	
	private void getLiXianJieYueData(){
		boolean exist = false;
		String[][] jieYue = null;
		SharedPreferences share = getSharedPreferences(WhutGlobal.SHARE_LIXIAN_JIEYUE_NAME, Activity.MODE_PRIVATE);
		int length = Integer.parseInt(share.getString("length", "100"));
		if(length<100){
			exist = true;
			jieYue = new String[length][3];
			for(int i=0; i<length; i++){
				jieYue[i][0] = share.getString("title"+i, "");
				jieYue[i][1] = share.getString("start_time"+i, "");
				jieYue[i][2] = share.getString("end_time"+i, "");
			}
		}
		
		if(exist){
			WhutGlobal.htmlData = jieYue;
			Intent i = new Intent();
			i.setClass(LoginActivity.this, LiXianJieYueActivity.class);
			startActivity(i);
		}else{
			Toast.makeText(LoginActivity.this, "未下载离线数据！", Toast.LENGTH_LONG).show();
		}
	}
	
    public static Intent createIntent(Context context) {
        Intent i = new Intent(context, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return i;
    }
	
	private class SubmitOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			if(httpOperation.isConnect(LoginActivity.this)){
				userIdText = userIdEdit.getText().toString().trim();
				userPasswordText = userPasswordEdit.getText().toString().trim();
				WhutGlobal.USER_ID = userIdText;
				WhutGlobal.USER_PASSWORD = userPasswordText;
				cancelDialogByHand = false;
				if(!userIdText.equals("")&&!userPasswordText.equals("")){
					WhutGlobal.USER_NAME = "";//进图书馆靠这个来判断
					if(jiaoTuFlag){
						updateIdAndPassword(UserInfoAdapter.DATABASE_JIAO_TABLE_NAME);
						WhutGlobal.WhichAction = 1;
					}else{
						updateIdAndPassword(UserInfoAdapter.DATABASE_TU_TABLE_NAME);
						WhutGlobal.WhichAction = 9;
					}
					myThread = new HttpOperateThread(LoginActivity.this, myHandler, httpOperation);
					progressDialog = new ProgressDialog(LoginActivity.this);
					progressDialog.setOnCancelListener(new DialogCancelListener());
//					isJumping = true;
					myThread.start();
				}else{
					if(jiaoTuFlag){
						Toast.makeText(LoginActivity.this, "密码或学号不能为空...", Toast.LENGTH_LONG).show();
					}else{
						Toast.makeText(LoginActivity.this, "密码或卡号不能为空...", Toast.LENGTH_LONG).show();
					}
				}
			}else{
				Toast.makeText(LoginActivity.this, "貌似没有联网...", Toast.LENGTH_LONG).show();
			}
		}
	}
	
	private class PopupOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {   
			// TODO Auto-generated method stub
			if(popupWindow == null){
				popupListView = new ListView(LoginActivity.this);
				popupListAdapter = new PopupListAdapter();
				popupListView.setAdapter(popupListAdapter);
				popupWindow = new PopupWindow(popupListView, userIdEdit.getWidth(), LayoutParams.WRAP_CONTENT);
				popupWindow.showAsDropDown(userIdEdit);
				isPopupOrNot = true;
			}else if(isPopupOrNot){
				popupWindow.dismiss();
				isPopupOrNot = false;
			}else if(!isPopupOrNot){
				popupWindow.showAsDropDown(userIdEdit);
				isPopupOrNot = true;
			}
		}
	}
	
	private class RememberCheckedListener implements OnCheckedChangeListener{

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			// TODO Auto-generated method stub
			isRememberOrNot = isChecked;
		}
		
	}
	
	private void getListFromDb(String table){
		userIdList.clear();
		userPasswordList.clear();
		Cursor c = dbHelper.fetchAllItems(table);
		startManagingCursor(c);
		if(c.getCount()!=0){
			c.moveToFirst();
			userIdList.add(c.getString(c.getColumnIndexOrThrow(UserInfoAdapter.KEY_USER_ID)));
			userPasswordList.add(c.getString(c.getColumnIndexOrThrow(UserInfoAdapter.KEY_USER_PASSWORD)));
			while(!c.isLast()){
				c.moveToNext();
				userIdList.add(c.getString(c.getColumnIndexOrThrow(UserInfoAdapter.KEY_USER_ID)));
				userPasswordList.add(c.getString(c.getColumnIndexOrThrow(UserInfoAdapter.KEY_USER_PASSWORD)));
			}
		}
		c.close();
	}
	
	private void updateIdAndPassword(String table){
		 long id = dbHelper.returnIdByUserId(table, userIdText);
		 if(id!=0){
			 if(isRememberOrNot){
				 dbHelper.updatePassword(table, id, userPasswordText);
			 }else{
				 dbHelper.updatePassword(table, id, "");
			 }
		 }else{
			 if(isRememberOrNot){
				 dbHelper.addItem(table, userIdText, userPasswordText);
			 }else{
				 dbHelper.addItem(table, userIdText, "");
			 }
		 }
		 getListFromDb(table);
		 if(popupListAdapter!=null) 		 popupListAdapter.notifyDataSetChanged();
	}
	
	private class PopupListAdapter extends BaseAdapter{
		
		LayoutInflater layoutInflater;
		public PopupListAdapter(){
			layoutInflater = LayoutInflater.from(LoginActivity.this);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return userIdList.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View converView, ViewGroup parent) {
			// TODO Auto-generated method stub
			
			final String userIdText = userIdList.get(position);
			final String userPasswordText = userPasswordList.get(position);
			Holder holder = null;
			if(converView==null){
				converView = layoutInflater.inflate(R.layout.login_popup_item, null);
				holder = new Holder();
				holder.popupText = (TextView) converView.findViewById(R.id.popup_text);
				holder.deleteButton = (ImageButton) converView.findViewById(R.id.popup_text_delete);
				converView.setTag(holder);
			}else{
				holder = (Holder)converView.getTag();
			}
			
			holder.popupText.setText(userIdText);
			holder.popupText.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					popupWindow.dismiss();
					isPopupOrNot = false;
					userIdEdit.setText(userIdText);
					userIdEdit.setSelection(userIdEdit.getText().length());
					if(userPasswordText.equals("")){
						checkBox.setChecked(false);
						isRememberOrNot = false;
					}else{
						checkBox.setChecked(true);
						isRememberOrNot = true;
					}
					userPasswordEdit.setText(userPasswordText);
					userPasswordEdit.setSelection(userPasswordEdit.getText().length());
				}
			});
			
			holder.deleteButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					if(jiaoTuFlag){
						long id = dbHelper.returnIdByUserId(UserInfoAdapter.DATABASE_JIAO_TABLE_NAME, userIdText);
						if(id!=0){
							dbHelper.deleteItem(UserInfoAdapter.DATABASE_JIAO_TABLE_NAME, id);
						}
						getListFromDb(UserInfoAdapter.DATABASE_JIAO_TABLE_NAME);
					}else{
						long id = dbHelper.returnIdByUserId(UserInfoAdapter.DATABASE_TU_TABLE_NAME, userIdText);
						if(id!=0){
							dbHelper.deleteItem(UserInfoAdapter.DATABASE_TU_TABLE_NAME, id);
						}
						getListFromDb(UserInfoAdapter.DATABASE_TU_TABLE_NAME);
					}
					popupListAdapter.notifyDataSetChanged();
				}
			});
			return converView;
		}
	}
	
	private class Holder{
		TextView popupText;
		ImageButton deleteButton;
	}
	
	private void initialize(){
		userIdEdit = (EditText) findViewById(R.id.loginUserId);
		userPasswordEdit = (EditText) findViewById(R.id.loginUserPassword);
		submitButton = (Button) findViewById(R.id.loginButton);
		popupButton = (ImageButton)findViewById(R.id.popup_button);
		checkBox = (CheckBox)findViewById(R.id.remember_password);
		liXianButton = (Button) findViewById(R.id.lixian_kebiao_button);
		userIdTextView = (TextView) findViewById(R.id.login_userid_text);
		ToTuButton = (Button) findViewById(R.id.to_tu_button);
		ToJiaoButton = (Button) findViewById(R.id.to_jiao_button);
	}
	
	public class UpdateUIHandler extends Handler{
		 public UpdateUIHandler(Looper looper){  
	            super(looper);  
	        }
		
	        @Override  
	        public void handleMessage(Message msg) {
	        	switch (msg.arg1) {
				case 0:
					//without getting url-header, go ahead to case 2.
					closeHttpFlag = 2;
					progressDialog.setMessage("正在上传数据...");
					progressDialog.show();
					break;
				case 1:
					progressDialog.setMessage("正在获取地址...");
					progressDialog.show();
					closeHttpFlag = 1;
					break;
				case 10:
					progressDialog.setMessage("正在请求数据...");
					progressDialog.show();
					closeHttpFlag = 3;
					break;
				case 2:
					closeHttpFlag = 2;
					progressDialog.setMessage("正在上传数据...");
					break;
				case 3:
					progressDialog.setMessage("正在下载数据...");
					break;
				case 4:
					progressDialog.setMessage("正在处理数据...");
					break;
				case 6:
					progressDialog.dismiss();
					Toast.makeText(LoginActivity.this, "不好意思，数据出现问题...", Toast.LENGTH_LONG).show();
					break;
				case 7:
					progressDialog.dismiss();
					Toast.makeText(LoginActivity.this, "不好意思，网络出现问题...", Toast.LENGTH_LONG).show();
					break;
				case 5:   
					progressDialog.dismiss();
					if(WhutGlobal.JUMP_OR_NOT&&(!cancelDialogByHand)){
						Intent i = new Intent();
						if(jiaoTuFlag){
							i.setClass(LoginActivity.this, WelcomeJiaoActivity.class);
						}else{
							i.setClass(LoginActivity.this, WelcomeTuActivity.class);
						}
						startActivity(i);
						LoginActivity.this.finish();
					}else{
						if(!cancelDialogByHand){
							if(jiaoTuFlag){
								Toast.makeText(LoginActivity.this, "密码或学号错误...", Toast.LENGTH_LONG).show();
							}else{
								Toast.makeText(LoginActivity.this, "密码或卡号错误...", Toast.LENGTH_LONG).show();
							}
						}
					}
					break;
				}
	        }
	}
	
	private class DialogCancelListener implements OnCancelListener{

		@Override
		public void onCancel(DialogInterface dialog) {
			// TODO Auto-generated method stub
			switch(closeHttpFlag){
			case 1:
				httpOperation.closeHttpGet();
				break;
			case 2:
				httpOperation.closeHttpUrl();
				break;
			case 3:
				httpOperation.closeHttpPost();
				break;
			}
			cancelDialogByHand = true;
		}
		
	}
}
