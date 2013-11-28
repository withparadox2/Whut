package com.withparadox2.whut.adapter;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MainFragmentPagerAdapter extends FragmentPagerAdapter {

	private ArrayList<Fragment> fragmentList;

	public MainFragmentPagerAdapter(FragmentManager fm) {
	    super(fm);
	    // TODO Auto-generated constructor stub
    }
	
	public MainFragmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragmentList){
		super(fm);
		this.fragmentList = fragmentList;
	}

	@Override
    public Fragment getItem(int position) {
	    // TODO Auto-generated method stub
	    return fragmentList.get(position);
    }

	@Override
    public int getCount() {
	    // TODO Auto-generated method stub
	    return fragmentList.size();
    }

}
