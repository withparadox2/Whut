package com.withparadox2.whut.adapter;

import java.util.Map;

import com.withparadox2.whut.R;
import com.withparadox2.whut.adapter.MyExpandableListAdapter.AddClickListener;
import com.withparadox2.whut.adapter.MyExpandableListAdapter.GroupViewHolder;
import com.withparadox2.whut.dao.BookListDatabase;
import com.withparadox2.whut.dao.WhutGlobal;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class MyListAdapter extends BaseAdapter{

	private Context context;
	private Map<Integer, Boolean> addToDatabaseFlag; 
	private BookListDatabase mDbHelper;
    		
    public MyListAdapter(Context context, Map<Integer, Boolean> addToDatabaseFlag ){
    	this.context = context;
    	this.addToDatabaseFlag = addToDatabaseFlag;
        mDbHelper = new BookListDatabase(context);
    }
	@Override
    public int getCount() {
	    // TODO Auto-generated method stub
		return WhutGlobal.BOOKLIST.size();
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
    public View getView(int groupPosition,View convertView, ViewGroup parent) {
	    // TODO Auto-generated method stub
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context)
			        .inflate(R.layout.book_expandable_group, null);
			viewHolder = new ViewHolder();
			viewHolder.groupDetail = (TextView) convertView.findViewById(R.id.book_detail);
			viewHolder.childFlag = (TextView) convertView.findViewById(R.id.get_child_flag);
			viewHolder.addToDbButton = (Button) convertView.findViewById(R.id.add_book_button);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.groupDetail.setText(WhutGlobal.BOOKLIST.get(groupPosition)[0]);
		viewHolder.childFlag.setBackgroundColor(addToDatabaseFlag
		        .get(groupPosition) == null ? Color.WHITE
		        : (addToDatabaseFlag.get(groupPosition) ? Color.RED
		                : Color.WHITE));
		viewHolder.addToDbButton.setText(addToDatabaseFlag
		        .get(groupPosition) == null ? "Ìí¼Ó" : (addToDatabaseFlag
		        .get(groupPosition) ? "É¾³ý" : "Ìí¼Ó"));
		viewHolder.addToDbButton.setOnClickListener(new AddClickListener(
		        groupPosition));
		return convertView;
    }
    
	public class ViewHolder {
		TextView groupDetail;
		TextView childDetail;
		TextView childFlag;
		Button addToDbButton;
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
			MyListAdapter.this.notifyDataSetChanged();

		}

	}

}
