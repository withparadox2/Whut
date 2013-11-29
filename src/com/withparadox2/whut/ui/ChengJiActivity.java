package com.withparadox2.whut.ui;

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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.IntentAction;
import com.withparadox2.whut.R;
import com.withparadox2.whut.dao.SaveTwoDimArray;
import com.withparadox2.whut.dao.WhutGlobal;
import com.withparadox2.whut.http.FetchChengjiTask;
import com.withparadox2.whut.util.Helper;

public class ChengJiActivity extends Activity implements FetchChengjiTask.Callback{
         /** Called when the activity is first created. */
                ListView mListView1;
                MyAdapter myAdapter;
                LinearLayout mHead;
                String[][] result;
                ActionBar actionBar;
                private int dataLength;

                @Override
                public void onCreate(Bundle savedInstanceState) {
                        super.onCreate(savedInstanceState);
                        setContentView(R.layout.chengji);
                      
                        result = WhutGlobal.htmlData;
                        mHead = (LinearLayout) findViewById(R.id.chengji_head);
                        mHead.setBackgroundColor(getResources().getColor(R.color.typical_red));
                        actionBar = (ActionBar)findViewById(R.id.chengji_actionbar);
                        actionBar.setHomeAction(new IntentAction(this, WelcomeJiaoActivity.createIntent(this), R.drawable.ic_actionbar_whut));                        
                        mListView1 = (ListView) findViewById(R.id.chengji_listview);
                        actionBar.setDisplayHomeAsUpEnabled(true);
                        actionBar.setTitle("全部成绩");

                        myAdapter = new MyAdapter(this, R.layout.chengji_item);
                        mListView1.setAdapter(myAdapter);
                        getChengjiData();
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
                                        synchronized (ChengJiActivity.this) {
                                                convertView = mInflater.inflate(id_row_layout, null);
                                                holder = new ViewHolder();

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
                                                holder.txt6= (TextView) convertView
                                                        .findViewById(R.id.chengji_textView6);

                                                convertView.setTag(holder);
                                                mHolderList.add(holder);
                                        }
                                } else {
                                        holder = (ViewHolder) convertView.getTag();
                                }
                                holder.txt1.setText(result[position][0]);
                                holder.txt2.setText(result[position][1]);
                                holder.txt3.setText(result[position][2]);
                                holder.txt4.setText(result[position][3]);
                                holder.txt5.setText(result[position][4]);
                                holder.txt6.setText(result[position][5]);
                                return convertView;
                        }

                       
                        class ViewHolder {
                                TextView txt1;
                                TextView txt2;
                                TextView txt3;
                                TextView txt4;
                                TextView txt5;
                                TextView txt6;
                         
                        }
                }// end class my

                private void getChengjiData(){
            		FetchChengjiTask fetchChengjiTask = new FetchChengjiTask(ChengJiActivity.this, ChengJiActivity.this);
                    actionBar.setTitle("正在获取数据...");
                    fetchChengjiTask.execute();
                }
				@Override
                public void onPostExecute(String[][] result) {
	                // TODO Auto-generated method stub
                    if(result.length != 1){
				       this.result = result;
				        dataLength = result.length;
				        myAdapter.notifyDataSetChanged();
                    }else{
                    	Helper.showShortToast(ChengJiActivity.this, "出错了...");
                    }
			        actionBar.setTitle("成绩");
                }

}
