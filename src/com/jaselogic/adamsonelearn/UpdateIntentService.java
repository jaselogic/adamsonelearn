package com.jaselogic.adamsonelearn;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.jaselogic.adamsonelearn.UpdatesListAdapter.UpdatesListItem;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class UpdateIntentService extends DownloadDocumentIntentService {
	public final static String CONNECTION_STATUS = "update_status";
	public final static String EXTRA_COOKIE = "PHPSESSID";
	public final static int CONNECTION_CONNECTING = 0;
	public final static int CONNECTION_UNSUCCESSFUL = 1;
	public final static int CONNECTION_SUCCESSFUL = 2;
	
	private final static String SELECTOR_UPDATES_PAGE = "tr";
	private final static String SELECTOR_SUBJECT = "div > div:nth-of-type(1) span";
	private final static String SELECTOR_TITLE = "div > div:nth-of-type(2) span";
	private final static String SELECTOR_BODY = "div > div:nth-of-type(3) span";
	private final static String SELECTOR_DATE = "div > div:nth-of-type(4)";
	private final static String SELECTOR_AVATAR = "img[alt=Avatar]";
	private final static String SELECTOR_TEACHER = "span.teachername";
	
	public final static String NOTIFICATION = "com.jaselogic.adamsonelearn.UpdateIntentService";
	
	public UpdateIntentService() {
		super("UpdateIntentService");
	}

	@Override
	protected void performPriorDownload() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		Editor editor = prefs.edit();
		editor.putInt(CONNECTION_STATUS, CONNECTION_CONNECTING);
		editor.commit();
	}

	@Override
	protected void performAfterDownload(Document result, String cookie,
			Intent intent) {
		DateFormat formatter = new SimpleDateFormat(
				"'Date Added : 'MMM dd, yyyy 'at' hh:mm:ss aa");
		
		//open database
		SQLiteDatabase eLearnDb = getApplicationContext().openOrCreateDatabase("AdUELearn", Context.MODE_PRIVATE, null);
		//drop table if it exists
		eLearnDb.execSQL("DROP TABLE IF EXISTS UpdatesTable;");
		
		//create the table
		eLearnDb.execSQL("CREATE TABLE UpdatesTable " +
				"(SectionId INTEGER, Title TEXT, " +
				"Body TEXT, DateAdded INTEGER);");
		
		//Root node for updates page.
		Elements updates = result.select(SELECTOR_UPDATES_PAGE);
		Elements teacher = updates.select(SELECTOR_TEACHER);
		Elements subject = updates.select(SELECTOR_SUBJECT);
		Elements title = updates.select(SELECTOR_TITLE);
		Elements body = updates.select(SELECTOR_BODY);
		Elements dateAdded = updates.select(SELECTOR_DATE);
		Elements avatarSrc = updates.select(SELECTOR_AVATAR);

		//SQL Statement
		String sqlUpdates = "INSERT INTO UpdatesTable VALUES (?,?,?,?);";
		SQLiteStatement stUpdates = eLearnDb.compileStatement(sqlUpdates);
		
		//begin SQL transaction
		eLearnDb.beginTransaction();
		for(int i = 0; i < subject.size(); i++) {
			UpdatesListItem updateItem = new UpdatesListItem();
			updateItem.name = teacher.get(i).text().trim();
			updateItem.subject = subject.get(i).text().trim();
			updateItem.title = title.get(i).text().trim();
			updateItem.body = body.get(i).text().trim();
			updateItem.dateAdded = dateAdded.get(i).text().trim();
			
			int subjCode = Integer.parseInt(
					updateItem.subject.substring(0, updateItem.subject.indexOf(' '))
					);
			
			Date date = null;
			
			try {
				date = (Date) formatter.parse(updateItem.dateAdded);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			Log.d("STAHP",
					date.toString()
					);
			String src = avatarSrc.get(i).attr("src");
			updateItem.avatarSrc = "http://learn.adamson.edu.ph/" + src.substring(3,
					(src.indexOf('#') > 0 ? src.indexOf('#') : src.length()));
			
			//add item to database
			stUpdates.clearBindings();
			stUpdates.bindLong(1, subjCode);
			stUpdates.bindString(2, updateItem.title);
			stUpdates.bindString(3, updateItem.body);
			stUpdates.bindLong(4, date.getTime());
			stUpdates.execute();
			
		}
		//set transaction success, then end transaction
		eLearnDb.setTransactionSuccessful();
		eLearnDb.endTransaction();
		
		eLearnDb.close();
		
		//set preference as successful
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		Editor editor = prefs.edit();
		editor.putInt(CONNECTION_STATUS, CONNECTION_SUCCESSFUL);
		editor.commit();
		
		//broadcast successful transaction
		Intent broadcastIntent = new Intent(NOTIFICATION);
		LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcastIntent);
	}

	@Override
	protected String getCookie(Intent intent) {
		return intent.getStringExtra(EXTRA_COOKIE);
	}

	@Override
	protected String getPage() {
		return PAGE_UPDATES;
	}

	@Override
	protected String getStudno(Intent intent) {
		return null;
	}

	@Override
	protected String getPassword(Intent intent) {
		return null;
	}

}
