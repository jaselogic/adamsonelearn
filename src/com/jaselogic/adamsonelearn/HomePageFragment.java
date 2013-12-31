package com.jaselogic.adamsonelearn;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.select.Elements;

import com.jaselogic.adamsonelearn.DocumentManager.DocumentCookie;
import com.jaselogic.adamsonelearn.DocumentManager.ResponseReceiver;
import com.jaselogic.adamsonelearn.DrawerListAdapter.DrawerListItem;
import com.jaselogic.adamsonelearn.DrawerListAdapter.DrawerListItem.ItemType;
import com.jaselogic.adamsonelearn.SubjectListAdapter.SubjectListItem;
import com.jaselogic.adamsonelearn.UpdatesListAdapter.UpdatesListItem;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;

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
			
			//TODO: remove strings stdno pw
			new DocumentManager.DownloadDocumentTask(SubjectsFragment.this, 
			  	DocumentManager.PAGE_SUBJECTS, cookie).execute("stdno", "pw");
			
			return pageRootView;
		}

		@Override
		public void onResourceReceived(DocumentCookie res)
				throws IOException {
			// TODO Auto-generated method stub
			//Root node for updates page.
			Elements updates = res.document.select(SELECTOR_SUBJECTS_PAGE);
			
			Elements teacher = updates.select(SELECTOR_TEACHER);
			Elements subject = updates.select(SELECTOR_SUBJECTNAME);
			Elements schedule = updates.select(SELECTOR_SCHEDULE);
			Elements avatarSrc = updates.select(SELECTOR_AVATAR);

			for(int i = 0; i < subject.size(); i++) {
				SubjectListItem subjectItem = new SubjectListItem();
				subjectItem.teacher = teacher.get(i).text();
				subjectItem.subject = subject.get(i).text();
				subjectItem.schedule = schedule.get(i).text();

				String src = avatarSrc.get(i).attr("src");
				subjectItem.avatarSrc = "http://learn.adamson.edu.ph/" + src.substring(3,
						(src.indexOf('#') > 0 ? src.indexOf('#') : src.length()));
						
				subjectArrayList.add(subjectItem);
			}
			
			adapter.notifyDataSetChanged();
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
