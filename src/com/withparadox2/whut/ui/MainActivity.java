package com.withparadox2.whut.ui;

import java.util.ArrayList;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.AbstractAction;
import com.withparadox2.whut.R;
import com.withparadox2.whut.adapter.MainFragmentPagerAdapter;
import com.withparadox2.whut.fragment.MainFragment1;
import com.withparadox2.whut.fragment.MainFragment2;
import com.withparadox2.whut.util.Helper;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;


public class MainActivity extends FragmentActivity implements MainFragment1.Callback{

    private ViewPager viewPager;
    private MainFragmentPagerAdapter myAdapter;
    private ArrayList<Fragment> fragmentList;
    private MainFragment1 fragment1;
    private MainFragment2 fragment2;
	private ActionBar actionBar;
    
	@Override
    protected void onCreate(Bundle arg0) {
	    // TODO Auto-generated method stub
	    super.onCreate(arg0);
	    setContentView(R.layout.main);
        initActionBar();
        initViewPager();
    }
    
	private void initActionBar() {
	    // TODO Auto-generated method stub
	    actionBar = (ActionBar)findViewById(R.id.actionbar);
        actionBar.setTitle("WHUT");
        actionBar.setHomeAction(new HomeAction(R.drawable.ic_actionbar_whut));
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
				actionBar.setTitle("WHUT");
			}else{
				actionBar.setTitle("其他功能");
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

	
    
}
