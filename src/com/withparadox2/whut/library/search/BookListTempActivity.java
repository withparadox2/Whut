package com.withparadox2.whut.library.search;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.TextView;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.IntentAction;
import com.withparadox2.whut.LiXianHomeActivity;
import com.withparadox2.whut.R;
import com.withparadox2.whut.R.drawable;
import com.withparadox2.whut.R.id;
import com.withparadox2.whut.R.layout;
import com.withparadox2.whut.dao.BookListTempAdapter;
import com.withparadox2.whut.dao.WhutGlobal;



public class BookListTempActivity extends Activity{
	
	private ExpandableListView expandableListView;
	private MyExpandableListAdapter myExpandableAdapter;
	private ActionBar actionbar;
	
	private int lastChildExpandedPosition = -1;
	
	private BookListDatabase mDbHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.book_temp_hold);
		mDbHelper = new BookListDatabase(this);
		mDbHelper.fetchBookList();
		actionbar = (ActionBar)findViewById(R.id.search_book_actionbar);
		actionbar.setHomeAction(new IntentAction(this, LiXianHomeActivity.createIntent(this), R.drawable.ic_actionbar_whut));
		actionbar.setDisplayHomeAsUpEnabled(true);
	    actionbar.setTitle("要借图书");
	    expandableListView = (ExpandableListView)findViewById(R.id.expandable_listview);
	    expandableListView.setGroupIndicator(null);
		myExpandableAdapter = new MyExpandableListAdapter();
		expandableListView.setAdapter(myExpandableAdapter);
		expandableListView.setOnGroupClickListener(new MyOnGroupClickListener());
		expandableListView.setOnGroupCollapseListener(new MyOnGroupCollapseListener());
		expandableListView.setOnGroupExpandListener(new MyOnGroupExpandListener());
	}
	
	class MyOnGroupClickListener implements OnGroupClickListener{

		@Override
		public boolean onGroupClick(ExpandableListView parent, View v,
				int groupPosition, long id) {
			// TODO Auto-generated method stub
			
			return false;
			
		}
	}
	
	class MyOnGroupCollapseListener implements OnGroupCollapseListener{

		@Override
		public void onGroupCollapse(int groupPosition) {
			// TODO Auto-generated method stub
			if(lastChildExpandedPosition == groupPosition){
				lastChildExpandedPosition = -1;
			}
		}
		
	}
	
	class MyOnGroupExpandListener implements OnGroupExpandListener{

		@Override
		public void onGroupExpand(int groupPosition) {
			// TODO Auto-generated method stub
			
			if(lastChildExpandedPosition != -1&&groupPosition!=lastChildExpandedPosition){
				expandableListView.collapseGroup(lastChildExpandedPosition);
				System.out.println("Expand fired");
			}
			lastChildExpandedPosition = groupPosition;
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
				convertView = LayoutInflater.from(BookListTempActivity.this).inflate(R.layout.book_expandable_child, null);
	            viewHolder = new ChildViewHolder();
	            viewHolder.bookLocation = (TextView)convertView.findViewById(R.id.book_location);
				viewHolder.bookCode = (TextView)convertView.findViewById(R.id.book_code);
				convertView.setTag(viewHolder);
			}else{
				viewHolder = (ChildViewHolder) convertView.getTag();
			}
			viewHolder.bookLocation.setText(WhutGlobal.CHILDLIST.get(groupPosition).get(childPosition)[1]);
			viewHolder.bookCode.setText(WhutGlobal.CHILDLIST.get(groupPosition).get(childPosition)[0]);
			
			return convertView;
		}

		@Override
		public int getChildrenCount(int arg0) {
			// TODO Auto-generated method stub
			return WhutGlobal.CHILDLIST.get(arg0).size();
		}

		@Override
		public Object getGroup(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int getGroupCount() {
			// TODO Auto-generated method stub
			return WhutGlobal.BOOKLIST.size();
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
				convertView = LayoutInflater.from(BookListTempActivity.this).inflate(R.layout.book_expandable_group, null);
	            viewHolder = new GroupViewHolder();
	            viewHolder.bookTitle = (TextView)convertView.findViewById(R.id.book_title);
				viewHolder.author = (TextView)convertView.findViewById(R.id.author);
				viewHolder.publisher = (TextView)convertView.findViewById(R.id.publisher);
				viewHolder.bookCode = (TextView)convertView.findViewById(R.id.book_code);
				viewHolder.childFlag = (TextView)convertView.findViewById(R.id.get_child_flag);
				viewHolder.deleteButton = (Button)convertView.findViewById(R.id.add_book_button);
				convertView.setTag(viewHolder);
			}else{
				viewHolder = (GroupViewHolder) convertView.getTag();
			}
			viewHolder.author.setText("作    者:  "+WhutGlobal.BOOKLIST.get(groupPosition));
			viewHolder.deleteButton.setOnClickListener(new DeleteClickListener(groupPosition));
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
			TextView bookTitle;
			TextView author;
			TextView publisher;
			TextView bookCode;
			TextView childFlag;
			Button deleteButton;
		}
		
		public class ChildViewHolder{
			TextView bookCode;
			TextView bookLocation;
		}
		
		public class DeleteClickListener implements OnClickListener{

			int groupPosition;
			public DeleteClickListener(int groupPosition) {
				this.groupPosition = groupPosition;
			}
			
			private int getGroupId(){
				return Integer.parseInt(WhutGlobal.BOOKLIST.get(groupPosition));
			}
			
			private List<Integer> getChildIds(){
				List<String[]> list = WhutGlobal.CHILDLIST.get(groupPosition);
				List<Integer> childIdList = new ArrayList<Integer>();
				for(String[] s:list){
					childIdList.add(Integer.parseInt(s[2]));
				}
				return childIdList;
			}
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				System.out.println("click"+groupPosition);
				mDbHelper.deleteGroupAndChildByIds(getGroupId(), getChildIds());
				updateList();
			}
			
		}
	}

	private void updateList(){
		WhutGlobal.BOOKLIST.clear();
		WhutGlobal.CHILDLIST.clear();
		mDbHelper.fetchBookList();
		myExpandableAdapter.notifyDataSetChanged();
	}
}
