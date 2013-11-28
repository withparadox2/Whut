package com.withparadox2.whut.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.withparadox2.whut.R;
import com.withparadox2.whut.dao.UserInfoAdapter;
import com.withparadox2.whut.ui.LoginActivity;

public class LoginPopupListAdapter extends BaseAdapter{
	LayoutInflater layoutInflater;
    private ArrayList<String> userIdList;
    private ArrayList<String> userPasswordList;
    private LoginPopupCallback callback;

	public LoginPopupListAdapter(Context context, ArrayList<String> userIdList, ArrayList<String> userPasswordList, LoginPopupCallback callback) {
		layoutInflater = LayoutInflater.from(context);
        this.callback = callback;
        this.userIdList = userIdList;
        this.userPasswordList = userPasswordList;
	}
    
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return userIdList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View converView, ViewGroup parent) {
		// TODO Auto-generated method stub

		final String userIdText = userIdList.get(position);
		final String userPasswordText = userPasswordList.get(position);
		Holder holder = null;
		if (converView == null) {
			converView = layoutInflater.inflate(R.layout.login_popup_item,
			        null);
			holder = new Holder();
			holder.popupText = (TextView) converView
			        .findViewById(R.id.popup_text);
			holder.deleteButton = (ImageButton) converView
			        .findViewById(R.id.popup_text_delete);
			converView.setTag(holder);
		} else {
			holder = (Holder) converView.getTag();
		}

		holder.popupText.setText(userIdText);
		holder.popupText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				callback.popupTextClick(userIdText, userPasswordText);
			}
		});

		holder.deleteButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				callback.popupDeleteButtonClick(userIdText);
			}
		});
		return converView;
	}
    
	class Holder {
		TextView popupText;
		ImageButton deleteButton;
	}
    
	public interface LoginPopupCallback{
		public void popupTextClick(String userIdText, String userPasswordText);
		public void popupDeleteButtonClick(String userIdText);
	}
}
