package com.withparadox2.whut.fragment;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.markupartist.android.widget.ActionBar;
import com.withparadox2.whut.R;
import com.withparadox2.whut.adapter.LoginPopupListAdapter;
import com.withparadox2.whut.dao.LoginDbHelper;
import com.withparadox2.whut.dao.UserInfoAdapter;
import com.withparadox2.whut.http.HttpOperateThread;
import com.withparadox2.whut.http.HttpOperation;
import com.withparadox2.whut.http.LoginJiaoTask;
import com.withparadox2.whut.ui.WelcomeJiaoActivity;
import com.withparadox2.whut.util.GlobalConstant;
import com.withparadox2.whut.util.Helper;

public class MainFragment1 extends Fragment {
	private Activity activity;
	private EditText userIdEdit;
	private EditText userPasswordEdit;
	private TextView userIdTextView;
	private String userIdText;
	private String userPasswordText;
	private Button submitButton;
	private Button liXianButton;
	private Button ToTuButton;
	private Button ToJiaoButton;
	private CheckBox checkBox;
	private ImageButton popupButton;
	private PopupWindow popupWindow;
	private boolean isPopupOrNot = false;
	private ListView popupListView;
	private ArrayList<String> userIdList;
	private ArrayList<String> userPasswordList;
	private UserInfoAdapter dbHelper;
	private boolean isRememberOrNot = false;
	private ProgressDialog progressDialog;
	private HttpOperateThread myThread;
	private HttpOperation httpOperation;
	private ActionBar actionBar;
	private boolean cancelDialogByHand = false;
	private int closeHttpFlag;// 是关闭httpurl还是关闭httppost
	private boolean jiaoTuFlag = true;// true for jiaowuchu, false for library
    
	private String userName;
	private Callback callback;
	private boolean isLoading;
	private LoginJiaoTask loginJiaoTask;
    
    private LoginPopupListAdapter myListAdapter;
    private LoginDbHelper loginDbHelper;

    
    /*
     * 登陆时候调用此方法，MainActivity实现此接口
     */
	public interface Callback{
		public void loading();
	}
    
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
	    // TODO Auto-generated method stub
	    super.onCreate(savedInstanceState);
	    activity = getActivity();
	    userIdList = new ArrayList<String>();
		userPasswordList = new ArrayList<String>();
        loginDbHelper = new LoginDbHelper(activity, userIdList, userPasswordList);
	  
    }

    
	@Override
    public void onAttach(Activity activity) {
	    // TODO Auto-generated method stub
	    super.onAttach(activity);
	    try {
	    	callback = (Callback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }	   
    }


	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
	    // TODO Auto-generated method stub
        View v = inflater.inflate(R.layout.main_fragment_1, container, false);
        initialize(v);
        setForJiaoOrTu();
        return v;
    }
	
	@Override
    public void onResume() {
	    // TODO Auto-generated method stub
	    super.onResume();
    }

	@Override
    public void onStop() {
	    // TODO Auto-generated method stub
	    super.onStop();
    }
    
	
    private void initialize(View v) {
		userIdEdit = (EditText) v.findViewById(R.id.loginUserId);
		userPasswordEdit = (EditText) v.findViewById(R.id.loginUserPassword);
		
		submitButton = (Button) v.findViewById(R.id.loginButton);
		submitButton.setOnClickListener(new SubmitOnClickListener());
		
		popupButton = (ImageButton) v.findViewById(R.id.popup_button);
		popupButton.setOnClickListener(new PopupOnClickListener());
		
		checkBox = (CheckBox) v.findViewById(R.id.remember_password);
		userIdTextView = (TextView) v.findViewById(R.id.login_userid_text);
		ToTuButton = (Button) v.findViewById(R.id.to_tu_button);
		ToJiaoButton = (Button) v.findViewById(R.id.to_jiao_button);
        
		submitButton.setOnClickListener(new SubmitOnClickListener());
		popupButton.setOnClickListener(new PopupOnClickListener());
		checkBox.setOnCheckedChangeListener(new RememberCheckedListener());
		ToTuButton.setOnClickListener(new ToTuOnClickListener());
		ToJiaoButton.setOnClickListener(new ToJiaoOnClickListener());
		userIdEdit.setOnClickListener(new EditClickListener());
        
		myListAdapter = new LoginPopupListAdapter(activity, userIdList, userPasswordList, new LoginPopupListAdapter.LoginPopupCallback() {
			
			@Override
			public void popupTextClick(String userIdText, String userPasswordText) {
				// TODO Auto-generated method stub
				popupTextClickMethod(userIdText, userPasswordText);
			}
			
			@Override
			public void popupDeleteButtonClick(String userIdText) {
				// TODO Auto-generated method stub
				loginDbHelper.deleteUserItem(jiaoTuFlag, userIdText);
                myListAdapter.notifyDataSetChanged();
			}
		});
	}
    
	class EditClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (!jiaoTuFlag) {
				if (userIdEdit.getText().length() == 0) {
					userIdEdit.setText("0000");
					userIdEdit.setSelection(4);
				}
			}
		}

	}

	public class ToTuOnClickListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			if (jiaoTuFlag) {
				jiaoTuFlag = !jiaoTuFlag;
				setForJiaoOrTu();
			}
		}

	}

	public class ToJiaoOnClickListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			if (!jiaoTuFlag) {
				jiaoTuFlag = !jiaoTuFlag;
				setForJiaoOrTu();
			}
		}

	}
    
	private class RememberCheckedListener implements OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
		        boolean isChecked) {
			// TODO Auto-generated method stub
			isRememberOrNot = isChecked;
		}

	}
    
    private void popupTextClickMethod(String userIdText, String userPasswordText){
		popupWindow.dismiss();
		isPopupOrNot = false;
		userIdEdit.setText(userIdText);
		userIdEdit.setSelection(userIdEdit.getText().length());
		if (userPasswordText.equals("")) {
			checkBox.setChecked(false);
			isRememberOrNot = false;
		} else {
			checkBox.setChecked(true);
			isRememberOrNot = true;
		}
		userPasswordEdit.setText(userPasswordText);
		userPasswordEdit.setSelection(userPasswordEdit.getText().length());
    }

	private class SubmitOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			userIdText = userIdEdit.getText().toString().trim();
			userPasswordText = userPasswordEdit.getText().toString().trim();
			if (!userIdText.equals("") && !userPasswordText.equals("")) {
				callback.loading();
				isLoading = true;
				loginJiaoTask = new LoginJiaoTask(new LoginJiaoTaskCallBack());
                loginJiaoTask.execute(userIdText, userPasswordText);
			}else{
				Helper.showShortToast(activity, "输入不可以为空...");
			}
		}
	}
    
	public class LoginJiaoTaskCallBack implements LoginJiaoTask.CallBack{

		@Override
		public void onPostExecute(String result) {
			// TODO Auto-generated method stub             
            isLoading = false;
            Helper.saveValueInSharePreference(activity, GlobalConstant.SP_LOCAL_TEMP, GlobalConstant.USER_NAME, result);
            Helper.saveValueInSharePreference(activity, GlobalConstant.SP_LOCAL_TEMP, GlobalConstant.USER_ID, userIdText);
            Helper.saveValueInSharePreference(activity, GlobalConstant.SP_LOCAL_TEMP, GlobalConstant.USER_PASSWORD, userPasswordText);
			Intent i = new Intent();
			i.setClass(activity, WelcomeJiaoActivity.class);
			startActivity(i);
		}
		
	}
    
    public boolean cancelTask(){
    	if(isLoading){
    		return loginJiaoTask.cancel(true);
    	}else{
    		return false;
    	}
    }
    
    
	private void updateIdAndPassword(String table) {
        loginDbHelper.updateIdAndPassword(table, userIdText, userPasswordText, isRememberOrNot);
        loginDbHelper.getListFromDb(table);
		if (myListAdapter != null)
			myListAdapter.notifyDataSetChanged();
	}
	
	private class PopupOnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (popupWindow == null) {
				popupListView = new ListView(activity);
				popupListView.setAdapter(myListAdapter);
				popupWindow = new PopupWindow(popupListView, userIdEdit.getWidth(), LayoutParams.WRAP_CONTENT);
				popupWindow.showAsDropDown(userIdEdit);
				isPopupOrNot = true;
			} else if (isPopupOrNot) {
				popupWindow.dismiss();
				isPopupOrNot = false;
			} else if (!isPopupOrNot) {
				popupWindow.showAsDropDown(userIdEdit);
				isPopupOrNot = true;
			}
		}
	}
    
	private void setForJiaoOrTu() {
		// 选择教务处或者图书馆
		if (jiaoTuFlag) {
			userIdTextView.setText("学号  ");
			userIdEdit.setHint("请输入学号");
			loginDbHelper.getListFromDb(UserInfoAdapter.DATABASE_JIAO_TABLE_NAME);
			ToTuButton.setBackgroundResource(R.drawable.to_tu_button_normal);
			ToJiaoButton.setBackgroundResource(R.drawable.to_jiao_button_selected);
		} else {
			userIdTextView.setText("卡号  ");
			userIdEdit.setHint("请输入卡号(前加4个0)");
			loginDbHelper.getListFromDb(UserInfoAdapter.DATABASE_TU_TABLE_NAME);
			ToTuButton.setBackgroundResource(R.drawable.to_tu_button_selectedl);
			ToJiaoButton.setBackgroundResource(R.drawable.to_jiao_button_normal);
		}
		userIdEdit.setText("");
		userPasswordEdit.setText("");
		if (myListAdapter != null)
			myListAdapter.notifyDataSetChanged();
	}
    
}
