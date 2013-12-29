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
	public static class DownloadDocumentTask extends AsyncTask<String, Void, Document> {

		Context mContext;
		Map<String, String> mCookies;
		
		public DownloadDocumentTask(Context context, Map<String, String> cookies) {
			mContext = context;
			mCookies = cookies;
		}
		
		@Override
		protected Document doInBackground(String... details) {
			// TODO Auto-generated method stub
	        Document doc = null;
	        Response res = null;
	        try {                                              
	            res = Jsoup.connect("http://learn.adamson.edu.ph/V4/")
	            		.data("TXTusername", details[0], "TXTpassword", details[1], "BTNlogin", "Login")
	            		.method(Method.POST)
	            		.execute();
	            Map<String, String> loginCookies = res.cookies();
	            
	            doc = Jsoup.connect("http://learn.adamson.edu.ph/V4/?page=balinq")
	            		.cookies(loginCookies)
	            		.get();
	        } catch (IOException e) {                          
	            e.printStackTrace();                           
	        }
	        
	        return doc;
		}
		
		protected void onPostExecute(Document result) {
			Intent intent = new Intent(mContext, Dashboard.class);
			//changed to dashboard class
			String avatarSrc = result.select("img.avatar").get(0).attr("src");
			Elements studinfo = result.select("div.studinfo");
			avatarSrc = "http://learn.adamson.edu.ph/" + avatarSrc.substring(3,
					(avatarSrc.indexOf('#') > 0 ? avatarSrc.indexOf('#') : avatarSrc.length()));
			
			intent.putExtra("avatarSrc", avatarSrc);
			intent.putExtra("name", studinfo.get(0).text());
			intent.putExtra("studNo", studinfo.get(1).text());
			intent.putExtra("course", studinfo.get(2).text());
			intent.putExtra("year", studinfo.get(3).text());
			mContext.startActivity(intent);
		}
		
	}
}
