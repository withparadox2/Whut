package com.withparadox2.whut.adapter;

import java.util.Map;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.withparadox2.whut.R;
import com.withparadox2.whut.dao.BookListDatabase;
import com.withparadox2.whut.dao.WhutGlobal;

public class MyExpandableListAdapter extends BaseExpandableListAdapter{
    
    private Context context;
	private Map<Integer, Boolean> addToDatabaseFlag; 
	private BookListDatabase mDbHelper;
	
    public MyExpandableListAdapter(Context context, Map<Integer, Boolean> addToDatabaseFlag ){
    	this.context = context;
    	this.addToDatabaseFlag = addToDatabaseFlag;
        mDbHelper = new BookListDatabase(context);
    }
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
		if (convertView == null) {
			convertView = LayoutInflater.from(context)
			        .inflate(R.layout.book_expandable_child, null);
			viewHolder = new ChildViewHolder();
			viewHolder.bookLocation = (TextView) convertView
			        .findViewById(R.id.book_location);
			viewHolder.bookCode = (TextView) convertView
			        .findViewById(R.id.book_code);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ChildViewHolder) convertView.getTag();
		}
		viewHolder.bookLocation.setText(WhutGlobal.CHILDLIST.get(
		        groupPosition).get(childPosition)[1]);
		viewHolder.bookCode.setText(WhutGlobal.CHILDLIST.get(groupPosition)
		        .get(childPosition)[0]);

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
		if (convertView == null) {
			convertView = LayoutInflater.from(context)
			        .inflate(R.layout.book_expandable_group, null);
			viewHolder = new GroupViewHolder();
			viewHolder.detail = (TextView) convertView
			        .findViewById(R.id.book_detail);
			viewHolder.childFlag = (TextView) convertView
			        .findViewById(R.id.get_child_flag);
			viewHolder.addToDbButton = (Button) convertView
			        .findViewById(R.id.add_book_button);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (GroupViewHolder) convertView.getTag();
		}
		viewHolder.detail
		        .setText(WhutGlobal.BOOKLIST.get(groupPosition)[0]);
		viewHolder.childFlag.setBackgroundColor(addToDatabaseFlag
		        .get(groupPosition) == null ? context.getResources().getColor(R.color.searchbooks_group_background)
		        : (addToDatabaseFlag.get(groupPosition) ? Color.RED
		                : context.getResources().getColor(R.color.searchbooks_group_background)));
		viewHolder.addToDbButton.setText(addToDatabaseFlag
		        .get(groupPosition) == null ? "Ìí¼Ó" : (addToDatabaseFlag
		        .get(groupPosition) ? "É¾³ý" : "Ìí¼Ó"));
		viewHolder.addToDbButton.setOnClickListener(new AddClickListener(
		        groupPosition));
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

	public class GroupViewHolder {
		TextView detail;
		TextView childFlag;
		Button addToDbButton;
	}

	public class ChildViewHolder {
		TextView bookCode;
		TextView bookLocation;
	}

	public class AddClickListener implements OnClickListener {

		int groupPosition;

		public AddClickListener(int groupPosition) {
			this.groupPosition = groupPosition;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (mDbHelper.checkWhetherAddBefore(groupPosition)) {
				mDbHelper.addGroupAndChildByPosition(groupPosition);
				addToDatabaseFlag.put(groupPosition, true);
			} else {
				mDbHelper.deleteGroupAndChildByPosition(groupPosition);
				addToDatabaseFlag.put(groupPosition, false);
			}
			MyExpandableListAdapter.this.notifyDataSetChanged();

		}

	}

}
