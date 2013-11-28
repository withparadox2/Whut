package com.withparadox2.whut.dao;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;

public class LoginDbHelper {
	private UserInfoAdapter dbHelper;
	private Context context;
	private ArrayList<String> userIdList;
    private ArrayList<String> userPasswordList;
    
    public LoginDbHelper(Context context, ArrayList<String> userIdList, ArrayList<String> userPasswordList){
    	this.context = context;
        this.userIdList = userIdList;
        this.userPasswordList = userPasswordList;
        dbHelper = new UserInfoAdapter(context);
        dbHelper.open();
    }
    
	public void updateIdAndPassword(String table, String userIdText, String userPasswordText, boolean isRememberOrNot) {
		long id = dbHelper.returnIdByUserId(table, userIdText);
		if (id != 0) {
			if (isRememberOrNot) {
				dbHelper.updatePassword(table, id, userPasswordText);
			} else {
				dbHelper.updatePassword(table, id, "");
			}
		} else {
			if (isRememberOrNot) {
				dbHelper.addItem(table, userIdText, userPasswordText);
			} else {
				dbHelper.addItem(table, userIdText, "");
			}
		}
	}
    
	public void getListFromDb(String table) {
		userIdList.clear();
		userPasswordList.clear();
		Cursor c = dbHelper.fetchAllItems(table);
        while(c.moveToNext()){
        	userIdList.add(c.getString(c.getColumnIndexOrThrow(UserInfoAdapter.KEY_USER_ID)));
			userPasswordList.add(c.getString(c.getColumnIndexOrThrow(UserInfoAdapter.KEY_USER_PASSWORD)));
        }
		c.close();
	}
    
    public void deleteUserItem(boolean jiaoTuFlag, String userIdText){
    	if (jiaoTuFlag) {
			long id = dbHelper.returnIdByUserId(UserInfoAdapter.DATABASE_JIAO_TABLE_NAME, userIdText);
			if (id != 0) {
				dbHelper.deleteItem(UserInfoAdapter.DATABASE_JIAO_TABLE_NAME,id);
			}
			getListFromDb(UserInfoAdapter.DATABASE_JIAO_TABLE_NAME);
		} else {
			long id = dbHelper.returnIdByUserId(UserInfoAdapter.DATABASE_TU_TABLE_NAME, userIdText);
			if (id != 0) {
				dbHelper.deleteItem(UserInfoAdapter.DATABASE_TU_TABLE_NAME, id);
			}
			getListFromDb(UserInfoAdapter.DATABASE_TU_TABLE_NAME);
		}
    }
}
    
