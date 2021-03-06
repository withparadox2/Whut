package com.withparadox2.whut.ui;

import java.util.HashMap;
import java.util.Map;

import android.app.ExpandableListActivity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshExpandableListView;
import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.IntentAction;
import com.withparadox2.whut.R;
import com.withparadox2.whut.adapter.MyExpandableListAdapter;
import com.withparadox2.whut.dao.WhutGlobal;
import com.withparadox2.whut.http.HttpSearchThread;
import com.withparadox2.whut.util.Helper;

public class SearchBookActivity extends ExpandableListActivity {

	private HttpSearchThread myThread;
	private UpdaetUIHandler myHandler;
	private PullToRefreshExpandableListView expandableListView;
	private MyExpandableListAdapter myExpandableAdapter;
	private ActionBar actionbar;
	private Button searchBookButton;
	private EditText searchBookEdittext;

	public static int page = 1;
	private boolean loadingBooksFlag = false;
	private boolean childExpandedFlag = false;// if expanded then lastItem - 2,
	// else lastItem - 1
	private int lastChildExpandedPosition = -1;

	private int lastItem;

	public final static int GET_HTML = 1;
	public final static int UPDATE_GROUP = 2;
	public final static int NO_BOOKS = 3;
	public final static int UPDATE_CHILD = 4;

	public final static int UPDATE_GROUP_THREAD = 1;
	public static final int UPDATE_CHILD_THREAD = 2;

	private Map<Integer, Boolean> addToDatabaseFlag = new HashMap<Integer, Boolean>();

	private Button popupButton;
	private PopupWindow popupWindow;
	private LinearLayout layout;
	private ListView popupListView;
	private String searchMethod[];
	private float popupWidth;
	private InputMethodManager inputManager;

	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.book_expandable_hold);
		context = SearchBookActivity.this;

		expandableListView = (PullToRefreshExpandableListView) findViewById(R.id.expandable_listview);
		searchBookButton = (Button) findViewById(R.id.search_book_button);
		popupButton = (Button) findViewById(R.id.popup_button);
		searchBookEdittext = (EditText) findViewById(R.id.search_book_edittext);
		actionbar = (ActionBar) findViewById(R.id.search_book_actionbar);

		actionbar.setHomeAction(new IntentAction(this, MainActivity.createIntent(this), R.drawable.ic_actionbar_whut));
		actionbar.setDisplayHomeAsUpEnabled(true);
		actionbar.setTitle("图书搜索");

		searchMethod = getResources().getStringArray(R.array.search_method);
		inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		myExpandableAdapter = new MyExpandableListAdapter(this, addToDatabaseFlag);

		setListAdapter(myExpandableAdapter);
		this.getExpandableListView()
		.setOnGroupClickListener(new MyOnGroupClickListener());

		this.getExpandableListView().setGroupIndicator(null);
		myHandler = new UpdaetUIHandler(Looper.myLooper());

		expandableListView.setOnRefreshListener(new OnRefreshListener<ExpandableListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ExpandableListView> refreshView) {
				// Do work to refresh the list here.
				if(WhutGlobal.BOOKLIST.size()!=0){
					loadMoreBooks();
				}else{
					expandableListView.onRefreshComplete();
				}
			}
		});

		searchBookButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(Helper.isNetworkConnected(context)){
					searchBooks();
				}else{
					Helper.showShortToast(context, "无法连接网络...");
				}
			}
		});
		popupButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				int y = popupButton.getBottom() * 3 / 2;
				int x = popupButton.getWidth() / 2;

				showPopupWindow(x, y);
			}
		});
		searchBookEdittext
		.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					searchBooks();
					return true;
				}
				return false;
			}
		});
		Resources r = getResources();
		popupWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60,
				r.getDisplayMetrics());

		AnimationSet set = new AnimationSet(true);

		Animation animation = new AlphaAnimation(0.0f, 1.0f);
		animation.setDuration(400);
		set.addAnimation(animation);

		animation = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0.0f
				);
		animation.setDuration(400);
		set.addAnimation(animation);

		LayoutAnimationController controller =
				new LayoutAnimationController(set, 0.25f);
		this.getExpandableListView().setLayoutAnimation(controller);

	}

	private String getSearchMethod() {
		if (popupButton.getText().equals(searchMethod[0])) {
			return "title";
		} else if (popupButton.getText().equals(searchMethod[1])) {
			return "author";
		} else {
			return "subject";
		}
	}

	private void showPopupWindow(int x, int y) {
		layout = (LinearLayout) LayoutInflater.from(SearchBookActivity.this)
				.inflate(R.layout.search_method_pop, null);
		popupListView = (ListView) layout.findViewById(R.id.popup_listview);
		popupListView.setAdapter(new ArrayAdapter<String>(
				SearchBookActivity.this, R.layout.search_method_pop_item,
				R.id.search_popup_text, searchMethod));

		popupWindow = new PopupWindow(SearchBookActivity.this);
		popupWindow.setWidth((int) popupWidth);
		popupWindow.setHeight(LayoutParams.WRAP_CONTENT);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setFocusable(true);
		popupWindow.setContentView(layout);
		popupWindow.showAsDropDown(popupButton, -((int) popupWidth / 2 - x), 5);

		popupListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				popupButton.setText(searchMethod[arg2]);
				popupWindow.dismiss();
				popupWindow = null;
			}
		});
	}

	private void updateData() {
		myExpandableAdapter.notifyDataSetChanged();
		expandableListView.onRefreshComplete();
		loadingBooksFlag = false;
	}

	class UpdaetUIHandler extends Handler {
		public UpdaetUIHandler(Looper myLooper) {
			super(myLooper);
		}

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.arg1) {
			case NO_BOOKS:
				Toast.makeText(SearchBookActivity.this, "没有数据！",
						Toast.LENGTH_SHORT).show();
				expandableListView.onRefreshComplete();
				loadingBooksFlag = false;
				actionbar.setTitle("图书搜索");
				break;
			case UPDATE_GROUP:
				updateData();
				actionbar.setTitle("图书搜索");
				break;
			case UPDATE_CHILD:
				myExpandableAdapter.notifyDataSetChanged();
				break;

			}
		}

	}

	class MyOnGroupClickListener implements OnGroupClickListener {

		@Override
		public boolean onGroupClick(ExpandableListView parent, View v,
				int groupPosition, long id) {
			// TODO Auto-generated method stub

			if (!WhutGlobal.CLICK_GROUP_FLAG.get(groupPosition)) {
				WhutGlobal.BOOK_CODE = WhutGlobal.BOOKLIST.get(groupPosition)[1];
				WhutGlobal.BOOK_CODE_POS = groupPosition;
				WhutGlobal.WhichAction = UPDATE_CHILD_THREAD;
				myThread = new HttpSearchThread(myHandler, groupPosition, SearchBookActivity.this);
				myThread.start();
				WhutGlobal.CLICK_GROUP_FLAG.set(groupPosition, true);
			}
			return false;
		}

	}

	private void loadMoreBooks() {
		WhutGlobal.WhichAction = UPDATE_GROUP_THREAD;
		loadingBooksFlag = true;
		page = page + 1;
		myThread = new HttpSearchThread(myHandler, page);
		myThread.start();
	}

	private void searchBooks() {

		inputManager.hideSoftInputFromWindow(
				getCurrentFocus().getWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);

		addToDatabaseFlag.clear();
		WhutGlobal.BOOKLIST.clear();
		WhutGlobal.CHILDLIST.clear();
		WhutGlobal.CLICK_GROUP_FLAG.clear();
		WhutGlobal.SEARCH_TITLE = searchBookEdittext.getText().toString();
		WhutGlobal.SEARCH_METHOD = getSearchMethod();
		page = 1;
		expandableListView.scrollTo(0, 0);
		myExpandableAdapter.notifyDataSetChanged();
		childExpandedFlag = false;
		if (!WhutGlobal.SEARCH_TITLE.equals("")) {
			actionbar.setTitle("搜索中...");
			myThread = new HttpSearchThread(myHandler, page);
			WhutGlobal.WhichAction = UPDATE_GROUP_THREAD;
			myThread.start();
		} else {
			Toast.makeText(SearchBookActivity.this, "空空如也！", Toast.LENGTH_SHORT)
			.show();
		}
	}

}