package com.withparadox2.whut;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.IntentAction;
import com.withparadox2.whut.R;
import com.withparadox2.whut.dao.WhutGlobal;
import com.withparadox2.whut.library.search.BookListTempActivity;
import com.withparadox2.whut.library.search.SearchBookActivity;

public class LiXianHomeActivity extends Activity{

	private EditText searchBookEditText;
	private Button searchBookButton;
	private Button lixianJieyueButton;
	private Button lixianKebiaoButton;
	private Button booksTempButton;
	private ActionBar actionBar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lixian_home);
		lixianJieyueButton = (Button)findViewById(R.id.lixian_jieyue_button);
		lixianKebiaoButton = (Button)findViewById(R.id.lixian_kebiao_button);
		searchBookButton = (Button)findViewById(R.id.search_book_button);
		booksTempButton = (Button)findViewById(R.id.books_temp_button);
		actionBar = (ActionBar) findViewById(R.id.lixian_actionbar);
        actionBar.setHomeAction(new IntentAction(this, LoginActivity.createIntent(this), R.drawable.ic_actionbar_whut));
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("辅助功能");
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
				i.setClass(LiXianHomeActivity.this, SearchBookActivity.class);
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
				i.setClass(LiXianHomeActivity.this, BookListTempActivity.class);
				startActivity(i);
			}
		});
	}
	
    public static Intent createIntent(Context context) {
        Intent i = new Intent(context, LiXianHomeActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return i;
    }
	
	private void getLiXianJieYueData(){
		boolean exist = false;
		String[][] jieYue = null;
		SharedPreferences share = getSharedPreferences(WhutGlobal.SHARE_LIXIAN_JIEYUE_NAME, Activity.MODE_PRIVATE);
		int length = Integer.parseInt(share.getString("length", "100"));
		if(length<100){
			exist = true;
			jieYue = new String[length][3];
			for(int i=0; i<length; i++){
				jieYue[i][0] = share.getString("title"+i, "");
				jieYue[i][1] = share.getString("start_time"+i, "");
				jieYue[i][2] = share.getString("end_time"+i, "");
			}
		}
		
		if(exist){
			WhutGlobal.htmlData = jieYue;
			Intent i = new Intent();
			i.setClass(LiXianHomeActivity.this, LiXianJieYueActivity.class);
			startActivity(i);
		}else{
			Toast.makeText(LiXianHomeActivity.this, "未下载离线数据！", Toast.LENGTH_LONG).show();
		}
	}

	private void getLiXianKeBiaoData(){
		boolean exist = true;
		String[][] kebiao = new String[4][5];
		SharedPreferences share = getSharedPreferences(WhutGlobal.ShARE_LIXIAN_KEBIAO_NAME, Activity.MODE_PRIVATE);
		for(int i=0; i<4; i++){
			for(int j=0; j<5; j++){
				kebiao[i][j] = share.getString(""+(j+5*i), "不存在");
				if(kebiao[i][j].equals("不存在")) exist = false;
			}
		}
		
		if(exist){
			WhutGlobal.htmlData = kebiao;
			Intent i = new Intent();
			i.setClass(LiXianHomeActivity.this, LiXianKeBiaoActivity.class);
			startActivity(i);
		}else{
			Toast.makeText(LiXianHomeActivity.this, "未下载离线课表！", Toast.LENGTH_LONG).show();
		}
	}
	
	
}
