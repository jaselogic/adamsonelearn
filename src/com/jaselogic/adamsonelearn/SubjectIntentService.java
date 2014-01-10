package com.jaselogic.adamsonelearn;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.jaselogic.adamsonelearn.SubjectListAdapter.SubjectListItem;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;

public class SubjectIntentService extends DownloadDocumentIntentService {
	public final static String CONNECTION_STATUS = "subject_status";
	public final static String EXTRA_COOKIE = "PHPSESSID";
	public final static int CONNECTION_CONNECTING = 0;
	public final static int CONNECTION_UNSUCCESSFUL = 1;
	public final static int CONNECTION_SUCCESSFUL = 2;
	
	private final static String SELECTOR_SUBJECTS_PAGE = "td";
	private final static String SELECTOR_AVATAR = "img[alt=Avatar]";
	private final static String SELECTOR_TEACHER = "div.teachername";
	private final static String SELECTOR_SUBJECTNAME = "div.lectitle";
	private final static String SELECTOR_SCHEDULE = "div.addeddate";
	
	public final static String NOTIFICATION = "com.jaselogic.adamsonelearn.SubjectIntentService";
	
	public SubjectIntentService() {
		super("SubjectIntentService");
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
		Elements updates = result.select(SELECTOR_SUBJECTS_PAGE);
		
		Elements teacher = updates.select(SELECTOR_TEACHER);
		Elements subject = updates.select(SELECTOR_SUBJECTNAME);
		Elements schedule = updates.select(SELECTOR_SCHEDULE);
		Elements avatarSrc = updates.select(SELECTOR_AVATAR);
		
		SQLiteDatabase eLearnDb = getApplicationContext().openOrCreateDatabase("AdUELearn", Context.MODE_PRIVATE, null);
		
		//drop subject table if it exists
		eLearnDb.execSQL("DROP TABLE IF EXISTS SubjTable");
		
		//create subj table

		/*
		 * DaySlots can be implemented as boolean type for each day
		 * (hasMonday, hasTuesday etc..) but since there is no boolean
		 * primitive type for SQLite and type affinity of boolean is an
		 * 8-bit integer, it would consume more space than having a single
		 * column for day slots.
		 * Days are represented as a 6-bit integer
		 * 0 bit when subject has no slot, otherwise 1
		 * Monday starts at the least significant bit, up to 6th bit which is saturday
		 * example:
		 * for MWF subjects: 010101b = 21
		 * for Sat subjects: 100000b = 32 
		 * for subjects with Tuesday: xxxx1x or (dayslot | (1 << 1));
		 * 
		 * TimeStart is the time slot start per day with 0 = 7:00am
		 * and increments for every 30mins. (e.g. 1 = 7:30, 2 = 8:00)
		 * 
		 * TimeEnd is the time slot end per day with 0 = 7:00am
		 * and increments for every 30mins.
		 */
		eLearnDb.execSQL("CREATE TABLE SubjTable " +
						"(SectionId INTEGER, SubjName TEXT, " +
						"ProfName TEXT, DaySlot INTEGER, " + 
						"TimeStart INTEGER, TimeEnd INTEGER, Room TEXT, " +
						"AvatarSrc TEXT);");
		
		//SQL Statement
		String sqlSubj = "INSERT INTO SubjTable VALUES (?,?,?,?,?,?,?,?);";
		SQLiteStatement stSubj = eLearnDb.compileStatement(sqlSubj);
		
		//create a transaction to minimize database insertion times
		eLearnDb.beginTransaction();
		for(int i = 0; i < subject.size(); i++) {
			SubjectListItem subjectItem = new SubjectListItem();
			subjectItem.teacher = teacher.get(i).text().trim();
			subjectItem.subject = subject.get(i).text().trim();
			subjectItem.schedule = schedule.get(i).text().trim();
			
			//get the index of ':' separator of subject and section id
			int sepIndex = subjectItem.subject.indexOf(':');
			//extract the section id from the subject
			int sectionId = Integer.parseInt(subjectItem.subject
					.substring(0, sepIndex - 1)
					);
			//extract the subject name
			String subjName = subjectItem.subject.substring(sepIndex + 2);
			
			//extract the day slots			
			//get the index of the first space
			int firstSpaceIndex = subjectItem.schedule.indexOf(' ');
			
			String daySlotString = subjectItem.schedule
					.substring(0, firstSpaceIndex);
			int daySlot = ScheduleHelper.convertStringToIntDaySlot(daySlotString);
			
			//extract timeslots
			String timeSlotString = subjectItem.schedule = subjectItem.schedule
					.substring(firstSpaceIndex).trim();
			//get next space index
			int nextSpaceIndex = timeSlotString.indexOf(' ');
			timeSlotString = timeSlotString.substring(0, nextSpaceIndex);
			
			//01:34-67:90
			//convert to integer timeslot
			String startString = timeSlotString.substring(0, 5);
			String endString = timeSlotString.substring(6, 11);
			int startSlot = ScheduleHelper.convertStringToIntSlot(startString);
			int endSlot = ScheduleHelper.convertStringToIntSlot(endString);

			//extract room
			String room = subjectItem.schedule.substring(nextSpaceIndex).trim();
											
			String src = avatarSrc.get(i).attr("src");
			subjectItem.avatarSrc = "http://learn.adamson.edu.ph/" + src.substring(3,
					(src.indexOf('#') > 0 ? src.indexOf('#') : src.length()));
			
			//add item to database.
			stSubj.clearBindings();
			stSubj.bindLong(1, sectionId);
			stSubj.bindString(2, subjName);
			stSubj.bindString(3, subjectItem.teacher);
			stSubj.bindLong(4, daySlot);
			stSubj.bindLong(5, startSlot);
			stSubj.bindLong(6, endSlot);
			stSubj.bindString(7, room);
			stSubj.bindString(8, subjectItem.avatarSrc);
			stSubj.execute();
			//subjectArrayList.add(subjectItem);
		}
		//set transaction successful, then end transaction
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
		return PAGE_SUBJECTS;
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
