package com.withparadox2.whut.library.search;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;

import com.withparadox2.whut.dao.BookListTempAdapter;
import com.withparadox2.whut.dao.WhutGlobal;

public class BookListDatabase {
	private Context ctx;
	private BookListTempAdapter mDbHelper;
	
	public BookListDatabase(Context ctx) {
		// TODO Auto-generated constructor stub
		this.ctx = ctx;
		initialDatabase();
	}
	
	private void initialDatabase(){
		mDbHelper = new BookListTempAdapter(ctx);
		mDbHelper.open();
	}
	
	public void addGroupAndChildByPosition(int pos){
		String[] s = WhutGlobal.BOOKLIST.get(pos);
		String bookNum = s[4];
		mDbHelper.addGroupItem(s[0], s[1], s[2], s[3], s[4]);
		List<String[]> list = WhutGlobal.CHILDLIST.get(pos);
		for(int i=0; i<list.size(); i++){
			s = list.get(i);
			mDbHelper.addChildItem(s[1], s[0], bookNum);
		}
	}
	
	public boolean checkWhetherAddBefore(int pos){
		String bookNum = WhutGlobal.BOOKLIST.get(pos)[4];
		Cursor c = mDbHelper.fetchGroupItem(bookNum);
		return c.getCount() == 0;
	}
	
	public void deleteGroupAndChildByPosition(int pos){
		mDbHelper.deleteGroupChildItem(getGroupIdByPos(pos), getChildIdsByPos(pos));
	}
	
	private int getGroupIdByPos(int pos){
		int id = -1;
		String bookNum = WhutGlobal.BOOKLIST.get(pos)[4];
		Cursor c = mDbHelper.fetchGroupItem(bookNum);
		c.moveToNext();
		if(c.getCount() != 0){
			id = c.getInt(c.getColumnIndex(BookListTempAdapter.KEY_ID));
		}
		return id;
	}
	
	private List<Integer> getChildIdsByPos(int pos){
		List<Integer> list = new ArrayList<Integer>();
		String bookNum = WhutGlobal.BOOKLIST.get(pos)[4];
		Cursor c = mDbHelper.fetchChildItemsMatchGroup(bookNum);
		while(c.moveToNext()){
			list.add(c.getInt(c.getColumnIndex(BookListTempAdapter.KEY_ID)));
		}
		return list;
	}
	

}
