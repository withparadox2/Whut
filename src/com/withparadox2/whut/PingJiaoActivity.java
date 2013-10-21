package com.withparadox2.whut;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.AbstractAction;
import com.markupartist.android.widget.ActionBar.IntentAction;
import com.withparadox2.whut.dao.SaveTwoDimArray;
import com.withparadox2.whut.dao.WhutGlobal;
import com.withparadox2.whut.http.HttpOperateThread;
import com.withparadox2.whut.http.HttpOperation;

public class PingJiaoActivity extends Activity{
	ListView mListView1;
	MyAdapter myAdapter;
	LinearLayout mHead;
	ActionBar actionBar;
	String[][] result;
	ArrayAdapter<CharSequence> spinnerAdapter;
	private Map<String, Integer> selectedMap;
	private String[] selectedValues;
	private HttpOperateThread httpOperateThread;
	private List<NameValuePair> nameValuePairs;
	private TextView teacherNameTextView;
	private ProgressDialog progressDialog;
	private UpdateUIHandler myHandler;
	private int position;
	private String oldPartUrlString;
	private String nowPartUrlString;
	private boolean subButtonOn = false;
	private SharedPreferences share;
	private SharedPreferences.Editor edit;
	private boolean cancelDialogByHand = false;
	private HttpOperation httpOperation;
	private String viewState;
	private String dropDownList;
	private String pingJiaoUrl;
	private final String TAG = "PingJiaoActivity";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pingjiao);
		if(savedInstanceState!=null){
			SaveTwoDimArray save = (SaveTwoDimArray) savedInstanceState.getSerializable(SaveTwoDimArray.NAME);
			WhutGlobal.htmlData = save.getCustomArray();
			WhutGlobal.PINGJIAO_URLS = save.getPingJiaoUrls();
			WhutGlobal.PART_URL = savedInstanceState.getString("PART_URL");
			WhutGlobal.DROPDOWN_LIST_STR = savedInstanceState.getString("DROPDOWN_LIST_STR");
			WhutGlobal.PINGJIAO_URL = savedInstanceState.getString("PINGJIAO_URL");
			WhutGlobal.VIEW_STATE = savedInstanceState.getString("VIEW_STATE");
			WhutGlobal.URL_HEADER_STR = savedInstanceState.getString("URL_HEADER_STR");
			WhutGlobal.USER_ID = savedInstanceState.getString("USER_ID");
			WhutGlobal.USER_NAME = savedInstanceState.getString("USER_NAME");	
			WhutGlobal.TIJIAO = savedInstanceState.getString("TIJIAO");	
		}
		actionBar = (ActionBar) findViewById(R.id.pingjiao_actionbar);
		actionBar.addAction(new SaveAction());
		actionBar.addAction(new AutoFillAction());
		position = WhutGlobal.PINGJIAO_URL_POSITION;
		selectedMap = new HashMap<String, Integer>();
		selectedMap.put("", 0);
		selectedMap.put("A", 1);
		selectedMap.put("B", 2);
		selectedMap.put("C", 3);
		selectedMap.put("D", 4);
		selectedMap.put("E", 4);
		myHandler = new UpdateUIHandler(Looper.myLooper());
		actionBar.setHomeAction(new IntentAction(this, PrePingJiaoActivity.createIntent(this), R.drawable.ic_actionbar_whut));			
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(WhutGlobal.PINGJIAO_URLS[position][1]);	
		Log.i(TAG, "第一次position"+position);
		teacherNameTextView = (TextView)findViewById(R.id.pingjiao_teacherName);
		share = super.getSharedPreferences(WhutGlobal.USER_ID+"flagOfSaved", Activity.MODE_PRIVATE);
		initialData();
		mHead = (LinearLayout) findViewById(R.id.pingjiao_head);
		mHead.setFocusable(false);
		mHead.setClickable(false);
		mHead.setBackgroundColor(Color.parseColor("#FF5735"));
		
		mListView1 = (ListView) findViewById(R.id.pingjiao_listview);
		
		spinnerAdapter = ArrayAdapter.createFromResource(PingJiaoActivity.this,
		        R.array.pingjiao_spinner, android.R.layout.simple_spinner_item);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		myAdapter = new MyAdapter(this, R.layout.pingjiao_item);
		mListView1.setAdapter(myAdapter);
		httpOperation = new HttpOperation(this);
	}
	

	
	private boolean allSavedSuccess(){
		boolean result = true;
		for(int i=0; i<WhutGlobal.PINGJIAO_URLS.length; i++){
			if(share.getBoolean(""+i, false)==false){
				result = false;
			}
		}
		return result;
	}
	
	private void addSubmitButton(){
		if(!subButtonOn){
			actionBar.addAction(new SubmitAction());
			subButtonOn = true;
		}
	}
	
	public class SubmitAction extends AbstractAction{

		public SubmitAction() {
			super(R.drawable.ic_actionbar_submit);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void performAction(View view) {
			// TODO Auto-generated method stub
			oldPartUrlString = nowPartUrlString;
			WhutGlobal.WhichAction = 7;
			cancelDialogByHand = false;
			setNVPaisr("Button2");
			progressDialog = new ProgressDialog(PingJiaoActivity.this);
			progressDialog.setOnCancelListener(new DialogCancelListener());
			httpOperateThread = new HttpOperateThread(PingJiaoActivity.this, myHandler, httpOperation);
			httpOperateThread.setPostParas(nameValuePairs);
			httpOperateThread.start();
		}
		
	}
	
	public class SaveAction extends AbstractAction{

		public SaveAction() {
			super(R.drawable.ic_actionbar_save);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void performAction(View view) {
			// TODO Auto-generated method stub
			System.out.println("当前的"+WhutGlobal.PINGJIAO_URL);
			oldPartUrlString = nowPartUrlString;
			WhutGlobal.WhichAction = 6;
			cancelDialogByHand = false;
			setNVPaisr("Button1");
			progressDialog = new ProgressDialog(PingJiaoActivity.this);
			progressDialog.setOnCancelListener(new DialogCancelListener());
			httpOperateThread = new HttpOperateThread(PingJiaoActivity.this, myHandler, httpOperation);
			httpOperateThread.setPostParas(nameValuePairs);
			httpOperateThread.start();
		}
		
	}
	
	public class AutoFillAction extends AbstractAction{

		public AutoFillAction() {
			super(R.drawable.ic_actionbar_byonehand);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void performAction(View view) {
			// TODO Auto-generated method stub
			int length = selectedValues.length;
			for(int i=0; i<length; i++){
				selectedValues[i]="A";
			}
			selectedValues[(int)(Math.random()*length)]="B";
			myAdapter.notifyDataSetChanged();
		}
		
	}
	
	public class MyAdapter extends BaseAdapter {

		int id_row_layout;
		LayoutInflater mInflater;

		public MyAdapter(Context context, int id_row_layout) {
			super();
			this.id_row_layout = id_row_layout;
			mInflater = LayoutInflater.from(context);

		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return result.length-1;
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parentView) {
			ViewHolder holder = null;
			if (convertView == null) {
				synchronized (PingJiaoActivity.this) {
					convertView = mInflater.inflate(id_row_layout, null);
					holder = new ViewHolder();

					holder.txt1 = (TextView) convertView
							.findViewById(R.id.pingjiao_textView1);
					holder.txt3 = (TextView) convertView
							.findViewById(R.id.pingjiao_textView3);
					holder.spinner = (Spinner) convertView
							.findViewById(R.id.pingjiao_spinner);
					holder.spinner.setAdapter(spinnerAdapter);

					convertView.setTag(holder);
				}
			} else {
				holder = (ViewHolder) convertView.getTag();   
			}
			holder.txt1.setText(result[position+1][0]+"\n"+result[position+1][1]);
			holder.txt3.setText(result[position+1][2]);
			holder.spinner.setSelection(selectedMap.get(selectedValues[position]));
			holder.spinner.setOnItemSelectedListener(new SpinnerOnSelectedListener(position));
			return convertView;
		}

		class ViewHolder {
			TextView txt1;
			TextView txt3;
			Spinner spinner;
		}
		
		class SpinnerOnSelectedListener implements OnItemSelectedListener{
			private int position;
			
			public SpinnerOnSelectedListener(int position) {
				// TODO Auto-generated constructor stub
				this.position = position;
			}

			@Override
			public void onItemSelected(AdapterView<?> parent, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				selectedValues[position]=parent.getItemAtPosition(arg2).toString();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
			
		}
	}// end class my
	
	private void setNVPaisr(String whichButton){
		Pattern pattern = Pattern.compile("xkkh=(.*?)&xh=");
		Matcher matcher = pattern.matcher(pingJiaoUrl);
		String url = null;
		if(matcher.find()) url = matcher.group(1);
        nameValuePairs = new ArrayList<NameValuePair>(8+selectedValues.length);
        nameValuePairs.add(new BasicNameValuePair("__VIEWSTATE", viewState));
        nameValuePairs.add(new BasicNameValuePair("__EVENTTARGET", ""));
        nameValuePairs.add(new BasicNameValuePair("__EVENTARGUMENT", ""));
        nameValuePairs.add(new BasicNameValuePair(whichButton, "��  ��"));
        for(int i=0; i<selectedValues.length; i++){
        	 nameValuePairs.add(new BasicNameValuePair("DataGrid1:_ctl"+(i+2)+":JS1", selectedValues[i]));
        }
        nameValuePairs.add(new BasicNameValuePair("DropDownList1", dropDownList));
        nameValuePairs.add(new BasicNameValuePair("pjkc", url));
        nameValuePairs.add(new BasicNameValuePair("pjxx", ""));
        nameValuePairs.add(new BasicNameValuePair("TextBox1", "0"));
        nameValuePairs.add(new BasicNameValuePair("txt1", ""));
	}
	
	private boolean isSaveSuccess(){
		if(position==(WhutGlobal.PINGJIAO_URLS.length-1)){
			return true;
		}else{
			return !oldPartUrlString.equals(nowPartUrlString);
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
						initialData();
						updateUrl();
						actionBar.setTitle(WhutGlobal.PINGJIAO_URLS[position][1]);	
						Log.i(TAG, "第二次position"+position);
						myAdapter.notifyDataSetChanged();
					}
					break;
				case 4:
					progressDialog.setMessage("正在验证提交...");
					break;
				case 5:
					progressDialog.dismiss();
					if(WhutGlobal.JUMP_OR_NOT&&(!cancelDialogByHand)){
						initialData();
						updateUrl();
						myAdapter.notifyDataSetChanged();
						if(WhutGlobal.TIJIAO_SUCCESS){
							Toast.makeText(PingJiaoActivity.this, "提交成功...", Toast.LENGTH_LONG).show();
//							edit.clear();
//							edit.commit();
							PingJiaoActivity.this.finish();
						}else{
							Toast.makeText(PingJiaoActivity.this, "提交失败...", Toast.LENGTH_LONG).show();
						}
					}
					break;
				case 100:
					if(!cancelDialogByHand){
						Toast.makeText(PingJiaoActivity.this, "不好意思，数据有问题...", Toast.LENGTH_LONG).show();
					}
					break;
				}
	        }
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		SaveTwoDimArray mySave = SaveTwoDimArray.getSingletonObject();
		mySave.setCustomArray(result);
		mySave.setPingJiaoUrls(WhutGlobal.PINGJIAO_URLS);
		outState.putSerializable(SaveTwoDimArray.NAME, mySave);
		outState.putString("PART_URL", WhutGlobal.PART_URL);
		outState.putString("DROPDOWN_LIST_STR", WhutGlobal.DROPDOWN_LIST_STR);
		outState.putString("PINGJIAO_URL", WhutGlobal.PINGJIAO_URL);
		outState.putString("VIEW_STATE", WhutGlobal.VIEW_STATE);
		outState.putString("URL_HEADER_STR", WhutGlobal.URL_HEADER_STR);
		outState.putString("USER_ID", WhutGlobal.USER_ID);
		outState.putString("USER_NAME", WhutGlobal.USER_NAME);
		outState.putString("TIJIAO", WhutGlobal.TIJIAO);
	}
	
	private void initialData(){
		result = WhutGlobal.htmlData;
		viewState = WhutGlobal.VIEW_STATE;
		dropDownList = WhutGlobal.DROPDOWN_LIST_STR;
		pingJiaoUrl = WhutGlobal.PINGJIAO_URL;
		selectedValues = new String[result.length-1];
		nowPartUrlString = WhutGlobal.PART_URL;
		for(int i=1; i<result.length;i++){
			selectedValues[i-1] = result[i][3];
		}
		teacherNameTextView.setText(result[0][3]);
		if(WhutGlobal.TIJIAO.trim().equals("提  交")||allSavedSuccess()){
			addSubmitButton();
		}
	}
	
	private void updateUrl(){
		if(isSaveSuccess()){
			edit = share.edit();
			edit.putBoolean(""+position, true);
			edit.commit();
			if(position<(WhutGlobal.PINGJIAO_URLS.length-1)){
				position++;
			}
			Log.i(TAG, "增加position"+position);
			WhutGlobal.PINGJIAO_URL = WhutGlobal.PINGJIAO_URLS[position][0];
			pingJiaoUrl = WhutGlobal.PINGJIAO_URL;
		}
		
	}
	
	private class DialogCancelListener implements OnCancelListener{

		@Override
		public void onCancel(DialogInterface dialog) {
			// TODO Auto-generated method stub
			cancelDialogByHand = true;
			httpOperation.closeHttpPost();
		}
	}
	
}
