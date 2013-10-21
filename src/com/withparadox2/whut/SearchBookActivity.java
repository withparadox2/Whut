package com.withparadox2.whut;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import com.withparadox2.whut.http.HttpOperation;

import android.app.Activity;
import android.os.Bundle;

public class SearchBookActivity extends Activity{
	private HttpOperation httpOperation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_book);
		httpOperation = new HttpOperation(this);
		try {
			httpOperation.getSearchBookResultHtml();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
