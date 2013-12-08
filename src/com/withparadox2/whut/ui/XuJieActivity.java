package com.withparadox2.whut.ui;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.AbstractAction;
import com.markupartist.android.widget.ActionBar.IntentAction;
import com.withparadox2.whut.R;
import com.withparadox2.whut.http.FetchRenewListTask;
import com.withparadox2.whut.http.XuJieTask;
import com.withparadox2.whut.util.Helper;

public class XuJieActivity extends Activity implements FetchRenewListTask.Callback{

	private ActionBar actionBar;
	private ListView myListView;
	LinearLayout mHead;
	private MyAdapter myAdapter;
	private String[][] result;
	private final String TAG = "XuJieActivity";
	private List<NameValuePair> nameValuePairs;
	private boolean[] guoQiFlag; // 过期为true，没过期为false，然后在表中用颜色标记出来
	private int resultActualLength = 0;
    private FetchRenewListTask fetchRenewListTask;
    private XuJieTask xuJieTask;
    
    private boolean isLoading = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xujie);

		actionBar = (ActionBar) findViewById(R.id.xujie_actionbar);
		actionBar.setHomeAction(new IntentAction(this, WelcomeTuActivity
		        .createIntent(this), R.drawable.ic_actionbar_whut));
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.addAction(new XuJieAllAction());


		mHead = (LinearLayout) findViewById(R.id.xujie_head);
		mHead.setBackgroundColor(getResources().getColor(R.color.typical_red));
        

		myListView = (ListView) findViewById(R.id.xujie_listview);
		myAdapter = new MyAdapter(this, R.layout.xujie_item);
		myListView.setAdapter(myAdapter);
        getRenewList();
	}

	
	private void getRenewList() {
	    // TODO Auto-generated method stub
	    fetchRenewListTask = new FetchRenewListTask(this);
	    fetchRenewListTask.execute();
	    actionBar.setTitle("正在获取数据...");
        isLoading = true;
    }


	public class XuJieAllAction extends AbstractAction {

		public XuJieAllAction() {
			super(R.drawable.ic_actionbar_byonehand);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void performAction(View view) {
			// TODO Auto-generated method stub
			boolean allXuJie = false;
			for (int i = 0; i < resultActualLength; i++) {
				if (result[i][8].equals("0")) {
					allXuJie = true;
				}
			}
			if (allXuJie) {
                isLoading = true;
                actionBar.setTitle("正在提交。。。");
				setAllNVPairs();
                xuJieTask = new XuJieTask(new XuJieTaskCallBack(), nameValuePairs);
                xuJieTask.execute();
			} else {
                Helper.showShortToast(XuJieActivity.this,  "没有可以续借的书...");
			}
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
			return resultActualLength;
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
				synchronized (XuJieActivity.this) {
					convertView = mInflater.inflate(id_row_layout, parentView,
					        false);
					holder = new ViewHolder();

					holder.txt1 = (TextView) convertView
					        .findViewById(R.id.xujie_textView1);
					holder.txt2 = (TextView) convertView
					        .findViewById(R.id.xujie_textView2);
					holder.txt3 = (TextView) convertView
					        .findViewById(R.id.xujie_textView3);
					holder.button = (Button) convertView
					        .findViewById(R.id.xujie_button);
					convertView.setTag(holder);
				}
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.txt1.setText(result[position][1]);
			holder.txt2.setText("借出:\n" + result[position][6] + "\n" + "应还:\n" + result[position][7]);
			holder.txt3.setText(result[position][8]);
			holder.button.setOnClickListener(new xuJieSingleClickListener(position));
            if(guoQiFlag[position]){
				holder.txt1.setBackgroundResource(R.color.holo_blue_darker);
            }
			return convertView;
		}

		class ViewHolder {
			TextView txt1;
			TextView txt2;
			TextView txt3;
			Button button;
		}

		class xuJieSingleClickListener implements OnClickListener {
			private int position;

			public xuJieSingleClickListener(int position) {
				// TODO Auto-generated constructor stub
				this.position = position;
			}

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (result[position][8].equals("1")) {
                    Helper.showShortToast(XuJieActivity.this, "已经续借过了...");
				} else {
					isLoading = true;
                    actionBar.setTitle("正在提交。。。");
					setNVPairs(position);
	                xuJieTask = new XuJieTask(new XuJieTaskCallBack(), nameValuePairs);
	                xuJieTask.execute();
				}

			}

		}

	}
    
	private void setNVPairs(int position) {
		nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("furl", "/opac/loan/renewList"));
		nameValuePairs.add(new BasicNameValuePair("barcodeList", result[position][0]));
	}

	private void setAllNVPairs() {
		nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("furl", "/opac/loan/renewList"));
		for (int i = 0; i < resultActualLength; i++) {
			nameValuePairs.add(new BasicNameValuePair("barcodeList", result[i][0]));
		}
	}

	private int getGuoQiNum() {
		guoQiFlag = new boolean[resultActualLength];
		String s = todayDate();
		Log.i(TAG, "时间" + s);
		int num = 0;
		for (int i = 0; i < guoQiFlag.length; i++) {
			if (guoQiDetect(s, result[i][7])) {
				guoQiFlag[i] = true;
				num++;
			} else {
				guoQiFlag[i] = false;
			}
		}
		return num;
	}


	private boolean guoQiDetect(String dateString1, String dateString2) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		boolean result = false;
		try {
			Date date1 = df.parse(dateString1);
			Date date2 = df.parse(dateString2);
			if (date1.getTime() > date2.getTime()) {
				result = true;
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	private String todayDate() {
		Calendar c = Calendar.getInstance();
		int mDay = c.get(Calendar.DAY_OF_MONTH);
		int mMonth = c.get(Calendar.MONTH);
		int mYear = c.get(Calendar.YEAR);
		return new StringBuilder().append(mYear).append("-")
		        .append(formatDate(mMonth + 1)).append("-")
		        .append(formatDate(mDay)).toString();
	}

	private String formatDate(int x) {
		String s = "" + x;
		if (s.length() == 1)
			s = "0" + s;
		return s;
	}

	@Override
    public void onPostExecute(String[][] result) {
	    // TODO Auto-generated method stub
        if(result != null){
        	this.result = result;
        	resultActualLength = result.length;
            getGuoQiNum();
    		actionBar.setTitle("共借：" + resultActualLength + "本|过期：" + getGuoQiNum() + "本");
            myAdapter.notifyDataSetChanged();
            saveDataToOffline();
        }else{
        	Helper.showShortToast(this, "出错了...");
            actionBar.setTitle("续借");
        }
        isLoading = false;
    }
    
	public class  XuJieTaskCallBack implements XuJieTask.Callback{

		@Override
        public void onPostExecute(boolean reuslt) {
	        // TODO Auto-generated method stub
            if(reuslt){
                Helper.showShortToast(XuJieActivity.this, "续借成功，正在更新数据...");
            	getRenewList();
            }else{
                Helper.showShortToast(XuJieActivity.this, "续借失败...");
                actionBar.setTitle("共借：" + resultActualLength + "本|过期：" + getGuoQiNum() + "本");
            }
            isLoading = false;
        }
	}
    
	private void saveDataToOffline(){
		SharedPreferences share = getSharedPreferences("liXianJieYue",
		        Activity.MODE_PRIVATE);
		SharedPreferences.Editor edit = share.edit();
		for (int i = 0; i < resultActualLength; i++) {
			edit.putString("title" + i, result[i][1]);
			edit.putString("start_time" + i, result[i][6]);
			edit.putString("end_time" + i, result[i][7]);
			edit.putString("location" + i, result[i][3]);
		}
		edit.putString("length", "" + resultActualLength);
		edit.commit();
	}
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
	    // TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && cancelTask()) {  
        	 actionBar.setTitle("续借");
     	     Helper.showShortToast(XuJieActivity.this, "取消操作...");
             if(resultActualLength == 0){
            	 startActivity(WelcomeTuActivity.createIntent(XuJieActivity.this));
             }
     	     return true;
		} else{
			return super.onKeyDown(keyCode, event);
		}
    } 
	
    
	private boolean cancelTask(){
		if(isLoading){
            isLoading = false;
			return (fetchRenewListTask == null ? false : fetchRenewListTask.cancel(true))||
				   (xuJieTask == null ? false :xuJieTask.cancel(true));
		}else{
			return false;
		}
	}
}
