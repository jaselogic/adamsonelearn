package com.jaselogic.adamsonelearn;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

class HomePageFragment {
	//Page fragment class
	public static class UpdatesFragment extends Fragment {
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			ViewGroup pageRootView = (ViewGroup) inflater.inflate(
					R.layout.fragment_updates, container, false);
			return pageRootView;
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
