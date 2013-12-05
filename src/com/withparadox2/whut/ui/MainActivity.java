package com.withparadox2.whut.ui;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.AbstractAction;
import com.withparadox2.whut.R;
import com.withparadox2.whut.adapter.MainFragmentPagerAdapter;
import com.withparadox2.whut.fragment.MainFragment1;
import com.withparadox2.whut.fragment.MainFragment2;
import com.withparadox2.whut.util.GlobalConstant;
import com.withparadox2.whut.util.Helper;


public class MainActivity extends FragmentActivity implements MainFragment1.LoadingCallback, MainFragment1.DoneCallback{

    private ViewPager viewPager;
    private MainFragmentPagerAdapter myAdapter;
    private ArrayList<Fragment> fragmentList;
    private MainFragment1 fragment1;
    private MainFragment2 fragment2;
	private ActionBar actionBar;
    private TextView footView;
    
	public static final int PAGE_DEFAULT = -1;
	public static final int PAGE_FIRST = 0;
	public static final int PAGE_SECOND = 1;
	@Override
    protected void onCreate(Bundle arg0) {
	    // TODO Auto-generated method stub
	    super.onCreate(arg0);
	    setContentView(R.layout.main);
        initActionBar();
        initViewPager();
        footView = (TextView) findViewById(R.id.footer_view);
    }
    
	
	@Override
    protected void onResume() {
	    // TODO Auto-generated method stub
	    super.onResume();
        setCurrentPage();
    }


	private void initActionBar() {
	    // TODO Auto-generated method stub
	    actionBar = (ActionBar)findViewById(R.id.actionbar);
        actionBar.setTitle("WHUT");
        actionBar.setHomeAction(new HomeAction(R.drawable.ic_actionbar_whut));
        actionBar.addAction(new AboutAction());
    }
	
	class HomeAction extends AbstractAction{

		public HomeAction(int drawableId) {
	        super(drawableId);
	        // TODO Auto-generated constructor stub
        }

		@Override
        public void performAction(View view) {
	        // TODO Auto-generated method stub
	        
        }
		
	}
	public class AboutAction extends AbstractAction {

		public AboutAction() {
			super(R.drawable.ic_actionbar_about);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void performAction(View view) {
			// TODO Auto-generated method stub
			Intent i = new Intent();
			i.setClass(MainActivity.this, InformationActivity.class);
			startActivity(i);
		}

	}
    
	public static Intent createIntent(Context context) {
		Intent i = new Intent(context, MainActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		return i;
	}
    
	

	private void initViewPager(){
	    viewPager = (ViewPager)findViewById(R.id.main_viewpager);
        fragmentList = new ArrayList<Fragment>();
        fragment1 = new MainFragment1();
        fragment2 = new MainFragment2();
        fragmentList.add(fragment1);
        fragmentList.add(fragment2);
        myAdapter = new MainFragmentPagerAdapter(getSupportFragmentManager(), fragmentList);
        viewPager.setAdapter(myAdapter);
        viewPager.setOnPageChangeListener(new MyPageChangeListener());
	}
    
	
    
	public class MyPageChangeListener implements OnPageChangeListener{

		@Override
        public void onPageSelected(int index) {
	        // TODO Auto-generated method stub
			if(index == 0){
				saveCurrentPageIndex(PAGE_FIRST);
                footView.setBackgroundResource(R.drawable.main_page_indicator_1);
			}else{
				saveCurrentPageIndex(PAGE_SECOND);
                footView.setBackgroundResource(R.drawable.main_page_indicator_2);
			}
        }

		@Override
        public void onPageScrollStateChanged(int arg0) {
	        // TODO Auto-generated method stub
	        
        }

		@Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
	        // TODO Auto-generated method stub
	        
        }
	}

	@Override
    public void loading() {
	    // TODO Auto-generated method stub
	    actionBar.setTitle("正在登陆...");
    }	
	
	@Override
    public void done() {
	    // TODO Auto-generated method stub
	    actionBar.setTitle("WHUT");
    }

	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
	    // TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && fragment1.cancelTask()) {  
        	 actionBar.setTitle("WHUT");
     	     Helper.showShortToast(MainActivity.this, "取消登陆...");
     	     return true;
		} else{
			return super.onKeyDown(keyCode, event);
		}
    }
    
	private void saveCurrentPageIndex(int index){
		SharedPreferences sharedPreferences = getSharedPreferences(GlobalConstant.SP_LOCAL_TEMP, Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putInt(GlobalConstant.CURRENT_PAGE_INDEX, index);
		editor.commit();
	}
	
	private void setCurrentPage(){
		SharedPreferences sharedPreferences = getSharedPreferences(GlobalConstant.SP_LOCAL_TEMP, Context.MODE_PRIVATE);
		int index = sharedPreferences.getInt(GlobalConstant.CURRENT_PAGE_INDEX, PAGE_DEFAULT);
        if(index == PAGE_SECOND){
        	viewPager.setCurrentItem(index);
        }
	}




}
