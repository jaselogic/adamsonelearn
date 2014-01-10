package com.jaselogic.adamsonelearn;

import java.io.IOException;
import java.net.SocketTimeoutException;

import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;
import org.jsoup.nodes.Document;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public abstract class DownloadDocumentIntentService extends IntentService {
	protected final static String PAGE_LOGIN = "http://learn.adamson.edu.ph/V4/";
	protected final static String PAGE_BALINQ = "http://learn.adamson.edu.ph/V4/?page=balinq";
	protected final static String PAGE_UPDATES = "http://learn.adamson.edu.ph/V4/modules/newsfeed.php?sy=2013-2014&t=2";
	protected final static String PAGE_SUBJECTS = "http://learn.adamson.edu.ph/V4/modules/mysubjects.php?cat=0";
	protected final static String PAGE_CURRICULUM = "http://learn.adamson.edu.ph/V4/?page=curr";
	
	public DownloadDocumentIntentService(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		String cookie = getCookie(intent);
		
		Document result = null;
		Response loginres = null;
		
		
		
        try {
        	if(cookie == null) {
	            loginres = Jsoup.connect(PAGE_LOGIN)
	            		.data("TXTusername", getStudno(intent), "TXTpassword", getPassword(intent), "BTNlogin", "Login")
	            		.method(Method.POST)
	            		.execute();
	            cookie = loginres.cookie("PHPSESSID");
        	}
            
        	//TODO: Handle if cookie has expired.       	
            result = Jsoup.connect(getPage())
            		.cookie("PHPSESSID", cookie)
            		.get();            
        } catch (IOException e) {                          
            e.printStackTrace();                           
        }
        
       	performAfterDownload(result, cookie, intent);       

	}

	protected abstract void performAfterDownload(Document result, String cookie, Intent intent);
	protected abstract String getCookie(Intent intent);
	protected abstract String getPage();
	protected abstract String getStudno(Intent intent);
	protected abstract String getPassword(Intent intent);
}
