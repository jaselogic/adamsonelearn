package com.jaselogic.adamsonelearn;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.jaselogic.adamsonelearn.DocumentManager.DocumentCookie;
import com.jaselogic.adamsonelearn.DrawerListItem.ItemType;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

class HomePageFragment {
	//Page fragment class
	public final static String SELECTOR_UPDATES = "div.divupdates td > div";
	public final static String SELECTOR_SUBJECT = ":root > div:nth-of-type(1) span";
	public final static String SELECTOR_TITLE = ":root > div:nth-of-type(2) span";
	public final static String SELECTOR_BODY = ":root > div:nth-of-type(3) span";
	public final static String SELECTOR_DATE = ":root > div:nth-of-type(4)";
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
			tester.add(dummyItem);
			
			adapter = new DrawerListAdapter(getActivity(), tester, ((Dashboard)getActivity()).studinfo.getString("name"));
			setListAdapter(adapter);
			
			tester.add(dummyItem);
			adapter.notifyDataSetChanged();
			
			//get original cookie
			cookie = ((Dashboard)getActivity()).cookie;
			
			new DocumentManager.DownloadDocumentTask(UpdatesFragment.this, 
					DocumentManager.PAGE_UPDATES, cookie).execute("stdno", "pw");
			
			return pageRootView;
		}

		@Override
		public void onResourceReceived(DocumentCookie res) throws IOException {
			// TODO Auto-generated method stub
			
			Elements updates = res.document.select(SELECTOR_UPDATES);
			Log.d("JusSelector", updates.select(SELECTOR_DATE).text());
			String teststr = updates.select(SELECTOR_DATE).text().substring(1, 10);
			DrawerListItem dummyItem = new DrawerListItem();
			dummyItem.label = teststr;
			dummyItem.itemType = ItemType.NAME;
			
			tester.add(dummyItem);
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
