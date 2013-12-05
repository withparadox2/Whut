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

public class WelcomeTuActivity extends Activity {
	private Button tushuXujieButton;
	private ActionBar actionBar;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome_tu);

		tushuXujieButton = (Button) findViewById(R.id.tushu_xujie_button);
		actionBar = (ActionBar) findViewById(R.id.welcometu_actionbar);
		actionBar.setHomeAction(new IntentAction(this, MainActivity.createIntent(this), R.drawable.ic_actionbar_whut));
        actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("»¶Ó­Äú£º" + Helper.getValueInSharePreference(this, GlobalConstant.SP_LOCAL_TEMP, GlobalConstant.USER_NAME, "") + "Í¬Ñ§");
		tushuXujieButton.setOnClickListener(new XuJieOnClickListener());
	}


	public static Intent createIntent(Context context) {
		Intent i = new Intent(context, WelcomeTuActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		return i;
	}


	class XuJieOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
            Intent i = new Intent();
            i.setClass(WelcomeTuActivity.this, XuJieActivity.class);
            startActivity(i);
		}

	}
}
