package com.jaselogic.adamsonelearn;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.select.Elements;

import com.jaselogic.adamsonelearn.DocumentManager.DocumentCookie;
import com.jaselogic.adamsonelearn.DrawerListAdapter.DrawerListItem;
import com.jaselogic.adamsonelearn.DrawerListAdapter.DrawerListItem.ItemType;
import com.jaselogic.adamsonelearn.UpdatesListAdapter.UpdatesListItem;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;

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
		private UpdatesListAdapter adapter;
		ArrayList<UpdatesListItem> updateArrayList;
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View pageRootView = inflater.inflate(R.layout.fragment_updates, 
					container, false);
			
			DrawerListItem dummyItem = new DrawerListItem();
			dummyItem.label = "test";
			dummyItem.itemType = ItemType.NAME;
			
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
			Elements updates = res.document.select(SELECTOR_UPDATES);
			
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
