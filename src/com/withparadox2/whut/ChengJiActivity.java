package com.withparadox2.whut;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
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

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.IntentAction;
import com.withparadox2.whut.MyHScrollView.OnScrollChangedListener;
import com.withparadox2.whut.dao.SaveTwoDimArray;
import com.withparadox2.whut.dao.WhutGlobal;

public class ChengJiActivity extends Activity{
	 /** Called when the activity is first created. */
		ListView mListView1;
		MyAdapter myAdapter;
		RelativeLayout mHead;
		String[][] result;
		ActionBar actionBar;

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.chengji);
			if(savedInstanceState!=null){
				SaveTwoDimArray save = (SaveTwoDimArray) savedInstanceState.getSerializable(SaveTwoDimArray.NAME);
				WhutGlobal.htmlData = save.getCustomArray();
				WhutGlobal.PINGJIAO_URLS = save.getPingJiaoUrls();
				WhutGlobal.URL_HEADER_STR = savedInstanceState.getString("URL_HEADER_STR");
				WhutGlobal.USER_ID = savedInstanceState.getString("USER_ID");
				WhutGlobal.USER_NAME = savedInstanceState.getString("USER_NAME");	
			}
			result = WhutGlobal.htmlData;
			mHead = (RelativeLayout) findViewById(R.id.chengji_head);
			mHead.setFocusable(false);
			mHead.setClickable(false);
			mHead.setBackgroundColor(Color.parseColor("#FF5735"));
			mHead.setOnTouchListener(new ListViewAndHeadViewTouchLinstener());
			
			actionBar = (ActionBar)findViewById(R.id.chengji_actionbar);
			actionBar.setHomeAction(new IntentAction(this, WelcomeJiaoActivity.createIntent(this), R.drawable.ic_actionbar_whut));			
			mListView1 = (ListView) findViewById(R.id.chengji_listview);
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setTitle("全部成绩");
			mListView1.setOnTouchListener(new ListViewAndHeadViewTouchLinstener());

			myAdapter = new MyAdapter(this, R.layout.chengji_item);
			mListView1.setAdapter(myAdapter);
		}
		
		@Override
		protected void onSaveInstanceState(Bundle outState) {
			// TODO Auto-generated method stub
			super.onSaveInstanceState(outState);
			SaveTwoDimArray mySave = SaveTwoDimArray.getSingletonObject();
			mySave.setCustomArray(result);
			mySave.setPingJiaoUrls(WhutGlobal.PINGJIAO_URLS);
			outState.putSerializable(SaveTwoDimArray.NAME, mySave);
			outState.putString("URL_HEADER_STR", WhutGlobal.URL_HEADER_STR);
			outState.putString("USER_ID", WhutGlobal.USER_ID);
			outState.putString("USER_NAME", WhutGlobal.USER_NAME);
		}

		class ListViewAndHeadViewTouchLinstener implements View.OnTouchListener {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				//当在列头 和 listView控件上touch时，将这个touch的事件分发给 ScrollView
				HorizontalScrollView headSrcrollView = (HorizontalScrollView) mHead
						.findViewById(R.id.chengji_horizontalScrollView1);
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
					synchronized (ChengJiActivity.this) {
						convertView = mInflater.inflate(id_row_layout, null);
						holder = new ViewHolder();

						MyHScrollView scrollView1 = (MyHScrollView) convertView
								.findViewById(R.id.chengji_horizontalScrollView1);

						holder.scrollView = scrollView1;
						holder.txt1 = (TextView) convertView
								.findViewById(R.id.chengji_textView1);
						holder.txt2 = (TextView) convertView
								.findViewById(R.id.chengji_textView2);
						holder.txt3 = (TextView) convertView
								.findViewById(R.id.chengji_textView3);
						holder.txt4 = (TextView) convertView
								.findViewById(R.id.chengji_textView4);
						holder.txt5 = (TextView) convertView
								.findViewById(R.id.chengji_textView5);
						holder.txt6 = (TextView) convertView
								.findViewById(R.id.chengji_textView6);
						holder.txt7 = (TextView) convertView
								.findViewById(R.id.chengji_textView7);
						holder.txt8 = (TextView) convertView
								.findViewById(R.id.chengji_textView8);
						holder.txt9 = (TextView) convertView
								.findViewById(R.id.chengji_textView9);
						holder.txt10 = (TextView) convertView
								.findViewById(R.id.chengji_textView10);
						holder.txt11 = (TextView) convertView
								.findViewById(R.id.chengji_textView11);
						holder.txt12 = (TextView) convertView
								.findViewById(R.id.chengji_textView12);
						holder.txt13 = (TextView) convertView
								.findViewById(R.id.chengji_textView13);

						MyHScrollView headSrcrollView = (MyHScrollView) mHead
								.findViewById(R.id.chengji_horizontalScrollView1);
						headSrcrollView
								.AddOnScrollChangedListener(new OnScrollChangedListenerImp(
										scrollView1));

						convertView.setTag(holder);
						mHolderList.add(holder);
					}
				} else {
					holder = (ViewHolder) convertView.getTag();
				}
				holder.txt1.setText(result[position+1][3]);
				holder.txt2.setText(result[position+1][0]);
				holder.txt3.setText(result[position+1][1]);
				holder.txt4.setText(result[position+1][4]);
				holder.txt5.setText(result[position+1][6]);
				holder.txt6.setText(result[position+1][7]);
				holder.txt7.setText(result[position+1][8]);
				holder.txt8.setText(result[position+1][9]);
				holder.txt9.setText(result[position+1][10]);
				holder.txt10.setText(result[position+1][11]);
				holder.txt11.setText(result[position+1][12]);
				holder.txt12.setText(result[position+1][13]);
				holder.txt13.setText(result[position+1][14]);
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
				TextView txt9;
				TextView txt10;
				TextView txt11;
				TextView txt12;
				TextView txt13;
				HorizontalScrollView scrollView;
			}
		}// end class my

}
