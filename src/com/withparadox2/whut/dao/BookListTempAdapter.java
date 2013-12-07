package com.withparadox2.whut.dao;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BookListTempAdapter {

	private Context ctx;
	private DatabaseHelper dbHelper;
	private SQLiteDatabase mDb;

	public static final String DATABASE_NAME = "book_list";
	public static final String GROUP_TABLE_NAME = "group_table";
	public static final String CHILD_TABLE_NAME = "child_table";
	public static final int DATABASE_VERSION = 1;
	public static final String KEY_ID = "_id";
	public static final String KEY_BOOK_NAME = "book_name";
	public static final String KEY_BOOK_RECNO = "book_recno";
	public static final String KEY_BOOK_NUM = "book_num";
	public static final String KEY_BOOK_DETAIL = "book_detail";
	public static final String KEY_BOOK_LOCATION = "book_location";

	public final String CREAT_CHILD_TABLE = "create table " + CHILD_TABLE_NAME
	        + " (" + KEY_ID + " integer primary key autoincrement, "
	        + KEY_BOOK_LOCATION + " text not null," + KEY_BOOK_RECNO
	        + " text not null," + KEY_BOOK_NUM + " text not null);";
	public final String CREAT_GROUP_TABLE = "create table " + GROUP_TABLE_NAME
	        + " (" + KEY_ID + " integer primary key autoincrement, "
	        + KEY_BOOK_DETAIL + " text not null," + KEY_BOOK_NUM
	        + " text not null);";

	public BookListTempAdapter(Context ctx) {
		this.ctx = ctx;
	}

	public class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			db.execSQL(CREAT_CHILD_TABLE);
			db.execSQL(CREAT_GROUP_TABLE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub

		}
	}

	public BookListTempAdapter open() throws SQLException {
		dbHelper = new DatabaseHelper(ctx);
		mDb = dbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		mDb.close();
	}

	public long addGroupItem(String detail, String num) {
		ContentValues con = new ContentValues();
		con.put(KEY_BOOK_DETAIL, detail);
		con.put(KEY_BOOK_NUM, num);
		return mDb.insert(GROUP_TABLE_NAME, null, con);
	}

	public long addChildItem(String location, String recno, String num) {
		ContentValues con = new ContentValues();
		con.put(KEY_BOOK_LOCATION, location);
		con.put(KEY_BOOK_RECNO, recno);
		con.put(KEY_BOOK_NUM, num);
		return mDb.insert(CHILD_TABLE_NAME, null, con);
	}

	public Cursor fetchAllGroupItems() {
		return mDb.rawQuery("SELECT * FROM " + GROUP_TABLE_NAME, null);
	}

	public Cursor fetchChildItemsMatchGroup(String num) {
		return fetchItem(num, CHILD_TABLE_NAME);
	}

	public Cursor fetchGroupItem(String num) {
		return fetchItem(num, GROUP_TABLE_NAME);
	}

	private Cursor fetchItem(String num, String tableName) {
		return mDb.rawQuery("SELECT * FROM " + tableName + " WHERE "
		        + KEY_BOOK_NUM + " = '" + num + "'", null);
	}

	private boolean deleteItem(int rowId, String tableName) {
		return mDb.delete(tableName, KEY_ID + "=" + rowId, null) > 0;
	}

	public void deleteGroupChildItem(int groupId, List<Integer> childIds) {
		deleteItem(groupId, GROUP_TABLE_NAME);
		for (int itemId : childIds) {
			deleteItem(itemId, CHILD_TABLE_NAME);
		}
	}
}
