package com.withparadox2.whut.ui;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.IntentAction;
import com.withparadox2.whut.R;
import com.withparadox2.whut.R.color;
import com.withparadox2.whut.R.drawable;
import com.withparadox2.whut.R.id;
import com.withparadox2.whut.R.layout;
import com.withparadox2.whut.dao.SaveTwoDimArray;
import com.withparadox2.whut.dao.WhutGlobal;

public class LiXianJieYueActivity extends Activity {

	private ActionBar actionBar;
	private ListView myListView;
	LinearLayout mHead;
	private MyAdapter myAdapter;
	private String[][] result;
	private final String TAG = "LiXianJieYueActivity";
	private boolean[] guoQiFlag; // 过期为true，没过期为false，然后在表中用颜色标记出来

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lixian_jieyue);
		if (savedInstanceState != null) {
			SaveTwoDimArray save = (SaveTwoDimArray) savedInstanceState
			        .getSerializable(SaveTwoDimArray.NAME);
			WhutGlobal.htmlData = save.getCustomArray();
		}
		actionBar = (ActionBar) findViewById(R.id.xujie_actionbar);
		actionBar.setHomeAction(new IntentAction(this, LoginActivity
		        .createIntent(this), R.drawable.ic_actionbar_whut));
		actionBar.setDisplayHomeAsUpEnabled(true);

		result = WhutGlobal.htmlData;
		actionBar.setTitle("共借：" + result.length + "本|过期：" + getGuoQiNum()
		        + "本");
		mHead = (LinearLayout) findViewById(R.id.xujie_head);
		mHead.setFocusable(false);
		mHead.setClickable(false);
		mHead.setBackgroundColor(Color.parseColor("#FF5735"));

		myListView = (ListView) findViewById(R.id.xujie_listview);
		myAdapter = new MyAdapter(this, R.layout.lixian_jieyue_item);
		myListView.setAdapter(myAdapter);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		SaveTwoDimArray mySave = SaveTwoDimArray.getSingletonObject();
		mySave.setCustomArray(result);
		outState.putSerializable(SaveTwoDimArray.NAME, mySave);
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
			return result.length;
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
				synchronized (LiXianJieYueActivity.this) {
					convertView = mInflater.inflate(id_row_layout, parentView,
					        false);
					holder = new ViewHolder();

					holder.txt1 = (TextView) convertView
					        .findViewById(R.id.xujie_textView1);
					holder.txt2 = (TextView) convertView
					        .findViewById(R.id.xujie_textView2);
					holder.txt3 = (TextView) convertView
					        .findViewById(R.id.xujie_textView3);
					convertView.setTag(holder);
				}
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.txt1.setText(result[position][0]);
			if (guoQiFlag[position]) {
				holder.txt1.setBackgroundResource(R.color.holo_blue_darker);
			} else {
				holder.txt1.setBackgroundColor(Color.parseColor("#FF5735"));
			}

			holder.txt2.setText(result[position][1]);
			holder.txt3.setText(result[position][2]);
			return convertView;
		}

		class ViewHolder {
			TextView txt1;
			TextView txt2;
			TextView txt3;
		}

	}

	private int getGuoQiNum() {
		guoQiFlag = new boolean[result.length];
		String s = todayDate();
		Log.i(TAG, "时间" + s);
		int num = 0;
		for (int i = 0; i < guoQiFlag.length; i++) {
			if (guoQiDetect(s, result[i][2])) {
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
}
