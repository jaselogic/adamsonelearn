package com.jaselogic.adamsonelearn;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

public class LoginIntentService extends DownloadDocumentIntentService {
	public final static String EXTRA_NAME = "name";
	public final static String EXTRA_PASSWORD = "password";
	public final static String EXTRA_STATUS = "status";
	public final static String EXTRA_AVATAR = "avatarSrc";
	public final static String EXTRA_STUDNO = "studNo";
	public final static String EXTRA_COURSE = "course";
	public final static String EXTRA_YEAR = "year";
	public final static String EXTRA_COOKIE = "PHPSESSID";
	public final static String STATUS_UNSUCCESSFUL = "unsuccessful";
	public final static String STATUS_VALID = "valid";
	public final static String STATUS_INVALID = "invalid";
	public final static String NOTIFICATION = "com.jaselogic.adamsonelearn.LoginIntentService";
	
	public LoginIntentService() {
		super("LoginIntentService");
	}

	//preferences constants
	public final static String CONNECTION_STATUS = "status";
	public final static int CONNECTION_IDLE = 0;
	public final static int CONNECTION_CONNECTING = 1;
	public final static int CONNECTION_VALID = 2;
	public final static int CONNECTION_INVALID = 3;
	public final static int CONNECTION_UNSUCCESSFUL = 4;
	
	@Override
	protected void performAfterDownload(Document result, String cookie, Intent intent) {
		Intent broadcastIntent = new Intent(NOTIFICATION);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		Editor editor = prefs.edit();
		if(result != null) { //successful download
			
			//check if img.avatar exists to verify if user has logged in
			Elements avatar = result.select("img.avatar");
			if(avatar.size() > 0) { //if avatar exists
				//edit connection pref to valid
				editor.putInt(CONNECTION_STATUS, CONNECTION_VALID);
				//extract data
				String avatarSrc = avatar.get(0).attr("src");
				
				avatarSrc = "http://learn.adamson.edu.ph/" + avatarSrc.substring(3,
						(avatarSrc.indexOf('#') > 0 ? avatarSrc.indexOf('#') : avatarSrc.length()));
				
				Elements studinfo = result.select("div.studinfo");
				broadcastIntent.putExtra(EXTRA_STATUS, STATUS_VALID);
				/*
				broadcastIntent.putExtra(EXTRA_NAME, studinfo.get(0).text().trim());
				broadcastIntent.putExtra(EXTRA_AVATAR, avatarSrc);
				broadcastIntent.putExtra(EXTRA_COOKIE, cookie);
				broadcastIntent.putExtra(EXTRA_COURSE, studinfo.get(2).text().trim());
				broadcastIntent.putExtra(EXTRA_STUDNO, studinfo.get(1).text().trim());
				broadcastIntent.putExtra(EXTRA_YEAR, studinfo.get(3).text().trim());
				*/
				editor.putString(EXTRA_NAME, studinfo.get(0).text().trim());
				editor.putString(EXTRA_AVATAR, avatarSrc);
				editor.putString(EXTRA_COOKIE, cookie);
				editor.putString(EXTRA_COURSE, studinfo.get(2).text().trim());
				editor.putString(EXTRA_STUDNO, studinfo.get(1).text().trim());
				editor.putString(EXTRA_YEAR, studinfo.get(3).text().trim());
				
			} else { //invalid username or password
				//edit connection pref to invalid
				editor.putInt(CONNECTION_STATUS, CONNECTION_INVALID);
				broadcastIntent.putExtra(EXTRA_STATUS, STATUS_INVALID);
			}
		} else { // unsuccessful download
			//edit connection pref to unsuccessful
			editor.putInt(CONNECTION_STATUS, CONNECTION_UNSUCCESSFUL);
			broadcastIntent.putExtra(EXTRA_STATUS, STATUS_UNSUCCESSFUL);
		}
		editor.commit();
		sendBroadcast(broadcastIntent);
		
	}

	@Override
	protected String getCookie(Intent intent) {
		return null;
	}

	@Override
	protected String getPage() {
		return PAGE_BALINQ;
	}

	@Override
	protected String getStudno(Intent intent) {
		return intent.getStringExtra(EXTRA_STUDNO);
	}

	@Override
	protected String getPassword(Intent intent) {
		return intent.getStringExtra(EXTRA_PASSWORD);
	}

	@Override
	protected void performPriorDownload() {
		//commit to prefs connecting
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		Editor editor = prefs.edit();
		editor.putInt(CONNECTION_STATUS, CONNECTION_CONNECTING);
		editor.commit();
	}

}
