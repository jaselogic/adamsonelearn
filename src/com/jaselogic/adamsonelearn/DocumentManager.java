package com.jaselogic.adamsonelearn;

import java.io.IOException;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

class DocumentManager {
	public static class DownloadDocumentTask extends AsyncTask<String, Void, Response> {

		ResponseReceiver mRec;
		String mCookie;
		
		public DownloadDocumentTask(ResponseReceiver rec, String cookie) {
			mRec = rec;
			mCookie = cookie;
		}
		
		@Override
		protected Response doInBackground(String... details) {
	        Response loginres = null;
	        Response result = null;
	        
	        try {
	        	if(mCookie == null) {
		            loginres = Jsoup.connect(Page.PAGE_LOGIN)
		            		.data("TXTusername", details[0], "TXTpassword", details[1], "BTNlogin", "Login")
		            		.method(Method.POST)
		            		.execute();
		            mCookie = loginres.cookie("PHPSESSID");
	        	}
	            
	        	//TODO: Handle if cookie has expired.
	        	
	            result = Jsoup.connect(Page.PAGE_BALINQ)
	            		.cookie("PHPSESSID", mCookie)
	            		.execute();
	            
	            
	        } catch (IOException e) {                          
	            e.printStackTrace();                           
	        }
	        
	        return result;
		}
		
		protected void onPostExecute(Response result) {
			mRec.onResourceReceived(result);
		}
	}
	
	public interface ResponseReceiver {
		void onResourceReceived(Response res);
	}
	
	public static class Page {
		public final static String PAGE_LOGIN = "http://learn.adamson.edu.ph/V4/";
		public final static String PAGE_BALINQ = "http://learn.adamson.edu.ph/V4/?page=balinq";
	}
}
