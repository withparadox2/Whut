package com.withparadox2.whut;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.AbstractAction;
import com.markupartist.android.widget.ActionBar.IntentAction;
import com.withparadox2.whut.MyHScrollView.OnScrollChangedListener;
import com.withparadox2.whut.dao.SaveTwoDimArray;
import com.withparadox2.whut.dao.WhutGlobal;
import com.withparadox2.whut.http.HttpOperateThread;
import com.withparadox2.whut.http.HttpOperation;


public class GeRenKeBiaoActivity extends Activity {
    /** Called when the activity is first created. */
	ListView mListView1;
	MyAdapter myAdapter;
	RelativeLayout mHead;
	String[][] result;
	private ActionBar actionBar;
	private HttpOperateThread httpOperateThread;
	private UpdateUIHandler myHandler;
	private ProgressDialog progressDialog;
	private boolean cancelDialogByHand = false;
	private HttpOperation httpOperation;
	public ArrayAdapter<String> dialogSpinnerAdapter;
	private final String TAG = "GeRenKeBiaoActivity";
	private String[] dateList;
	private String selectedTerm;
	private String selectedDate;
	private String viewState;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.geren_kebiao);
		if(savedInstanceState!=null){
			SaveTwoDimArray save = (SaveTwoDimArray) savedInstanceState.getSerializable(SaveTwoDimArray.NAME);
			WhutGlobal.htmlData = save.getCustomArray();
			WhutGlobal.PINGJIAO_URLS = save.getPingJiaoUrls();
			WhutGlobal.DATE_LIST = savedInstanceState.getStringArray("DATE_LIST");
			WhutGlobal.SELECTED_DATE = savedInstanceState.getString("SELECTED_DATE");
			WhutGlobal.SELECTED_TERM = savedInstanceState.getString("SELECTED_TERM");
			WhutGlobal.VIEW_STATE = savedInstanceState.getString("VIEW_STATE");
			WhutGlobal.URL_HEADER_STR = savedInstanceState.getString("URL_HEADER_STR");
			WhutGlobal.USER_ID = savedInstanceState.getString("USER_ID");
			WhutGlobal.USER_NAME = savedInstanceState.getString("USER_NAME");			
		}
		actionBar = (ActionBar)findViewById(R.id.gerenkebiao_actionbar);
		actionBar.setHomeAction(new IntentAction(this, WelcomeJiaoActivity.createIntent(this), R.drawable.ic_actionbar_whut));
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("学年："+WhutGlobal.SELECTED_DATE+"|学期："+WhutGlobal.SELECTED_TERM);
		actionBar.addAction(new PostAction());
		result = WhutGlobal.htmlData;
		mHead = (RelativeLayout) findViewById(R.id.geren_kebiao_head);
		mHead.setFocusable(false);
		mHead.setClickable(false);
		mHead.setBackgroundColor(Color.parseColor("#FF5735"));
		mHead.setOnTouchListener(new ListViewAndHeadViewTouchLinstener());
		myHandler = new UpdateUIHandler(Looper.myLooper());
		mListView1 = (ListView) findViewById(R.id.geren_kebiao_listview);
		mListView1.setOnTouchListener(new ListViewAndHeadViewTouchLinstener());

		myAdapter = new MyAdapter(this, R.layout.geren_kebiao_item);
		mListView1.setAdapter(myAdapter);
		httpOperation = new HttpOperation(this);
		dateList = WhutGlobal.DATE_LIST;
		selectedDate = WhutGlobal.SELECTED_DATE;
		selectedTerm = WhutGlobal.SELECTED_TERM;
		viewState = WhutGlobal.VIEW_STATE;
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		SaveTwoDimArray mySave = SaveTwoDimArray.getSingletonObject();
		mySave.setCustomArray(result);
		mySave.setPingJiaoUrls(WhutGlobal.PINGJIAO_URLS);
		outState.putSerializable(SaveTwoDimArray.NAME, mySave);
		outState.putStringArray("DATE_LIST", WhutGlobal.DATE_LIST);
		outState.putString("SELECTED_DATE", WhutGlobal.SELECTED_DATE);
		outState.putString("SELECTED_TERM", WhutGlobal.SELECTED_TERM);
		outState.putString("VIEW_STATE", WhutGlobal.VIEW_STATE);
		outState.putString("URL_HEADER_STR", WhutGlobal.URL_HEADER_STR);
		outState.putString("USER_ID", WhutGlobal.USER_ID);
		outState.putString("USER_NAME", WhutGlobal.USER_NAME);
		Log.i(TAG, "调用保存临时变量");
	}
	
	private void initial(){
		actionBar.setTitle("学年："+WhutGlobal.SELECTED_DATE+"|学期："+WhutGlobal.SELECTED_TERM);
		result = WhutGlobal.htmlData;
		dateList = WhutGlobal.DATE_LIST;
		selectedDate = WhutGlobal.SELECTED_DATE;
		selectedTerm = WhutGlobal.SELECTED_TERM;
		viewState = WhutGlobal.VIEW_STATE;
		myAdapter.notifyDataSetChanged();
	}
	
	class PostAction extends AbstractAction{

		public PostAction() {
			super(R.drawable.ic_actionbar_expand);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void performAction(View view) {
			// TODO Auto-generated method stub
			final Dialog dialog = new Dialog(GeRenKeBiaoActivity.this);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(R.layout.gerenkebiao_dialog);
			Spinner spinner = (Spinner) dialog.findViewById(R.id.geren_dialog_spinner);
			creatSpinnerAdapter();
			spinner.setAdapter(dialogSpinnerAdapter);
			spinner.setOnItemSelectedListener(new MyDialogSpinnerListener());
			spinner.setSelection(getPositionOfSelectedItem());
			Button submitButton = (Button) dialog.findViewById(R.id.geren_dialog_submitbutton);
			submitButton.setOnClickListener(new MyDialogSubmitListener(dialog));
			Button cancelButton = (Button) dialog.findViewById(R.id.geren_dialog_cancelbutton);
			cancelButton.setOnClickListener(new MyDialogCancelListener(dialog));
			RadioGroup radioGroup = (RadioGroup) dialog.findViewById(R.id.geren_dialog_radiogroup);
			RadioButton radioButton1 = (RadioButton) dialog.findViewById(R.id.geren_dialog_radio1);
			RadioButton radioButton2 = (RadioButton) dialog.findViewById(R.id.geren_dialog_radio2);
			if(selectedTerm.equals("1")){
				radioButton1.setChecked(true);
			}else{
				radioButton2.setChecked(true);
			}
			radioGroup.setOnCheckedChangeListener(new MyDialogRadioListener(dialog));
			dialog.show();
		}
		
	}
	
	public void creatSpinnerAdapter(){
		ArrayList<String> xueNianLists = new ArrayList<String>();
		for(String s:dateList){
			xueNianLists.add(s);
		}
		
		dialogSpinnerAdapter = new ArrayAdapter<String>(GeRenKeBiaoActivity.this, 
				android.R.layout.simple_spinner_item, xueNianLists);
		dialogSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	}
	
	class MyDialogSpinnerListener implements OnItemSelectedListener{
		@Override
		public void onItemSelected(AdapterView<?> parent, View arg1, int position,
				long id) {
			// TODO Auto-generated method stub
			Log.i(TAG, parent.getItemAtPosition(position).toString());
			WhutGlobal.SELECTED_DATE = parent.getItemAtPosition(position).toString();
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	class MyDialogSubmitListener implements OnClickListener{
		Dialog dialog;
		
		public MyDialogSubmitListener(Dialog dialog) {
			// TODO Auto-generated constructor stub
			this.dialog = dialog;
		}
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			WhutGlobal.WhichAction = 8;
			cancelDialogByHand = false;
			progressDialog = new ProgressDialog(GeRenKeBiaoActivity.this);
			progressDialog.setOnCancelListener(new DialogCancelListener());
			httpOperateThread = new HttpOperateThread(GeRenKeBiaoActivity.this, myHandler, httpOperation);
			httpOperateThread.setPostParas(getNameValuePairs());
			httpOperateThread.start();
			if(dialog.isShowing()){
				dialog.dismiss();
			}
		}
		
	}
	
	class MyDialogCancelListener implements OnClickListener{
		Dialog dialog;
		
		public MyDialogCancelListener(Dialog dialog) {
			// TODO Auto-generated constructor stub
			this.dialog = dialog;
		}
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(dialog.isShowing()){
				Log.i(TAG, "cancel");
				dialog.dismiss();
			}
		}
		
	}
	
	class MyDialogRadioListener implements OnCheckedChangeListener{
		Dialog dialog;
		public MyDialogRadioListener(Dialog dialog) {
			// TODO Auto-generated constructor stub
			this.dialog = dialog;
		}
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			// TODO Auto-generated method stub
			int id = group.getCheckedRadioButtonId();
			RadioButton rd = (RadioButton) dialog.findViewById(id);
			WhutGlobal.SELECTED_TERM = rd.getText().toString();
			Log.i(TAG, "button=="+rd.getText());
		}
	}
	
	private int getPositionOfSelectedItem(){
		int k=0;
		for(int i=1; i<dateList.length; i++){
			if(dateList[i].equals(selectedDate)){
				k=i;
			}
		}
		return k;
	}
	
	
	private List<NameValuePair> getNameValuePairs(){
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("__VIEWSTATE",viewState));
        nameValuePairs.add(new BasicNameValuePair("__EVENTTARGET", ""));
        nameValuePairs.add(new BasicNameValuePair("__EVENTARGUMENT", ""));
        nameValuePairs.add(new BasicNameValuePair("ddlXN", WhutGlobal.SELECTED_DATE));//不能换成selectedDate，当手动取消时会使spinner显示不对
        nameValuePairs.add(new BasicNameValuePair("ddlXQ", WhutGlobal.SELECTED_TERM));
        return nameValuePairs;
	}

	class ListViewAndHeadViewTouchLinstener implements View.OnTouchListener {

		@Override
		public boolean onTouch(View arg0, MotionEvent arg1) {
			//当在列头 和 listView控件上touch时，将这个touch的事件分发给 ScrollView
			HorizontalScrollView headSrcrollView = (HorizontalScrollView) mHead
					.findViewById(R.id.geren_horizontalScrollView1);
			headSrcrollView.onTouchEvent(arg1);
			return false;
		}
	}

	public class MyAdapter extends BaseAdapter {
		public List<ViewHolder> mHolderList = new ArrayList<ViewHolder>();

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
				synchronized (GeRenKeBiaoActivity.this) {
					convertView = mInflater.inflate(id_row_layout, null);
					holder = new ViewHolder();

					MyHScrollView scrollView1 = (MyHScrollView) convertView
							.findViewById(R.id.geren_horizontalScrollView1);

					holder.scrollView = scrollView1;
					holder.txt1 = (TextView) convertView
							.findViewById(R.id.geren_kebiao_textView1);
					holder.txt2 = (TextView) convertView
							.findViewById(R.id.geren_kebiao_textView2);
					holder.txt3 = (TextView) convertView
							.findViewById(R.id.geren_kebiao_textView3);
					holder.txt4 = (TextView) convertView
							.findViewById(R.id.geren_kebiao_textView4);
					holder.txt5 = (TextView) convertView
							.findViewById(R.id.geren_kebiao_textView5);
					holder.txt6 = (TextView) convertView
							.findViewById(R.id.geren_kebiao_textView6);
					holder.txt7 = (TextView) convertView
							.findViewById(R.id.geren_kebiao_textView7);
					holder.txt8 = (TextView) convertView
							.findViewById(R.id.geren_kebiao_textView8);

					MyHScrollView headSrcrollView = (MyHScrollView) mHead
							.findViewById(R.id.geren_horizontalScrollView1);
					headSrcrollView
							.AddOnScrollChangedListener(new OnScrollChangedListenerImp(
									scrollView1));

					convertView.setTag(holder);
					mHolderList.add(holder);
				}
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.txt1.setText(result[position+1][0]);
			holder.txt2.setText(result[position+1][1]);
			holder.txt3.setText(result[position+1][2]);
			holder.txt4.setText(result[position+1][3]);
			holder.txt5.setText(result[position+1][4]);
			holder.txt6.setText(result[position+1][5]);
			holder.txt7.setText(result[position+1][6]);
			holder.txt8.setText(result[position+1][7]);

			return convertView;
		}

		class OnScrollChangedListenerImp implements OnScrollChangedListener {
			MyHScrollView mScrollViewArg;

			public OnScrollChangedListenerImp(MyHScrollView scrollViewar) {
				mScrollViewArg = scrollViewar;
			}

			@Override
			public void onScrollChanged(int l, int t, int oldl, int oldt) {
				mScrollViewArg.smoothScrollTo(l, t);
			}
		};

		class ViewHolder {
			TextView txt1;
			TextView txt2;
			TextView txt3;
			TextView txt4;
			TextView txt5;
			TextView txt6;
			TextView txt7;
			TextView txt8;
			HorizontalScrollView scrollView;
		}
	}// end class my
	
	public class PingJiaoOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent i = new Intent();
//			i.setClass(GeRenKeBiaoActivity.this, PrePingJiaoActivity.class);
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
	        		progressDialog.show();
	        		progressDialog.setMessage("正在请求数据...");
	        		break;
	        	case 2:
	        		progressDialog.setMessage("正在咀嚼数据...");
	        		break;
				case 3:
					progressDialog.dismiss();
					if(WhutGlobal.JUMP_OR_NOT&&(!cancelDialogByHand)){
						Log.i(TAG, "更新了吗");
						initial();
					}
					break;
				case 100:
					if(!cancelDialogByHand){
						Toast.makeText(GeRenKeBiaoActivity.this, "不好意思，数据有问题...", Toast.LENGTH_LONG).show();
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
			httpOperation.closeHttpPost();
		}
	}
}
