package com.withparadox2.whut.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserInfoAdapter {
	
	private final Context myContext;
    private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	
	public static final String DATABASE_NAME = "user_info_db";
	public static final String DATABASE_JIAO_TABLE_NAME = "user_info_table";
	public static final String DATABASE_TU_TABLE_NAME = "user_info_table2";
	public static final int DATABASE_VERISION = 1;
	public static final String KEY_ID = "_id";
	public static final String KEY_USER_ID = "user_id";
	public static final String KEY_USER_PASSWORD = "user_password";
	public static final String KEY_USER_NAME = "user_name";
	public static final String KEY_CLASS_TABLE = "class_table";
	
	public final String creatJiaoTable = "create table "
			+ DATABASE_JIAO_TABLE_NAME + " ("
			+ KEY_ID +" integer primary key autoincrement, "
			+ KEY_USER_ID + " text not null,"
	        + KEY_USER_PASSWORD + " text not null);";
	
	public final String creatTuTable = "create table "
			+ DATABASE_TU_TABLE_NAME + " ("
			+ KEY_ID +" integer primary key autoincrement, "
			+ KEY_USER_ID + " text not null,"
	        + KEY_USER_PASSWORD + " text not null);";
	
	
	
	
	
	public class DatabaseHelper extends SQLiteOpenHelper{

		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERISION);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			db.execSQL(creatJiaoTable);
			db.execSQL(creatTuTable);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			db.execSQL(creatTuTable);
			List<String> list1 = new ArrayList<String>();
			List<String> list2 = new ArrayList<String>();
		    Cursor c = null;
		    try {
		        c = db.rawQuery("select * from " + DATABASE_JIAO_TABLE_NAME , null);
		        c.moveToFirst();
		        while(!c.isAfterLast()){
		            	list1.add(c.getString(c.getColumnIndex(KEY_USER_ID)));
		            	list2.add(c.getString(c.getColumnIndex(KEY_USER_PASSWORD)));
		            	c.moveToNext();
		        }
		    } catch (Exception e) {
		        e.printStackTrace();
		    } finally {
		        if (c != null)
		            c.close();
		    }
		    mDb.execSQL("DROP TABLE IF EXISTS "+DATABASE_JIAO_TABLE_NAME);
		    mDb.execSQL(creatJiaoTable);
		    for(int i=0; i<list1.size(); i++){
		    	ContentValues con = new ContentValues();
				con.put(KEY_USER_ID, list1.get(i));
				con.put(KEY_USER_PASSWORD,  list2.get(i));
				db.insert(DATABASE_JIAO_TABLE_NAME, null, con);
		    }
		}
		
	}
	
	
	public UserInfoAdapter(Context context){
		this.myContext = context;
	}
	
	public UserInfoAdapter open() throws SQLException{
		mDbHelper = new DatabaseHelper(myContext);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}
	
	public void close(){
		mDb.close();
	}
	
	public long addItem(String table, String userId, String userPassword){
		ContentValues con = new ContentValues();
		con.put(KEY_USER_ID, userId);
		con.put(KEY_USER_PASSWORD, userPassword);
		return mDb.insert(table, null, con);
	}
	
	public boolean deleteItem(String table, long rowId){
		return mDb.delete(table, KEY_ID + "=" + rowId, null) > 0;
	}
	
	public Cursor fetchAllItems(String table) throws SQLException{
		return mDb.rawQuery("SELECT * FROM "+ table, null);
	}
	
	public Cursor fetchItemByUserId(String table, String userId) throws SQLException{
		return mDb.rawQuery("SELECT * FROM " + table + " WHERE " + KEY_USER_ID + " = '"+ userId +"'" , null);
	}
	
	public long returnIdByUserId(String table, String userId){
		Cursor c = fetchItemByUserId(table, userId);
		long id;
		if(c.getCount()!=0){
			c.moveToFirst();
			id = c.getLong(c.getColumnIndexOrThrow(KEY_ID));
		}else{
			id = 0;
		}
		c.close();
		return id;
	}
	
	public boolean updatePassword(String table, long id, String userPassword) {
      ContentValues con = new ContentValues();
      con.put(KEY_USER_PASSWORD, userPassword);
      return mDb.update(table, con, KEY_ID + "=" + id, null) > 0;
  }

}
