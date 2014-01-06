package com.jaselogic.adamsonelearn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import org.jsoup.select.Elements;

import com.jaselogic.adamsonelearn.DocumentManager.DocumentCookie;
import com.jaselogic.adamsonelearn.DocumentManager.ResponseReceiver;
import com.jaselogic.adamsonelearn.DrawerListAdapter.DrawerListItem;
import com.jaselogic.adamsonelearn.DrawerListAdapter.DrawerListItem.ItemType;
import com.jaselogic.adamsonelearn.SubjectListAdapter.SubjectListItem;
import com.jaselogic.adamsonelearn.TodayListAdapter.TodayListItem;
import com.jaselogic.adamsonelearn.UpdatesListAdapter.UpdatesListItem;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.format.Time;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

class HomePageFragment {
	//Page fragment class
	public static class UpdatesFragment extends ListFragment implements ResponseReceiver {
		//TODO: change to private final static
		public final static String SELECTOR_UPDATES_PAGE = "tr";
		public final static String SELECTOR_SUBJECT = "div > div:nth-of-type(1) span";
		public final static String SELECTOR_TITLE = "div > div:nth-of-type(2) span";
		public final static String SELECTOR_BODY = "div > div:nth-of-type(3) span";
		public final static String SELECTOR_DATE = "div > div:nth-of-type(4)";
		public final static String SELECTOR_AVATAR = "img[alt=Avatar]";
		public final static String SELECTOR_TEACHER = "span.teachername";
		
		private String cookie;
		private UpdatesListAdapter adapter;
		private ArrayList<UpdatesListItem> updateArrayList;
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View pageRootView = inflater.inflate(R.layout.fragment_listview, 
					container, false);
				
			updateArrayList = new ArrayList<UpdatesListItem>();	
			adapter = new UpdatesListAdapter(getActivity(), updateArrayList);
			setListAdapter(adapter);
			
			//get original cookie
			cookie = ((Dashboard)getActivity()).cookie;
			
			//TODO: remove strings stdno pw
			new DocumentManager.DownloadDocumentTask(UpdatesFragment.this, 
				DocumentManager.PAGE_UPDATES, cookie).execute("stdno", "pw");
			
			return pageRootView;
		}

		@Override
		public void onResourceReceived(DocumentCookie res) throws IOException {
			//Root node for updates page.
			Elements updates = res.document.select(SELECTOR_UPDATES_PAGE);
			
			Elements teacher = updates.select(SELECTOR_TEACHER);
			Elements subject = updates.select(SELECTOR_SUBJECT);
			Elements title = updates.select(SELECTOR_TITLE);
			Elements body = updates.select(SELECTOR_BODY);
			Elements dateAdded = updates.select(SELECTOR_DATE);
			Elements avatarSrc = updates.select(SELECTOR_AVATAR);

			for(int i = 0; i < subject.size(); i++) {
				UpdatesListItem updateItem = new UpdatesListItem();
				updateItem.name = teacher.get(i).text();
				updateItem.subject = subject.get(i).text();
				updateItem.title = title.get(i).text();
				updateItem.body = body.get(i).text();
				updateItem.dateAdded = dateAdded.get(i).text();
				
				String src = avatarSrc.get(i).attr("src");
				updateItem.avatarSrc = "http://learn.adamson.edu.ph/" + src.substring(3,
						(src.indexOf('#') > 0 ? src.indexOf('#') : src.length()));
						
				updateArrayList.add(updateItem);
			}
			
			adapter.notifyDataSetChanged();
		}
	}
	
	public static class SubjectsFragment extends ListFragment implements ResponseReceiver {
		public static final String SELECTOR_SUBJECTS_PAGE = "td";
		public static final String SELECTOR_AVATAR = "img[alt=Avatar]";
		public static final String SELECTOR_TEACHER = "div.teachername";
		public static final String SELECTOR_SUBJECTNAME = "div.lectitle";
		public static final String SELECTOR_SCHEDULE = "div.addeddate";
		
		private String cookie;
		private SubjectListAdapter adapter;
		private ArrayList<SubjectListItem> subjectArrayList;
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View pageRootView = inflater.inflate(R.layout.fragment_listview, 
					container, false);
			subjectArrayList = new ArrayList<SubjectListItem>();	
			adapter = new SubjectListAdapter(getActivity(), subjectArrayList);
			setListAdapter(adapter);
			
			//get original cookie
			cookie = ((Dashboard)getActivity()).cookie;
			
			new DocumentManager.DownloadDocumentTask(SubjectsFragment.this, 
			  	DocumentManager.PAGE_SUBJECTS, cookie).execute();
			
			return pageRootView;
		}

		@Override
		public void onResourceReceived(DocumentCookie res)
				throws IOException {

			//open or create elearn database
			SQLiteDatabase eLearnDb = getActivity().openOrCreateDatabase("AdUELearn", Context.MODE_PRIVATE, null);
			
			//add subjects to database
			addSubjectsToDatabase(res, eLearnDb);
		
			//Broadcast subject-list-ready event
			broadcastListReady();
			
			//display subjects in subject list view
			displaySubjects(eLearnDb);

			//close database
			eLearnDb.close();
		}
		
		public void broadcastListReady() {
			Intent intent = new Intent("subject-list-ready");
			LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
		}
		
		public void addSubjectsToDatabase(DocumentCookie res, SQLiteDatabase eLearnDb) {
			//Root node for updates page.
			Elements updates = res.document.select(SELECTOR_SUBJECTS_PAGE);
			
			Elements teacher = updates.select(SELECTOR_TEACHER);
			Elements subject = updates.select(SELECTOR_SUBJECTNAME);
			Elements schedule = updates.select(SELECTOR_SCHEDULE);
			Elements avatarSrc = updates.select(SELECTOR_AVATAR);
			
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
		}
		
		public void displaySubjects(SQLiteDatabase eLearnDb) {
			//issue select
			Cursor c = eLearnDb.rawQuery("SELECT * FROM SubjTable", null);
			
			while(c.moveToNext()) {
				SubjectListItem subjectItem = new SubjectListItem();
				StringBuilder sbSubject = new StringBuilder(
						String.format("%05d", c.getInt(c.getColumnIndex("SectionId")))
						);
				sbSubject.append(" : ");
				sbSubject.append(c.getString(c.getColumnIndex("SubjName")));
				
				subjectItem.subject = sbSubject.toString();
				subjectItem.teacher = c.getString(c.getColumnIndex("ProfName"));
				
				StringBuilder sbSchedule = new StringBuilder(
						ScheduleHelper.convertIntToStringDaySlot(c.getInt(c.getColumnIndex("DaySlot")))
						);
				sbSchedule.append(" ");
				sbSchedule.append(
						ScheduleHelper.convertIntToStringSlot(
								c.getInt(c.getColumnIndex("TimeStart")),
								c.getInt(c.getColumnIndex("TimeEnd")) )
						);
				sbSchedule.append(" ");
				sbSchedule.append(c.getString(c.getColumnIndex("Room")));
				
				subjectItem.schedule = sbSchedule.toString();
				
				subjectItem.avatarSrc = c.getString(c.getColumnIndex("AvatarSrc"));
				subjectArrayList.add(subjectItem);
			}
			
			adapter.notifyDataSetChanged();
		}
	}
	
	public static class TodayFragment extends ListFragment {
		private TodayListAdapter adapter;
		private ArrayList<TodayListItem> todayArrayList;
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			ViewGroup pageRootView = (ViewGroup) inflater.inflate(
					R.layout.fragment_listview, container, false);
			todayArrayList = new ArrayList<TodayListItem>();
			adapter = new TodayListAdapter(getActivity(), todayArrayList);
			setListAdapter(adapter);
			return pageRootView;
		}
		
		private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) {
				//get current time
				Time timeNow = new Time();
				timeNow.setToNow();
				
				//open database
				SQLiteDatabase eLearnDb = getActivity().openOrCreateDatabase("AdUELearn", Context.MODE_PRIVATE, null);
				populateTodayListView(timeNow, eLearnDb);
				//close database
				eLearnDb.close();
			}
		};
		
		//listen to subject list ready event
		@Override
		public void onResume() {
			super.onResume();
			LocalBroadcastManager.getInstance(getActivity())
				.registerReceiver(mMessageReceiver, new IntentFilter("subject-list-ready"));
		}
		
		@Override
		public void onPause() {
			LocalBroadcastManager.getInstance(getActivity())
			.unregisterReceiver(mMessageReceiver);
			super.onPause();
		}
				
		public void populateTodayListView(Time timeNow, SQLiteDatabase eLearnDb) {
			Cursor c = eLearnDb.rawQuery(
					"SELECT * FROM SubjTable WHERE DaySlot & ? > 1 " +
					"ORDER BY TimeStart", 
					new String[] { String.valueOf(1 << (timeNow.weekDay - 1)) }
					);
			
			int timeSlotNow = ScheduleHelper.convertTimeToIntSlot(timeNow);
			short indicator = 0; // 1 = NOW, 2 = NEXT
			while(c.moveToNext()) {
				int curTimeStart = c.getInt(c.getColumnIndex("TimeStart"));
				int curTimeEnd = c.getInt(c.getColumnIndex("TimeEnd"));
				TodayListItem tempItem = new TodayListItem();
				TodayListItem tempTitle = new TodayListItem();
				tempTitle.viewType = TodayListAdapter.ItemType.ITEM_TITLE;
				if( timeSlotNow < curTimeStart && (indicator & 2) == 0 ) { //WALANG PANG NOW at NEXT
					tempItem.viewType = TodayListAdapter.ItemType.ITEM_NEXT;
					tempTitle.mainText = "NEXT:";
					todayArrayList.add(tempTitle);
					indicator |= 2;
				} else if ( timeSlotNow >= curTimeStart && timeSlotNow < curTimeEnd ) { //Now
					tempItem.viewType = TodayListAdapter.ItemType.ITEM_NOW;
					tempTitle.mainText = "NOW:";
					todayArrayList.add(tempTitle);
					indicator |= 1;
				} else if ( timeSlotNow < curTimeStart ) {
					tempTitle.mainText = "LATER:";
					tempItem.viewType = TodayListAdapter.ItemType.ITEM_LATER;
					if ( indicator < 4 )
						todayArrayList.add(tempTitle);
					indicator |= 4;
				}
				
				//if now bit is unset after first pass, set it
				if( (indicator & 1) == 0 && (indicator & 2) == 2 ) {
					tempItem.viewType = TodayListAdapter.ItemType.ITEM_NOW;
					indicator |= 1;
				}
				
				//add to list here.
				if(indicator > 0) {
					tempItem.mainText = c.getString(c.getColumnIndex("SubjName"));
					tempItem.timeText = ScheduleHelper
							.convertIntToStringSlot(curTimeStart, curTimeEnd);
					tempItem.roomText = c.getString(c.getColumnIndex("Room"));
					todayArrayList.add(tempItem);
				}
			}
			adapter.notifyDataSetChanged();
		}
		
	}
}
