package com.withparadox2.whut.ui;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.AbstractAction;
import com.markupartist.android.widget.ActionBar.IntentAction;
import com.withparadox2.whut.R;
import com.withparadox2.whut.R.color;
import com.withparadox2.whut.R.drawable;
import com.withparadox2.whut.R.id;
import com.withparadox2.whut.R.layout;
import com.withparadox2.whut.dao.SaveTwoDimArray;
import com.withparadox2.whut.dao.WhutGlobal;
import com.withparadox2.whut.http.HttpOperateThread;
import com.withparadox2.whut.http.HttpOperation;

public class XuJieActivity extends Activity {

	private ActionBar actionBar;
	private ListView myListView;
	LinearLayout mHead;
	private MyAdapter myAdapter;
	private String[][] result;
	private final String TAG = "XuJieActivity";
	private HttpOperation httpOperation;
	private ProgressDialog progressDialog;
	private HttpOperateThread myThread;
	private UpdateUIHandler myHandler;
	private boolean cancelDialogByHand = false;
	private boolean closeHttpFlag = true;// true then close httpPost, false
										 // close httpGet
	private List<NameValuePair> nameValuePairs;
	private boolean[] guoQiFlag; // 过期为true，没过期为false，然后在表中用颜色标记出来
	private int resultActualLength;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xujie);
		if (savedInstanceState != null) {
			SaveTwoDimArray save = (SaveTwoDimArray) savedInstanceState
			        .getSerializable(SaveTwoDimArray.NAME);
			WhutGlobal.htmlData = save.getCustomArray();
			WhutGlobal.USER_NAME = savedInstanceState.getString("USER_NAME");
			WhutGlobal.JSESSIONID = savedInstanceState.getString("JSESSIONID");
		}
		actionBar = (ActionBar) findViewById(R.id.xujie_actionbar);
		actionBar.setHomeAction(new IntentAction(this, WelcomeTuActivity
		        .createIntent(this), R.drawable.ic_actionbar_whut));
		actionBar.setDisplayHomeAsUpEnabled(true);

		actionBar.addAction(new XuJieAllAction());

		result = WhutGlobal.htmlData;
		resultActualLength = result.length - 1;
		actionBar.setTitle("共借：" + resultActualLength + "本|过期：" + getGuoQiNum()
		        + "本");
		mHead = (LinearLayout) findViewById(R.id.xujie_head);
		mHead.setFocusable(false);
		mHead.setClickable(false);
		mHead.setBackgroundColor(Color.parseColor("#FF5735"));

		myListView = (ListView) findViewById(R.id.xujie_listview);
		myAdapter = new MyAdapter(this, R.layout.xujie_item);
		myListView.setAdapter(myAdapter);
		myHandler = new UpdateUIHandler(Looper.myLooper());
		httpOperation = new HttpOperation(this);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		SaveTwoDimArray mySave = SaveTwoDimArray.getSingletonObject();
		mySave.setCustomArray(result);
		outState.putSerializable(SaveTwoDimArray.NAME, mySave);
		outState.putString("USER_NAME", WhutGlobal.USER_NAME);
		outState.putString("JSESSIONID", WhutGlobal.JSESSIONID);
	}

	public class XuJieAllAction extends AbstractAction {

		public XuJieAllAction() {
			super(R.drawable.ic_actionbar_byonehand);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void performAction(View view) {
			// TODO Auto-generated method stub
			boolean allXuJie = false;
			for (int i = 0; i < resultActualLength; i++) {
				if (result[i + 1][8].equals("0")) {
					allXuJie = true;
				}
			}
			if (allXuJie) {
				setAllNVPairs();
				WhutGlobal.WhichAction = 12;
				cancelDialogByHand = false;
				progressDialog = new ProgressDialog(XuJieActivity.this);
				progressDialog.setOnCancelListener(new DialogCancelListener());
				myThread = new HttpOperateThread(XuJieActivity.this, myHandler,
				        httpOperation);
				myThread.setPostParas(nameValuePairs);
				myThread.start();
			} else {
				Toast.makeText(XuJieActivity.this, "没有可以续借的书...",
				        Toast.LENGTH_LONG).show();
			}
		}

	}

	public class MyAdapter extends BaseAdapter {
		int id_row_layout;
		LayoutInflater mInflater;

		public MyAdapter(Context context, int id_row_layout) {
			super();
			this.id_row_layout = id_row_layout;
			mInflater = LayoutInflater.from(context);

		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return resultActualLength;
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parentView) {
			ViewHolder holder = null;
			if (convertView == null) {
				synchronized (XuJieActivity.this) {
					convertView = mInflater.inflate(id_row_layout, parentView,
					        false);
					holder = new ViewHolder();

					holder.txt1 = (TextView) convertView
					        .findViewById(R.id.xujie_textView1);
					holder.txt2 = (TextView) convertView
					        .findViewById(R.id.xujie_textView2);
					holder.txt3 = (TextView) convertView
					        .findViewById(R.id.xujie_textView3);
					holder.button = (Button) convertView
					        .findViewById(R.id.xujie_button);
					convertView.setTag(holder);
				}
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.txt1.setText(result[position + 1][1]);
			if (guoQiFlag[position]) {
				holder.txt1.setBackgroundResource(R.color.holo_blue_darker);
			} else {
				holder.txt1.setBackgroundColor(Color.parseColor("#FF5735"));
			}

			holder.txt2.setText("借出:\n" + result[position + 1][6] + "\n"
			        + "应还:\n" + result[position + 1][7]);
			holder.txt3.setText(result[position + 1][8]);
			holder.button.setOnClickListener(new xuJieSingleClickListener(
			        position + 1));
			return convertView;
		}

		class ViewHolder {
			TextView txt1;
			TextView txt2;
			TextView txt3;
			Button button;
		}

		class xuJieSingleClickListener implements OnClickListener {
			private int position;

			public xuJieSingleClickListener(int position) {
				// TODO Auto-generated constructor stub
				this.position = position;
			}

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.i(TAG, "点击了啊");
				if (result[position][8].equals("1")) {
					Toast.makeText(XuJieActivity.this, "已经续借过了...",
					        Toast.LENGTH_LONG).show();
				} else {
					setNVPairs(position);
					xuJieSinglePost();
				}

			}

		}

	}

	private void xuJieSinglePost() {
		WhutGlobal.WhichAction = 12;
		cancelDialogByHand = false;
		progressDialog = new ProgressDialog(XuJieActivity.this);
		progressDialog.setOnCancelListener(new DialogCancelListener());
		myThread = new HttpOperateThread(XuJieActivity.this, myHandler,
		        httpOperation);
		myThread.setPostParas(nameValuePairs);
		myThread.start();
	}

	private void setNVPairs(int position) {
		nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("furl",
		        "/opac/loan/renewList"));
		nameValuePairs.add(new BasicNameValuePair("barcodeList",
		        result[position][0]));
	}

	private void setAllNVPairs() {
		nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("furl",
		        "/opac/loan/renewList"));
		for (int i = 0; i < resultActualLength; i++) {
			nameValuePairs.add(new BasicNameValuePair("barcodeList",
			        result[i + 1][0]));
		}
	}

	private class DialogCancelListener implements OnCancelListener {

		@Override
		public void onCancel(DialogInterface dialog) {
			// TODO Auto-generated method stub
			cancelDialogByHand = true;
			if (closeHttpFlag) {
				WhutGlobal.CANCEL_DOWNLOAD_FLAG = true;
				WhutGlobal.JUMP_OR_NOT = false;
				httpOperation.closeHttpPost();
			} else {
				httpOperation.closeHttpGet();
			}
		}
	}

	public class UpdateUIHandler extends Handler {
		public UpdateUIHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.arg1) {
			case 1:
				progressDialog.setMessage("正在提交数据...");
				progressDialog.show();
				closeHttpFlag = true;
				break;
			case 2:
				progressDialog.setMessage("正在处理结果...");
				break;
			case 3:
				if (WhutGlobal.XUJIE_SUCCESS_FLAG) {
					progressDialog.setMessage("续借成功\n正在下载新数据...");
				} else {
					progressDialog.setMessage("续借失败\n正在下载新数据...");
				}
				closeHttpFlag = false;
				break;

			case 4:
				if (WhutGlobal.XUJIE_SUCCESS_FLAG) {
					progressDialog.setMessage("续借成功\n正在处理新数据...");
				} else {
					progressDialog.setMessage("续借失败\n正在处理新数据...");
				}
				break;
			case 5:
				progressDialog.dismiss();
				if (WhutGlobal.JUMP_OR_NOT && (!cancelDialogByHand)) {
					initialData();
				}
				break;
			case 100:
				if (!cancelDialogByHand) {
					Toast.makeText(XuJieActivity.this, "不好意思，数据有问题...",
					        Toast.LENGTH_LONG).show();
				}
				break;
			}
		}
	}

	private int getGuoQiNum() {
		guoQiFlag = new boolean[resultActualLength];
		String s = todayDate();
		Log.i(TAG, "时间" + s);
		int num = 0;
		for (int i = 0; i < guoQiFlag.length; i++) {
			if (guoQiDetect(s, result[i + 1][7])) {
				guoQiFlag[i] = true;
				num++;
			} else {
				guoQiFlag[i] = false;
			}
		}
		return num;
	}

	private void initialData() {
		result = WhutGlobal.htmlData;
		resultActualLength = result.length - 1;
		actionBar.setTitle("共借：" + resultActualLength + "本|过期：" + getGuoQiNum()
		        + "本");
		myAdapter.notifyDataSetChanged();
	}

	private boolean guoQiDetect(String dateString1, String dateString2) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		boolean result = false;
		try {
			Date date1 = df.parse(dateString1);
			Date date2 = df.parse(dateString2);
			if (date1.getTime() > date2.getTime()) {
				result = true;
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	private String todayDate() {
		Calendar c = Calendar.getInstance();
		int mDay = c.get(Calendar.DAY_OF_MONTH);
		int mMonth = c.get(Calendar.MONTH);
		int mYear = c.get(Calendar.YEAR);
		return new StringBuilder().append(mYear).append("-")
		        .append(formatDate(mMonth + 1)).append("-")
		        .append(formatDate(mDay)).toString();
	}

	private String formatDate(int x) {
		String s = "" + x;
		if (s.length() == 1)
			s = "0" + s;
		return s;
	}
}
