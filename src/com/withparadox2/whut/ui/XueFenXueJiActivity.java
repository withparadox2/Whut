package com.withparadox2.whut.ui;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.IntentAction;
import com.withparadox2.whut.R;
import com.withparadox2.whut.http.FetchXuefenXuejiTask;
import com.withparadox2.whut.util.Helper;

public class XueFenXueJiActivity extends Activity implements FetchXuefenXuejiTask.Callback {

	private TextView classRankText;
	private TextView scoreYearText;
	private TextView courseRankText;
	private TextView scoreTotalText;

	private ListView myListView;
	private MyAdapter myAdapter;
	private ActionBar actionBar;
	
	private FetchXuefenXuejiTask fetchXuefenXuejiTask;

	private ArrayList<String[]> list = new ArrayList<String[]>();
	private boolean isLoading = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xuefen_xueji);
		actionBar = (ActionBar) findViewById(R.id.actionbar);
		actionBar.setHomeAction(new IntentAction(this, WelcomeJiaoActivity
				.createIntent(this), R.drawable.ic_actionbar_whut));
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("ѧ�ֺͼ���");
		classRankText = (TextView)findViewById(R.id.class_rank_text);
		scoreYearText = (TextView)findViewById(R.id.score_year_text);
		courseRankText = (TextView)findViewById(R.id.course_rank_text);
		scoreTotalText = (TextView)findViewById(R.id.score_total_text);
		myListView = (ListView)findViewById(R.id.xuefen_listview);
		myAdapter = new MyAdapter(this);
		myListView.setAdapter(myAdapter);
		getXuefenData();
	}

	private void getXuefenData() {
		// TODO Auto-generated method stub
		fetchXuefenXuejiTask = new FetchXuefenXuejiTask(XueFenXueJiActivity.this);
		fetchXuefenXuejiTask.execute();
        isLoading = true;
		actionBar.setTitle("���ڼ���...");

	}

	class MyAdapter extends BaseAdapter{
		private LayoutInflater inflater;
		public MyAdapter(Context ctx){
			inflater = LayoutInflater.from(ctx);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
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
		public View getView(int position, View converView, ViewGroup arg2) {
			// TODO Auto-generated method stub
			ViewHolder viewHolder = null;
			if(converView == null){
				converView = inflater.inflate(R.layout.xuefen_xueji_item, null);
				viewHolder = new ViewHolder();
				viewHolder.huodeXuefen = (TextView) converView.findViewById(R.id.huode_xuefen_textview);
				viewHolder.kechengXingzhil =  (TextView) converView.findViewById(R.id.kecheng_xingzhi_textview);
				viewHolder.yaoqiuXuefen =  (TextView) converView.findViewById(R.id.yaoqiu_xuefen_textview);
				converView.setTag(viewHolder);
			}else{
				viewHolder = (ViewHolder) converView.getTag();
			}

			viewHolder.kechengXingzhil.setText(list.get(position)[0]);
			viewHolder.yaoqiuXuefen.setText(list.get(position)[1]);
			viewHolder.huodeXuefen.setText(list.get(position)[2]);
			return converView;
		}

		private class ViewHolder{
			TextView kechengXingzhil;
			TextView yaoqiuXuefen;
			TextView huodeXuefen;
		}

	}

	@Override
	public void onPostExecute(ArrayList<String[]> result) {
		// TODO Auto-generated method stub
        if(result != null){
    		classRankText.setText("�༶������" + result.get(0)[0]);
    		scoreYearText.setText("ѧ��ƽ��ѧ�ּ���" + result.get(0)[1]);
    		courseRankText.setText("רҵ������" + result.get(1)[0]);
    		scoreTotalText.setText("��ƽ��ѧ�ּ���" + result.get(1)[1]);
    		result.remove(0);
    		result.remove(0);
    		list = result;
    		myAdapter.notifyDataSetChanged();
        }else{
        	Helper.showShortToast(XueFenXueJiActivity.this, "������...");
        }
        isLoading = false;
		actionBar.setTitle("ѧ�ֺͼ���");
	}

	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
	    // TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && cancelTask()) {  
        	 actionBar.setTitle("ѧ�ֺ�ѧ��");
     	     Helper.showShortToast(XueFenXueJiActivity.this, "ȡ������...");
             if(list.size() == 0){
            	 Intent i = new Intent();
            	 i.setClass(XueFenXueJiActivity.this, WelcomeJiaoActivity.class);
            	 startActivity(i);
            	 this.finish();
             }
     	     return true;
		} else{
			return super.onKeyDown(keyCode, event);
		}
    } 
	
    
	private boolean cancelTask(){
		if(isLoading){
            isLoading = false;
			return (fetchXuefenXuejiTask == null ? false : fetchXuefenXuejiTask.cancel(true));
		}else{
			return false;
		}
	}

}
