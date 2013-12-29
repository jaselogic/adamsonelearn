package com.jaselogic.adamsonelearn;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.select.Elements;

import com.jaselogic.adamsonelearn.DocumentManager.DocumentCookie;
import com.jaselogic.adamsonelearn.DrawerListAdapter.DrawerListItem;
import com.jaselogic.adamsonelearn.DrawerListAdapter.DrawerListItem.ItemType;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

class HomePageFragment {
	//Page fragment class
	public final static String SELECTOR_UPDATES = "tr";
	public final static String SELECTOR_SUBJECT = "div > div:nth-of-type(1) span";
	public final static String SELECTOR_TITLE = "div > div:nth-of-type(2) span";
	public final static String SELECTOR_BODY = "div > div:nth-of-type(3) span";
	public final static String SELECTOR_DATE = "div > div:nth-of-type(4)";
	public final static String SELECTOR_AVATAR = "img[alt=Avatar]";
	public final static String SELECTOR_TEACHER = "span.teachername";
	
	
	public static class UpdatesFragment extends ListFragment implements DocumentManager.ResponseReceiver {
		private String cookie;
		private DrawerListAdapter adapter;
		ArrayList<DrawerListItem> tester;
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View pageRootView = inflater.inflate(R.layout.fragment_updates, 
					container, false);
			
			DrawerListItem dummyItem = new DrawerListItem();
			dummyItem.label = "test";
			dummyItem.itemType = ItemType.NAME;
			
			tester = new ArrayList<DrawerListItem>();	
			adapter = new DrawerListAdapter(getActivity(), tester, ((Dashboard)getActivity()).studinfo.getString("avatarSrc"));
			setListAdapter(adapter);
			
			//get original cookie
			cookie = ((Dashboard)getActivity()).cookie;
			
			new DocumentManager.DownloadDocumentTask(UpdatesFragment.this, 
					DocumentManager.PAGE_UPDATES, cookie).execute("stdno", "pw");
			
			return pageRootView;
		}

		@Override
		public void onResourceReceived(DocumentCookie res) throws IOException {
			//Root node for updates page.
			Elements updates = res.document.select(SELECTOR_UPDATES);
			
			Elements teachers = updates.select(SELECTOR_TEACHER);
			
			for(int i = 0; i < teachers.size(); i++) {
				DrawerListItem dummyItem = new DrawerListItem();
				dummyItem.label = teachers.get(i).text();
				dummyItem.itemType = ItemType.NAME;
				tester.add(dummyItem);
			}
			
			adapter.notifyDataSetChanged();
			
			Log.d("JusSelector", updates.select(SELECTOR_DATE).text());
			Log.d("JusSelector", updates.select(SELECTOR_SUBJECT).text());
			Log.d("JusSelector", updates.select(SELECTOR_AVATAR).text());
			Log.d("JusSelector", updates.select(SELECTOR_TITLE).text());
			Log.d("JusSelector", updates.select(SELECTOR_BODY).text());
			Log.d("JusSelector", updates.select(SELECTOR_TEACHER).text());
			Log.d("JusSelector", updates.select(SELECTOR_AVATAR).attr("src"));
		}
	}
	
	public static class SubjectsFragment extends Fragment {
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			ViewGroup pageRootView = (ViewGroup) inflater.inflate(
					R.layout.fragment_subjects, container, false);
			return pageRootView;
		}		
	}
	
	public static class ScheduleFragment extends Fragment {
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			ViewGroup pageRootView = (ViewGroup) inflater.inflate(
					R.layout.fragment_schedule, container, false);
			return pageRootView;
		}		
	}
}
