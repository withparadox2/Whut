package com.withparadox2.whut.jiaoshi;


import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.IntentAction;
import com.withparadox2.whut.LiXianHomeActivity;
import com.withparadox2.whut.R;
import com.withparadox2.whut.dao.WhutGlobal;

public class KongJiaoShiActivity extends Activity{
	private ExpandableListView expandableListView;
	private MyExpandableListAdapter myExpandableAdapter;
	private ActionBar actionbar;
	
	public static final int MORNING = 0;
	public static final int AFTERNOON = 1;
	
	public static final int START_DOWNLOADING = 0;
	public static final int START_PARSING = 1;
	public static final int GET_DATA_SUCCESS = 2;
	
	private HttpRoomSearchThread myThread;
	
	private UpdaetUIHandler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jiaoshi_hold);
		actionbar = (ActionBar)findViewById(R.id.search_book_actionbar);
		actionbar.setHomeAction(new IntentAction(this, LiXianHomeActivity.createIntent(this), R.drawable.ic_actionbar_whut));
		actionbar.setDisplayHomeAsUpEnabled(true);
	    actionbar.setTitle("查空教室");
	    handler = new UpdaetUIHandler(Looper.myLooper());
	    myThread = new HttpRoomSearchThread(handler);
	    
	    expandableListView = (ExpandableListView)findViewById(R.id.expandable_listview);
	    expandableListView.setGroupIndicator(null);
		myExpandableAdapter = new MyExpandableListAdapter();
		expandableListView.setAdapter(myExpandableAdapter);
		expandableListView.setOnGroupClickListener(new MyOnGroupClickListener());
	    myThread.run();
	}
	
	class MyOnGroupClickListener implements OnGroupClickListener{

		@Override
		public boolean onGroupClick(ExpandableListView parent, View v,
				int groupPosition, long id) {
			// TODO Auto-generated method stub
			
			return false;
			
		}
	}
	class MyExpandableListAdapter extends BaseExpandableListAdapter{

		@Override
		public Object getChild(int arg0, int arg1) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getChildId(int arg0, int arg1) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
                boolean isLastChild, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ChildViewHolder viewHolder = null;
			if(convertView == null){
				convertView = LayoutInflater.from(KongJiaoShiActivity.this).inflate(R.layout.jiaoshi_expandable_child, null);
	            viewHolder = new ChildViewHolder();
	            viewHolder.room_location1 = (TextView)convertView.findViewById(R.id.room_location1);
				viewHolder.room_location2 = (TextView)convertView.findViewById(R.id.room_location2);
				viewHolder.room_location3 = (TextView)convertView.findViewById(R.id.room_location3);
				convertView.setTag(viewHolder);
			}else{
				viewHolder = (ChildViewHolder) convertView.getTag();
			}
			
			String[] s = WhutGlobal.ROOM_CHILD_LIST.get(groupPosition).get(childPosition);
			viewHolder.room_location1.setText(s[0]);
			viewHolder.room_location2.setText(s[1]);
			viewHolder.room_location3.setText(s[2]);
			
			return convertView;
		}
		
		

		@Override
		public int getChildrenCount(int arg0) {
			// TODO Auto-generated method stub
			return WhutGlobal.ROOM_CHILD_LIST.get(arg0).size();
		}

		@Override
		public Object getGroup(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int getGroupCount() {
			// TODO Auto-generated method stub
			return 4;
		}

		@Override
		public long getGroupId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
                View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			GroupViewHolder viewHolder = null;
			if(convertView == null){
				convertView = LayoutInflater.from(KongJiaoShiActivity.this).inflate(R.layout.jiaoshi_expandable_group, null);
	            viewHolder = new GroupViewHolder();
	            viewHolder.area_location = (TextView)convertView.findViewById(R.id.area_location);
				convertView.setTag(viewHolder);
			}else{
				viewHolder = (GroupViewHolder) convertView.getTag();
			}
			viewHolder.area_location.setText(WhutGlobal.ROOM_GROUP_LIST.get(groupPosition));
			return convertView;
		}

		@Override
		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isChildSelectable(int arg0, int arg1) {
			// TODO Auto-generated method stub
			return true;
		}
		
		public class GroupViewHolder{
			TextView area_location;
		}
		
		public class ChildViewHolder{
			TextView room_location1;
			TextView room_location2;
			TextView room_location3;
		}
		
	}
	
	class UpdaetUIHandler extends Handler{
		public UpdaetUIHandler(Looper myLooper){
			super(myLooper);
		}

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch(msg.arg1){
			case START_DOWNLOADING:
				Toast.makeText(KongJiaoShiActivity.this, "开始下载", Toast.LENGTH_SHORT).show();
				break;
			case START_PARSING:
				Toast.makeText(KongJiaoShiActivity.this, "开始解析", Toast.LENGTH_SHORT).show();
				break;
			case GET_DATA_SUCCESS:	
				updateData();
				break;
			}
		}
	}
	
	private void updateData(){
		myExpandableAdapter.notifyDataSetChanged();
	}
}
