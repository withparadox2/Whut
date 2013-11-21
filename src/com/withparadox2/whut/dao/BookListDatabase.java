package com.withparadox2.whut.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;

public class BookListDatabase {
	private Context ctx;
	private BookListTempAdapter mDbHelper;

	public BookListDatabase(Context ctx) {
		// TODO Auto-generated constructor stub
		this.ctx = ctx;
		initialDatabase();
	}

	private void initialDatabase() {
		mDbHelper = new BookListTempAdapter(ctx);
		mDbHelper.open();
	}

	public void addGroupAndChildByPosition(int pos) {
		String[] s = WhutGlobal.BOOKLIST.get(pos);
		String bookNum = s[1];
		mDbHelper.addGroupItem(s[0], s[1]);
		List<String[]> list = WhutGlobal.CHILDLIST.get(pos);
		for (int i = 0; i < list.size(); i++) {
			s = list.get(i);
			mDbHelper.addChildItem(s[1], s[0], bookNum);
		}
	}

	public boolean checkWhetherAddBefore(int pos) {
		String bookNum = WhutGlobal.BOOKLIST.get(pos)[1];
		Cursor c = mDbHelper.fetchGroupItem(bookNum);
		return c.getCount() == 0;
	}

	public void deleteGroupAndChildByPosition(int pos) {
		mDbHelper.deleteGroupChildItem(getGroupIdByPos(pos),
		        getChildIdsByPos(pos));
	}

	public void deleteGroupAndChildByIds(int groupId, List<Integer> childIds) {
		mDbHelper.deleteGroupChildItem(groupId, childIds);
	}

	private int getGroupIdByPos(int pos) {
		int id = -1;
		String bookNum = WhutGlobal.BOOKLIST.get(pos)[1];
		Cursor c = mDbHelper.fetchGroupItem(bookNum);
		c.moveToNext();
		if (c.getCount() != 0) {
			id = c.getInt(c.getColumnIndex(BookListTempAdapter.KEY_ID));
		}
		return id;
	}

	private List<Integer> getChildIdsByPos(int pos) {
		List<Integer> list = new ArrayList<Integer>();
		String bookNum = WhutGlobal.BOOKLIST.get(pos)[1];
		Cursor c = mDbHelper.fetchChildItemsMatchGroup(bookNum);
		while (c.moveToNext()) {
			list.add(c.getInt(c.getColumnIndex(BookListTempAdapter.KEY_ID)));
		}
		return list;
	}

	public void fetchBookList() {
		ArrayList<String[]> groupList = new ArrayList<String[]>();
		ArrayList<ArrayList<String[]>> childList = new ArrayList<ArrayList<String[]>>();
		Cursor c = mDbHelper.fetchAllGroupItems();
		String[] s;
		while (c.moveToNext()) {
			s = new String[3];
			s[0] = c.getString(c
			        .getColumnIndex(BookListTempAdapter.KEY_BOOK_DETAIL));
			s[1] = c.getString(c
			        .getColumnIndex(BookListTempAdapter.KEY_BOOK_NUM));
			s[2] = c.getString(c.getColumnIndex(BookListTempAdapter.KEY_ID));
			groupList.add(s);
		}
		ArrayList<String[]> myList;
		String[] childS;
		for (int i = 0, size = groupList.size(); i < size; i++) {
			c = mDbHelper.fetchChildItemsMatchGroup(groupList.get(i)[1]);
			myList = new ArrayList<String[]>();
			while (c.moveToNext()) {
				childS = new String[3];
				childS[0] = c.getString(c
				        .getColumnIndex(BookListTempAdapter.KEY_BOOK_RECNO));
				childS[1] = c.getString(c
				        .getColumnIndex(BookListTempAdapter.KEY_BOOK_LOCATION));
				childS[2] = c.getString(c
				        .getColumnIndex(BookListTempAdapter.KEY_ID));
				myList.add(childS);
			}
			childList.add(myList);
		}
		WhutGlobal.BOOKLIST = groupList;
		WhutGlobal.CHILDLIST = childList;
	}

}
