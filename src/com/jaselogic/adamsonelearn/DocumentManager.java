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
		Map<String, String> mCookies;
		
		public DownloadDocumentTask(ResponseReceiver rec, Map<String, String> cookies) {
			mRec = rec;
			mCookies = cookies;
		}
		
		@Override
		protected Response doInBackground(String... details) {
	        Response loginres = null;
	        Response result = null;
	        
	        try {                                              
	            loginres = Jsoup.connect("http://learn.adamson.edu.ph/V4/")
	            		.data("TXTusername", details[0], "TXTpassword", details[1], "BTNlogin", "Login")
	            		.method(Method.POST)
	            		.execute();
	            Map<String, String> loginCookies = loginres.cookies();
	            
	            result = Jsoup.connect("http://learn.adamson.edu.ph/V4/?page=balinq")
	            		.cookies(loginCookies)
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
}
