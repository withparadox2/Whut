package com.withparadox2.whut.http;

import java.util.ArrayList;

import android.os.AsyncTask;

public class FetchChengjiTask extends AsyncTask<Void, Void, ArrayList<String[]>>{

	private Callback callback;
    public interface Callback{
        public void onPostExecute(ArrayList<String[]> result);
    	
    }
    
    public FetchChengjiTask(Callback callback){
        this.callback = callback;
    }
    
	@Override
    protected ArrayList<String[]> doInBackground(Void... params) {
	    // TODO Auto-generated method stub
	    return null;
    }
    
	@Override
    protected void onPostExecute(ArrayList<String[]> result) {
	    // TODO Auto-generated method stub
	    callback.onPostExecute(result);
    }
    
    

}
