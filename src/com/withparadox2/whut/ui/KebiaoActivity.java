package com.withparadox2.whut.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
import com.withparadox2.whut.R.drawable;
import com.withparadox2.whut.R.id;
import com.withparadox2.whut.R.layout;
import com.withparadox2.whut.dao.SaveTwoDimArray;
import com.withparadox2.whut.dao.WhutGlobal;
import com.withparadox2.whut.http.FetchKebiaoTask;
import com.withparadox2.whut.view.MyHScrollView;
import com.withparadox2.whut.view.MyHScrollView.OnScrollChangedListener;

public class KebiaoActivity extends Activity implements FetchKebiaoTask.Callback{
	/** Called when the activity is first created. */
	ListView mListView1;
	MyAdapter myAdapter;
	RelativeLayout mHead;
	String[][] result = new String[4][5];
	private ActionBar actionBar;
	private String TAG = "ScrollTableActivity";
    
	private FetchKebiaoTask fetchKebiaoTask;
	
	private int dataLength = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.kebiao);
		actionBar = (ActionBar) findViewById(R.id.kebiao_actionbar);
		actionBar.setHomeAction(new IntentAction(this, WelcomeJiaoActivity
		        .createIntent(this), R.drawable.ic_actionbar_whut));
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.addAction(new DownLoadKeBiaoAction());
		result = WhutGlobal.htmlData;
		mHead = (RelativeLayout) findViewById(R.id.head);
		mHead.setFocusable(false);
		mHead.setClickable(false);
		mHead.setBackgroundColor(Color.parseColor("#FF5735"));
		mHead.setOnTouchListener(new ListViewAndHeadViewTouchLinstener());

		mListView1 = (ListView) findViewById(R.id.kebiao_listview);
		mListView1.setOnTouchListener(new ListViewAndHeadViewTouchLinstener());
		myAdapter = new MyAdapter(this, R.layout.kebiao_item);
		mListView1.setAdapter(myAdapter);
        
		getKeBiaoData();
	}

	public class DownLoadKeBiaoAction extends AbstractAction {

		public DownLoadKeBiaoAction() {
			super(R.drawable.ic_actionbar_download_kebiao);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void performAction(View view) {
			// TODO Auto-generated method stub
			SharedPreferences share = getSharedPreferences(
			        WhutGlobal.ShARE_LIXIAN_KEBIAO_NAME, Activity.MODE_PRIVATE);
			SharedPreferences.Editor edit = share.edit();
			for (int i = 0; i < 4; i++) {
				for (int j = 0; j < 5; j++) {
					edit.putString("" + (j + 5 * i), result[i][j]);
				}
			}
			edit.commit();
			Toast.makeText(KebiaoActivity.this, "保存成功...", Toast.LENGTH_LONG)
			        .show();
		}

	}

	class ListViewOnClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
		        long arg3) {
			// TODO Auto-generated method stub
			if (position % 2 == 0) {
				arg1.setBackgroundColor(Color.BLUE);
			}
		}
	}

	class ListViewAndHeadViewTouchLinstener implements View.OnTouchListener {

		@Override
		public boolean onTouch(View arg0, MotionEvent arg1) {
			// 当在列头 和 listView控件上touch时，将这个touch的事件分发给 ScrollView
			HorizontalScrollView headSrcrollView = (HorizontalScrollView) mHead
			        .findViewById(R.id.horizontalScrollView1);
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
			return dataLength;
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
				synchronized (KebiaoActivity.this) {
					convertView = mInflater.inflate(id_row_layout, null);
					holder = new ViewHolder();

					MyHScrollView scrollView1 = (MyHScrollView) convertView
					        .findViewById(R.id.horizontalScrollView1);

					holder.scrollView = scrollView1;
					holder.txt1 = (TextView) convertView
					        .findViewById(R.id.textView1);
					holder.txt2 = (TextView) convertView
					        .findViewById(R.id.textView2);
					holder.txt3 = (TextView) convertView
					        .findViewById(R.id.textView3);
					holder.txt4 = (TextView) convertView
					        .findViewById(R.id.textView4);
					holder.txt5 = (TextView) convertView
					        .findViewById(R.id.textView5);
					holder.txt6 = (TextView) convertView
					        .findViewById(R.id.textView6);

					MyHScrollView headSrcrollView = (MyHScrollView) mHead
					        .findViewById(R.id.horizontalScrollView1);
					headSrcrollView
					        .AddOnScrollChangedListener(new OnScrollChangedListenerImp(
					                scrollView1));

					convertView.setTag(holder);
					mHolderList.add(holder);
				}
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.txt1.setText("第\n\n" + (position + 1) + "\n\n节");
			holder.txt2.setText(result[position][0]);
			holder.txt3.setText(result[position][1]);
			holder.txt4.setText(result[position][2]);
			holder.txt5.setText(result[position][3]);
			holder.txt6.setText(result[position][4]);
			holder.txt2.setOnClickListener(new TextViewOnClickListener());
			holder.txt3.setOnClickListener(new TextViewOnClickListener());
			holder.txt4.setOnClickListener(new TextViewOnClickListener());
			holder.txt5.setOnClickListener(new TextViewOnClickListener());
			holder.txt6.setOnClickListener(new TextViewOnClickListener());

			return convertView;
		}

		class TextViewOnClickListener implements OnClickListener {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				v.setBackgroundColor(Color.CYAN);
			}

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
			HorizontalScrollView scrollView;
		}
	}// end class my

	private void getKeBiaoData() {
		fetchKebiaoTask = new FetchKebiaoTask(KebiaoActivity.this);
        actionBar.setTitle("正在获取数据...");
		fetchKebiaoTask.execute();
	}

	@Override
    public void onPostExecute(String[][] result) {
	    // TODO Auto-generated method stub
        this.result = result;
        dataLength = 4;
        actionBar.setTitle("课表");
        myAdapter.notifyDataSetChanged();
    }
}