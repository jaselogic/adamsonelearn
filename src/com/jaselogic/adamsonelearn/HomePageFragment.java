package com.jaselogic.adamsonelearn;

import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import com.jaselogic.adamsonelearn.SubjectListAdapter.SubjectListItem;
import com.jaselogic.adamsonelearn.TodayListAdapter.TodayListItem;
import com.jaselogic.adamsonelearn.UpdatesListAdapter.UpdatesListItem;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.format.Time;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

class HomePageFragment {
	//Page fragment class
	public static class UpdatesFragment extends ListFragment {	
		//private String cookie;
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
			//cookie = ((Dashboard)getActivity()).cookie;
			
			return pageRootView;
		}
				
		private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				populateUpdatesList();
				broadcastUpdatesReady();
			}
		};

		@Override
		public void onPause() {
			super.onPause();
			LocalBroadcastManager.getInstance(getActivity())
				.unregisterReceiver(mBroadcastReceiver);
		}
		
		@Override
		public void onResume() {
			super.onResume();
			LocalBroadcastManager.getInstance(getActivity())
				.registerReceiver(mBroadcastReceiver, new IntentFilter(SubjectIntentService.NOTIFICATION));
		}
		
		public void populateUpdatesList() {
			DateFormat formatter = new SimpleDateFormat(
					"'Date Added : 'MMM dd, yyyy 'at' hh:mm:ss aa");
			
			//open database
			SQLiteDatabase eLearnDb = getActivity().openOrCreateDatabase("AdUELearn", Context.MODE_PRIVATE, null);
			
			Cursor c = eLearnDb.rawQuery(
					"SELECT SubjTable.ProfName, UpdatesTable.SectionId, " +
					"SubjTable.SubjName, UpdatesTable.Title, " +
					"UpdatesTable.Body, UpdatesTable.DateAdded, " +
					"SubjTable.AvatarSrc " +
					"FROM UpdatesTable LEFT JOIN SubjTable ON " +
					"UpdatesTable.SectionId=SubjTable.SectionId", null);
			
			while(c.moveToNext()) {
				UpdatesListItem tempItem = new UpdatesListItem();
				tempItem.name = c.getString(c.getColumnIndex("ProfName"));
				StringBuilder sbSubject = new StringBuilder(
						c.getString(c.getColumnIndex("SectionId"))
						);
				sbSubject.append(" : ");
				sbSubject.append(c.getString(c.getColumnIndex("SubjName")));
				tempItem.subject = sbSubject.toString();
				tempItem.title = c.getString(c.getColumnIndex("Title"));
				tempItem.body = c.getString(c.getColumnIndex("Body"));
				tempItem.dateAdded = formatter.format(new Date(c.getLong(c.getColumnIndex("DateAdded"))));
				tempItem.avatarSrc = c.getString(c.getColumnIndex("AvatarSrc"));
				
				updateArrayList.add(tempItem);
			}
			
			adapter.notifyDataSetChanged();
			
			//close database
			eLearnDb.close();
		}
		
		public void broadcastUpdatesReady() {
			Intent intent = new Intent("updates-list-ready");
			LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
		}
	}
	
	public static class SubjectsFragment extends ListFragment {	
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
			
			Intent intent = new Intent(getActivity(), SubjectIntentService.class);
			intent.putExtra(SubjectIntentService.EXTRA_COOKIE, cookie);
			getActivity().startService(intent);
			
			return pageRootView;
		}
		
		private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				SQLiteDatabase eLearnDb = getActivity().openOrCreateDatabase("AdUELearn", Context.MODE_PRIVATE, null);
				displaySubjects(eLearnDb);
				eLearnDb.close();
				
				//start updates intent
				Intent updatesIntent = new Intent(getActivity(), UpdateIntentService.class);
				updatesIntent.putExtra(SubjectIntentService.EXTRA_COOKIE, cookie);
				getActivity().startService(updatesIntent);
			}
		};

		@Override
		public void onPause() {
			super.onPause();
			LocalBroadcastManager.getInstance(getActivity())
				.unregisterReceiver(mBroadcastReceiver);
		}
		
		@Override
		public void onResume() {
			super.onResume();
			LocalBroadcastManager.getInstance(getActivity())
				.registerReceiver(mBroadcastReceiver, new IntentFilter(SubjectIntentService.NOTIFICATION));
		}
			
		public void broadcastListReady() {
			Intent intent = new Intent("subject-list-ready");
			LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
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
				.registerReceiver(mMessageReceiver, new IntentFilter("updates-list-ready"));
		}
		
		@Override
		public void onPause() {
			LocalBroadcastManager.getInstance(getActivity())
			.unregisterReceiver(mMessageReceiver);
			super.onPause();
		}
/*SUBJECT
		eLearnDb.execSQL("CREATE TABLE SubjTable " +
				"(SectionId INTEGER, SubjName TEXT, " +
				"ProfName TEXT, DaySlot INTEGER, " + 
				"TimeStart INTEGER, TimeEnd INTEGER, Room TEXT, " +
				"AvatarSrc TEXT);");
	*/	
		
/*UPDATES
 * 			eLearnDb.execSQL("CREATE TABLE UpdatesTable " +
					"(SectionId INTEGER, Title TEXT, " +
					"Body TEXT, DateAdded INTEGER);");
 */
		
/*
 * 			"SELECT s.SubjName, s.TimeStart, " +
			"s.TimeEnd, s.Room, " +
			"u.Title, u.Body " +
			"FROM SubjTable s LEFT OUTER JOIN UpdatesTable u " +
			"ON s.SectionId = u.SectionId " +
			"INNER JOIN (SELECT SectionId, MAX(DateAdded) AS maxDate FROM UpdatesTable GROUP BY SectionId) uniqTable " +
			"ON uniqTable.SectionId = u.SectionId AND uniqTable.maxDate = u.DateAdded " +
			"WHERE s.DaySlot & ? > 1 " +
			"ORDER BY s.TimeStart", 
 */
		public void populateTodayListView(Time timeNow, SQLiteDatabase eLearnDb) {
			Cursor c = eLearnDb.rawQuery(
					"SELECT s.SubjName, s.TimeStart, " +
					"s.TimeEnd, s.Room, " +
					"v.Title, v.Body " +
					"FROM SubjTable s LEFT OUTER JOIN " +
					"(SELECT * FROM UpdatesTable u " +
					"INNER JOIN (SELECT SectionId, MAX(DateAdded) AS maxDate FROM UpdatesTable GROUP BY SectionId) uniqTable " +
					"ON uniqTable.SectionId = u.SectionId AND uniqTable.maxDate = u.DateAdded) v " +
					"ON s.SectionId = v.SectionId " +
					"WHERE s.DaySlot & ? > 1 " +
					"ORDER BY s.TimeStart", 
					new String[] { String.valueOf(1 << (timeNow.weekDay - 1)) }
					);
			
			int timeSlotNow = ScheduleHelper.convertTimeToIntSlot(timeNow);
			short indicator = 0; // 1 = NOW, 2 = NEXT
			
			Log.d("CURRI", String.valueOf(c.getCount()));
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
					//build reminder string
					
					String bodyString = c.getString(c.getColumnIndex("Body"));
					String titleString = c.getString(c.getColumnIndex("Title"));
					if(bodyString != null) {
						StringBuilder sbRem = new StringBuilder("<b>");
						sbRem.append(titleString);
						sbRem.append("</b>.&nbsp;&nbsp;");
						sbRem.append(bodyString);
						tempItem.reminderText = sbRem.toString();
					} else {
						tempItem.reminderText = "None";
					}
					todayArrayList.add(tempItem);
				}
			}
			adapter.notifyDataSetChanged();
		}
		
	}
}
