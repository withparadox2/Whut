package com.withparadox2.whut.ui;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.IntentAction;
import com.withparadox2.whut.R;


import android.app.Activity;
import android.os.Bundle;

public class InformationActivity extends Activity {
	private ActionBar actionBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.information);
		actionBar = (ActionBar) findViewById(R.id.information_actionbar);
		actionBar.setHomeAction(new IntentAction(this, MainActivity
		        .createIntent(this), R.drawable.ic_actionbar_whut));
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("WHUT beta");
	}

}