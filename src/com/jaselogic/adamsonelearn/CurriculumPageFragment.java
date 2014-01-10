package com.jaselogic.adamsonelearn;

import java.util.ArrayList;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.jaselogic.adamsonelearn.CurrDisplayAdapter.CurrDisplayListItem;
import com.jaselogic.adamsonelearn.CurrDisplayAdapter.ItemType;
import com.jaselogic.adamsonelearn.YearSelectAdapter.YearSelectListItem;

class CurriculumPageFragment {
	public static class YearSelectFragment extends ListFragment {
		public final static String SELECTOR_CURRICULUM_PAGE = "div.contentcontainer2 > table > tbody > tr > td:nth-of-type(2) > div:nth-of-type(3) div";
		public final static String SELECTOR_YEAR = "div[style*=background:#FF9]";
		public final static String[] YEAR_NAMES = {
			"First Year",
			"Second Year",
			"Third Year",
			"Fourth Year",
			"Fifth Year"
		};
		
		private NonSwipeViewPager parentViewPager;
		
		private String cookie;
		private YearSelectAdapter adapter;
		private ArrayList<YearSelectListItem> yearArrayList;
						
		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {
			Intent intent = new Intent("page-change-event");
			intent.putExtra("page", String.valueOf(position + 1));
			LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
			parentViewPager.setCurrentItem(1);
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View pageRootView = inflater.inflate(R.layout.fragment_listview, 
					container, false);
				
			yearArrayList = new ArrayList<YearSelectListItem>();	
			adapter = new YearSelectAdapter(getActivity(), yearArrayList);
			setListAdapter(adapter);
			
			//get original cookie
			cookie = ((Dashboard)getActivity()).cookie;

			Intent intent = new Intent(getActivity(), CurriculumIntentService.class);
			intent.putExtra(CurriculumIntentService.EXTRA_COOKIE, cookie);
			getActivity().startService(intent);
			
			//get parent viewpager
			parentViewPager = (NonSwipeViewPager) getActivity().findViewById(R.id.curriculum_pager);
			
			return pageRootView;
		}
		
		private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
				populateSelectList(prefs.getInt(CurriculumIntentService.EXTRA_YEARS, 4));
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
				.registerReceiver(mBroadcastReceiver, new IntentFilter(CurriculumIntentService.NOTIFICATION));
		}
		
		private void populateSelectList(int yr) {
			//initialize year select
			for(int i = 0; i < yr; i++) {
				YearSelectListItem yearItem = new YearSelectListItem();
				yearItem.year = YEAR_NAMES[i];
				yearItem.imageResId = R.drawable.ic_next;
				yearArrayList.add(yearItem);
			}
			
			adapter.notifyDataSetChanged();
		}
	}
	
	public static class CurrDisplayFragment extends ListFragment {
		
		private ArrayList<CurrDisplayListItem> currArrayList;
		private CurrDisplayAdapter adapter;
		
		public final static String[] SEMESTER_NAMES = {
			"No such semester",
			"First Semester",
			"Second Semester",
			"Summer"
		};
		
		@Override
		public void onResume() {
			super.onResume();
			
			LocalBroadcastManager.getInstance(getActivity())
				.registerReceiver(mMessageReceiver, new IntentFilter("page-change-event"));
			
		}
		
		private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String page = intent.getStringExtra("page");
				
				//clear current list
				currArrayList.clear();
				adapter.notifyDataSetChanged();
				
				//Assert database and tables have been created.
				SQLiteDatabase eLearnDb = getActivity().openOrCreateDatabase("AdUELearn", Context.MODE_PRIVATE, null);
								
				//perform query
				for(int semester = 1; semester <=3; semester++ ) {
					Cursor c = eLearnDb.rawQuery(
							"SELECT * FROM CurrTable WHERE Year = ? AND Semester = ?",
							new String[] {page, String.valueOf(semester)});
					
					CurrDisplayListItem tempItem;
					
					//add titler
					if(c.getCount() > 0) {
						tempItem = new CurrDisplayListItem();
						tempItem.mainText = SEMESTER_NAMES[semester].toUpperCase();
						tempItem.viewType = ItemType.ITEM_TITLE;
						currArrayList.add(tempItem);
					}
					
					while(c.moveToNext()) {
						tempItem = new CurrDisplayListItem();
						tempItem.mainText = c.getString(c.getColumnIndex("SubjName"));
						tempItem.unitsText = "Units: " + c.getString(c.getColumnIndex("Units"));
						
						String subjId = String.valueOf(c.getInt(c.getColumnIndex("Id")));
						//Check for prerequisites.
						if(c.getInt(c.getColumnIndex("HasPrereq")) == 1) {
							Cursor curPrereq = eLearnDb.rawQuery(
								"SELECT SubjCode, SubjName FROM PrereqTable " + 
							    "LEFT JOIN CurrTable ON Id=PrereqId WHERE " + 
								"SubjId = ?", new String[] {subjId});
							
							StringBuilder sb = new StringBuilder();
							while(curPrereq.moveToNext()) {
								sb.append(curPrereq.getString(curPrereq.getColumnIndex("SubjName")));
								if(!curPrereq.isLast()) {
									sb.append("\n");
								}
							}
							tempItem.prereqText = sb.toString();
						} else {
							tempItem.prereqText = "None";
						}
						
						//Check for corequisites.
						if(c.getInt(c.getColumnIndex("HasCoreq")) == 1) {
							Cursor curCoreq = eLearnDb.rawQuery(
								"SELECT SubjCode, SubjName FROM CoreqTable " + 
							    "LEFT JOIN CurrTable ON Id=CoreqId WHERE " + 
								"SubjId = ?", new String[] {subjId});
							
							StringBuilder sb = new StringBuilder();
							while(curCoreq.moveToNext()) {
								sb.append(curCoreq.getString(curCoreq.getColumnIndex("SubjName")));
								if(!curCoreq.isLast()) {
									sb.append("\n");
								}
							}
							tempItem.coreqText = sb.toString();
						} else {
							tempItem.coreqText = "None";
						}
						
						//Check for electives.
						if(c.getInt(c.getColumnIndex("HasElec")) == 1) {
							tempItem.viewType = ItemType.ITEM_ELECTIVE;
							Cursor curElec = eLearnDb.rawQuery(
								"SELECT SubjCode, SubjName FROM ElecTable " + 
							    "LEFT JOIN CurrTable ON Id=ElecId WHERE " + 
								"SubjId = ?", new String[] {subjId});
							
							StringBuilder sb = new StringBuilder();
							while(curElec.moveToNext()) {
								sb.append(curElec.getString(curElec.getColumnIndex("SubjName")));
								if(!curElec.isLast()) {
									sb.append("\n");
								}
							}
							tempItem.elecText = sb.toString();
						} else {
							tempItem.elecText = "None";
							tempItem.viewType = ItemType.ITEM_REGULAR;
						}
						currArrayList.add(tempItem);
					}
				}
				
				eLearnDb.close();

				adapter.notifyDataSetChanged();
			}
		};
		
		@Override
		public void onPause() {
			super.onPause();
			LocalBroadcastManager.getInstance(getActivity())
				.unregisterReceiver(mMessageReceiver);
		};
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View pageRootView = inflater.inflate(R.layout.fragment_listview, 
					container, false);
				
			currArrayList = new ArrayList<CurrDisplayListItem>();	
			adapter = new CurrDisplayAdapter(getActivity(), currArrayList);
			setListAdapter(adapter);
					
			return pageRootView;
		}
	}
}
