package com.withparadox2.whut.fragment;

import java.util.List;

import com.markupartist.android.widget.ActionBar;
import com.withparadox2.whut.R;
import com.withparadox2.whut.dao.UserInfoAdapter;
import com.withparadox2.whut.dao.WhutGlobal;
import com.withparadox2.whut.http.HttpOperateThread;
import com.withparadox2.whut.http.HttpOperation;
import com.withparadox2.whut.http.LoginJiaoTask;
import com.withparadox2.whut.ui.LoginActivity;
import com.withparadox2.whut.ui.LoginActivity.UpdateUIHandler;
import com.withparadox2.whut.ui.MainActivity;
import com.withparadox2.whut.ui.WelcomeJiaoActivity;
import com.withparadox2.whut.util.Helper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

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
	private List<String> userIdList;
	private List<String> userPasswordList;
	private UserInfoAdapter dbHelper;
	private boolean isRememberOrNot = false;
	private ProgressDialog progressDialog;
	private HttpOperateThread myThread;
	private UpdateUIHandler myHandler;
	private HttpOperation httpOperation;
	private ActionBar actionBar;
	private boolean cancelDialogByHand = false;
	private int closeHttpFlag;// 是关闭httpurl还是关闭httppost
	private boolean jiaoTuFlag = true;// true for jiaowuchu, false for library
    
	private String userName;
	private Callback callback;
	private boolean isLoading;
	private LoginJiaoTask loginJiaoTask;
    
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
		checkBox = (CheckBox) v.findViewById(R.id.remember_password);
		liXianButton = (Button) v.findViewById(R.id.lixian_kebiao_button);
		userIdTextView = (TextView) v.findViewById(R.id.login_userid_text);
		ToTuButton = (Button) v.findViewById(R.id.to_tu_button);
		ToJiaoButton = (Button) v.findViewById(R.id.to_jiao_button);
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
			Intent i = new Intent();
            i.putExtra("UserName", result);
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
	
}
