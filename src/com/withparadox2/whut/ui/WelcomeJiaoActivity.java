package com.withparadox2.whut.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.IntentAction;
import com.withparadox2.whut.R;
import com.withparadox2.whut.util.GlobalConstant;
import com.withparadox2.whut.util.Helper;

public class WelcomeJiaoActivity extends Activity {

	private Button timeTableButton;
	private Button geRenKeBiaoButton;
	private Button chengJiButton;
	private ActionBar actionBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);

		timeTableButton = (Button) findViewById(R.id.time_table_button);
		geRenKeBiaoButton = (Button) findViewById(R.id.gerenkebiao_button);
		chengJiButton = (Button) findViewById(R.id.chengji_button);
		actionBar = (ActionBar) findViewById(R.id.welcomen_actionbar);
        actionBar.setHomeAction(new IntentAction(this, MainActivity.createIntent(this), R.drawable.ic_actionbar_whut));
        actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("»¶Ó­Äú£º" + Helper.getValueInSharePreference(this, GlobalConstant.SP_LOCAL_TEMP, GlobalConstant.USER_NAME, "") + "Í¬Ñ§");

		timeTableButton.setOnClickListener(new TimeTableOnClickListener());
		geRenKeBiaoButton.setOnClickListener(new GeRenKeBiaoOnClickListener());
		chengJiButton.setOnClickListener(new ChengJiOnClickListener());
	}


	public static Intent createIntent(Context context) {
		Intent i = new Intent(context, WelcomeJiaoActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		return i;
	}

	public class TimeTableOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
            Intent i = new Intent();
            i.setClass(WelcomeJiaoActivity.this, KebiaoActivity.class);
            startActivity(i);
		}

	}

	public class GeRenKeBiaoOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
            Intent i = new Intent();
            i.setClass(WelcomeJiaoActivity.this, XueFenXueJiActivity.class);
            startActivity(i);		
		}

	}

	public class ChengJiOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent i = new Intent();
            i.setClass(WelcomeJiaoActivity.this, ChengJiActivity.class);
            startActivity(i);
		}

	}

}
