package com.withparadox2.whut.ui;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.AbstractAction;
import com.markupartist.android.widget.ActionBar.IntentAction;
import com.withparadox2.whut.R;
import com.withparadox2.whut.dao.SaveTwoDimArray;
import com.withparadox2.whut.dao.WhutGlobal;
import com.withparadox2.whut.view.MyHScrollView;
import com.withparadox2.whut.view.MyHScrollView.OnScrollChangedListener;

public class JieYueChaXunActivity extends Activity {
	/** Called when the activity is first created. */
	ListView mListView1;
	MyAdapter myAdapter;
	RelativeLayout mHead;
	String[][] result;
	private ActionBar actionBar;
	private int resultActualLength;// remove the first row of result
	private boolean[] guoQiFlag; // 过期为true，没过期为false，然后在表中用颜色标记出来

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jieyue_chaxun);
		if (savedInstanceState != null) {
			SaveTwoDimArray save = (SaveTwoDimArray) savedInstanceState
			        .getSerializable(SaveTwoDimArray.NAME);
			WhutGlobal.htmlData = save.getCustomArray();
			WhutGlobal.USER_NAME = savedInstanceState.getString("USER_NAME");
			WhutGlobal.JSESSIONID = savedInstanceState.getString("JSESSIONID");
		}
		actionBar = (ActionBar) findViewById(R.id.jieyuechaxun_actionbar);
		actionBar.setHomeAction(new IntentAction(this, WelcomeTuActivity
		        .createIntent(this), R.drawable.ic_actionbar_whut));
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.addAction(new DownLoadKeBiaoAction());
		result = WhutGlobal.htmlData;
		resultActualLength = result.length - 1;
		actionBar.setTitle("共借：" + resultActualLength + "本|过期：" + getGuoQiNum()
		        + "本");
		mHead = (RelativeLayout) findViewById(R.id.jieyue_chaxun_head);
		mHead.setFocusable(false);
		mHead.setClickable(false);
		mHead.setBackgroundColor(Color.parseColor("#FF5735"));
		mHead.setOnTouchListener(new ListViewAndHeadViewTouchLinstener());

		mListView1 = (ListView) findViewById(R.id.jieyue_chaxun_listview);
		mListView1.setOnTouchListener(new ListViewAndHeadViewTouchLinstener());

		myAdapter = new MyAdapter(this, R.layout.jieyue_chaxun_item);
		mListView1.setAdapter(myAdapter);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		SaveTwoDimArray mySave = SaveTwoDimArray.getSingletonObject();
		mySave.setCustomArray(result);
		outState.putSerializable(SaveTwoDimArray.NAME, mySave);
		outState.putString("USER_NAME", WhutGlobal.USER_NAME);
		outState.putString("JSESSIONID", WhutGlobal.JSESSIONID);
	}

	public class DownLoadKeBiaoAction extends AbstractAction {

		public DownLoadKeBiaoAction() {
			super(R.drawable.ic_actionbar_download_kebiao);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void performAction(View view) {
			// TODO Auto-generated method stub
			SharedPreferences share = getSharedPreferences("liXianJieYue",
			        Activity.MODE_PRIVATE);
			SharedPreferences.Editor edit = share.edit();
			for (int i = 0; i < resultActualLength; i++) {
				edit.putString("title" + i, result[i + 1][1]);
				edit.putString("start_time" + i, result[i + 1][6]);
				edit.putString("end_time" + i, result[i + 1][7]);
			}
			edit.putString("length", "" + resultActualLength);
			edit.commit();
			Toast.makeText(JieYueChaXunActivity.this, "保存成功...",
			        Toast.LENGTH_LONG).show();
		}

	}

	class ListViewAndHeadViewTouchLinstener implements View.OnTouchListener {

		@Override
		public boolean onTouch(View arg0, MotionEvent arg1) {
			// 当在列头 和 listView控件上touch时，将这个touch的事件分发给 ScrollView
			HorizontalScrollView headSrcrollView = (HorizontalScrollView) mHead
			        .findViewById(R.id.jieyue_horizontalScrollView1);
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
				synchronized (JieYueChaXunActivity.this) {
					convertView = mInflater.inflate(id_row_layout, null);
					holder = new ViewHolder();

					MyHScrollView scrollView1 = (MyHScrollView) convertView
					        .findViewById(R.id.jieyue_horizontalScrollView1);

					holder.scrollView = scrollView1;
					holder.txt1 = (TextView) convertView
					        .findViewById(R.id.jieyue_chaxun_textView1);
					holder.txt2 = (TextView) convertView
					        .findViewById(R.id.jieyue_chaxun_textView2);
					holder.txt3 = (TextView) convertView
					        .findViewById(R.id.jieyue_chaxun_textView3);
					holder.txt4 = (TextView) convertView
					        .findViewById(R.id.jieyue_chaxun_textView4);
					holder.txt5 = (TextView) convertView
					        .findViewById(R.id.jieyue_chaxun_textView5);
					holder.txt6 = (TextView) convertView
					        .findViewById(R.id.jieyue_chaxun_textView6);
					holder.txt7 = (TextView) convertView
					        .findViewById(R.id.jieyue_chaxun_textView7);
					holder.txt8 = (TextView) convertView
					        .findViewById(R.id.jieyue_chaxun_textView8);

					MyHScrollView headSrcrollView = (MyHScrollView) mHead
					        .findViewById(R.id.jieyue_horizontalScrollView1);
					headSrcrollView
					        .AddOnScrollChangedListener(new OnScrollChangedListenerImp(
					                scrollView1));

					convertView.setTag(holder);
					mHolderList.add(holder);
				}
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			if (guoQiFlag[position]) {
				holder.txt1.setBackgroundResource(R.color.holo_blue_darker);
			} else {
				holder.txt1.setBackgroundColor(Color.parseColor("#FF5735"));
			}
			holder.txt1.setText(result[position + 1][1]);
			holder.txt2.setText(result[position + 1][6]);
			holder.txt3.setText(result[position + 1][7]);
			holder.txt4.setText(result[position + 1][2]);
			holder.txt5.setText(result[position + 1][3]);
			holder.txt6.setText(result[position + 1][4]);
			holder.txt7.setText(result[position + 1][5]);
			holder.txt8.setText(result[position + 1][0]);

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

	private int getGuoQiNum() {
		guoQiFlag = new boolean[resultActualLength];
		String s = todayDate();
		int num = 0;
		for (int i = 0; i < guoQiFlag.length; i++) {
			if (guoQiDetect(s, result[i + 1][7])) {
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