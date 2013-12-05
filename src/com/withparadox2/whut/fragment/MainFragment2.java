package com.withparadox2.whut.fragment;

import com.withparadox2.whut.R;
import com.withparadox2.whut.dao.WhutGlobal;
import com.withparadox2.whut.ui.BookListTempActivity;
import com.withparadox2.whut.ui.LiXianJieYueActivity;
import com.withparadox2.whut.ui.LiXianKeBiaoActivity;
import com.withparadox2.whut.ui.SearchBookActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainFragment2 extends Fragment{
	private Button searchBookButton;
	private Button lixianJieyueButton;
	private Button lixianKebiaoButton;
	private Button booksTempButton;
	private Activity activity;
	@Override
    public void onCreate(Bundle savedInstanceState) {
	    // TODO Auto-generated method stub
	    super.onCreate(savedInstanceState);
	    activity = getActivity();
    }

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
	    // TODO Auto-generated method stub
        View v = inflater.inflate(R.layout.main_fragment_2, container, false);
		lixianJieyueButton = (Button) v.findViewById(R.id.lixian_jieyue_button);
		lixianKebiaoButton = (Button) v.findViewById(R.id.lixian_kebiao_button);
		searchBookButton = (Button) v.findViewById(R.id.search_book_button);
		booksTempButton = (Button) v.findViewById(R.id.books_temp_button);
		lixianJieyueButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getLiXianJieYueData();
			}
		});
		lixianKebiaoButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				getLiXianKeBiaoData();
			}
		});
		searchBookButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				WhutGlobal.BOOKLIST.clear();
				WhutGlobal.CHILDLIST.clear();
				WhutGlobal.CLICK_GROUP_FLAG.clear();
				Intent i = new Intent();
				i.setClass(activity, SearchBookActivity.class);
				startActivity(i);
			}
		});
		booksTempButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				WhutGlobal.BOOKLIST.clear();
				WhutGlobal.CHILDLIST.clear();
				WhutGlobal.CLICK_GROUP_FLAG.clear();
				Intent i = new Intent();
				i.setClass(activity, BookListTempActivity.class);
				startActivity(i);
			}
		});
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
    
	private void getLiXianJieYueData() {
		boolean exist = false;
		String[][] jieYue = null;
		SharedPreferences share = activity.getSharedPreferences(
		        WhutGlobal.SHARE_LIXIAN_JIEYUE_NAME, Activity.MODE_PRIVATE);
		int length = Integer.parseInt(share.getString("length", "100"));
        System.out.println(length);
		if (length < 100) {
			exist = true;
			jieYue = new String[length][4];
			for (int i = 0; i < length; i++) {
				jieYue[i][0] = share.getString("title" + i, "");
				jieYue[i][1] = share.getString("start_time" + i, "");
				jieYue[i][2] = share.getString("end_time" + i, "");
				jieYue[i][3] = share.getString("location" + i, "");
			}
		}

		if (exist) {
			WhutGlobal.htmlData = jieYue;
			Intent i = new Intent();
			i.setClass(activity, LiXianJieYueActivity.class);
			startActivity(i);
		} else {
			Toast.makeText(activity, "未下载离线数据！",
			        Toast.LENGTH_LONG).show();
		}
	}

	private void getLiXianKeBiaoData() {
		boolean exist = true;
		String[][] kebiao = new String[4][5];
		SharedPreferences share = activity.getSharedPreferences(
		        WhutGlobal.ShARE_LIXIAN_KEBIAO_NAME, Activity.MODE_PRIVATE);
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 5; j++) {
				kebiao[i][j] = share.getString("" + (j + 5 * i), "不存在");
				if (kebiao[i][j].equals("不存在"))
					exist = false;
			}
		}

		if (exist) {
			WhutGlobal.htmlData = kebiao;
			Intent i = new Intent();
			i.setClass(activity, LiXianKeBiaoActivity.class);
			startActivity(i);
		} else {
			Toast.makeText(activity, "未下载离线课表！",
			        Toast.LENGTH_LONG).show();
		}
	}
}
