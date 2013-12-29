package com.jaselogic.adamsonelearn;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;

import android.os.AsyncTask;

class DocumentManager {
	public final static String PAGE_LOGIN = "http://learn.adamson.edu.ph/V4/";
	public final static String PAGE_BALINQ = "http://learn.adamson.edu.ph/V4/?page=balinq";
	public final static String PAGE_UPDATES = "http://learn.adamson.edu.ph/V4/modules/newsfeed.php?sy=2013-2014&t=2";
	
	public static class DownloadDocumentTask extends AsyncTask<String, Void, DocumentCookie> {

		ResponseReceiver mRec;
		String mCookie;
		String mPage;
		
		public DownloadDocumentTask(ResponseReceiver rec, String page, String cookie) {
			mRec = rec;
			mCookie = cookie;
			mPage = page;
		}
		
		@Override
		protected DocumentCookie doInBackground(String... details) {
	        Response loginres = null;
	        DocumentCookie result = null;
	        
	        try {
	        	if(mCookie == null) {
		            loginres = Jsoup.connect(PAGE_LOGIN)
		            		.data("TXTusername", details[0], "TXTpassword", details[1], "BTNlogin", "Login")
		            		.method(Method.POST)
		            		.execute();
		            mCookie = loginres.cookie("PHPSESSID");
	        	}
	            
	        	//TODO: Handle if cookie has expired.
	        	result = new DocumentCookie();
	        	
	            result.document = Jsoup.connect(mPage)
	            		.cookie("PHPSESSID", mCookie)
	            		.get();
	            result.cookie = mCookie;
	            
	        } catch (IOException e) {                          
	            e.printStackTrace();                           
	        }
	        
	        return result;
		}
		
		protected void onPostExecute(DocumentCookie result) {
			try {
				mRec.onResourceReceived(result);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public interface ResponseReceiver {
		void onResourceReceived(DocumentCookie result) throws IOException;
	}
	
	public static class DocumentCookie {
		public Document document;
		public String cookie;
	}
}
