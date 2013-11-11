package com.withparadox2.whut.library.search;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.IntentAction;
import com.withparadox2.whut.LiXianHomeActivity;
import com.withparadox2.whut.R;
import com.withparadox2.whut.dao.WhutGlobal;

public class SearchBookActivity extends Activity implements OnScrollListener{
	
	private HttpSearchThread myThread;
	private UpdaetUIHandler myHandler;
	private ListView listView;
	private MyListAdapter myAdapter;
	private ActionBar actionbar;
	private Button searchBookButton;
	private EditText searchBookEdittext;
	
	public static int page = 1;
	private boolean loadingBooksFlag = false;
	private boolean childExpandedFlag = false;//if expanded then lastItem - 2, else lastItem - 1
	private int lastChildExpandedPosition = -1;
	
	private View moreView;
	private int lastItem;

	public final static int GET_HTML = 1;
	public final static int UPDATE_GROUP = 2;
	public final static int NO_BOOKS = 3;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.book_list_hold);
		moreView = getLayoutInflater().inflate(R.layout.footer, null);
		listView = (ListView)findViewById(R.id.listview);
		searchBookButton = (Button)findViewById(R.id.search_book_button);
		actionbar = (ActionBar)findViewById(R.id.search_book_actionbar);
		actionbar.setHomeAction(new IntentAction(this, LiXianHomeActivity.createIntent(this), R.drawable.ic_actionbar_whut));
		actionbar.setDisplayHomeAsUpEnabled(true);
	    actionbar.setTitle("图书搜索");
		searchBookEdittext = (EditText)findViewById(R.id.search_book_edittext);
		myAdapter = new MyListAdapter();
		listView.addFooterView(moreView);
		listView.setAdapter(myAdapter);
		listView.setOnScrollListener(this);
		myHandler = new UpdaetUIHandler(Looper.myLooper());
		
		searchBookButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE); 

				inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                           InputMethodManager.HIDE_NOT_ALWAYS);

				WhutGlobal.BOOKLIST.clear();
				WhutGlobal.SEARCH_TITLE = searchBookEdittext.getText().toString();
				page = 1;
				listView.scrollTo(0, 0);
				myAdapter.notifyDataSetChanged();
				myThread = new HttpSearchThread(myHandler, page);
				myThread.start();
			}
		});
	}
	
	
	private void updateData(){
		myAdapter.notifyDataSetChanged();
		moreView.setVisibility(View.GONE);
		loadingBooksFlag = false;
	}
	
	
	class UpdaetUIHandler extends Handler{
		public UpdaetUIHandler(Looper myLooper){
			super(myLooper);
		}

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch(msg.arg1){
			case GET_HTML:
				Toast.makeText(SearchBookActivity.this, "下载数据", Toast.LENGTH_SHORT).show();
				break;
			case NO_BOOKS:
				Toast.makeText(SearchBookActivity.this, "未找到书籍！", Toast.LENGTH_SHORT).show();
				moreView.setVisibility(View.GONE);
				loadingBooksFlag = false;
				break;
			case UPDATE_GROUP:	
				updateData();
				break;
			}
		}
		
		
	}
	
	class MyListAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return WhutGlobal.BOOKLIST.size();
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
		public View getView(int groupPosition, View convertView, ViewGroup arg2) {
			// TODO Auto-generated method stub
            ViewHolder viewHolder = null;
            if(convertView == null){
                convertView = LayoutInflater.from(SearchBookActivity.this).inflate(R.layout.book_list_item, null);
		        viewHolder = new ViewHolder();
		        viewHolder.detail = (TextView)convertView.findViewById(R.id.book_detail);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }
	            viewHolder.detail.setText(WhutGlobal.BOOKLIST.get(groupPosition));
            return convertView;
		}
		
		 public class ViewHolder{
             TextView detail;
	     }
	}
	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		   lastItem = firstVisibleItem + visibleItemCount - 1; 
//		System.out.println(childExpandedFlag+"===="+firstVisibleItem+"===="+visibleItemCount+"===="+lastItem);
	}


	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		int size;
		if(childExpandedFlag){
			 size = WhutGlobal.BOOKLIST.size()+WhutGlobal.CHILDLIST.get(lastChildExpandedPosition).size();
		}else{
			size = WhutGlobal.BOOKLIST.size();
		}
		if(lastItem == size  && scrollState == this.SCROLL_STATE_IDLE){ 
            moreView.setVisibility(view.VISIBLE);
            if(!loadingBooksFlag){
	            loadMoreBooks();
        }
    }
		
	}
	
	private void loadMoreBooks(){
		loadingBooksFlag = true;
		page = page + 1;
		myThread = new HttpSearchThread(myHandler, page);
		myThread.start();
	}

}
