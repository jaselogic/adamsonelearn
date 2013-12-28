package com.jaselogic.adamsonelearn;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

class HomeFragment extends Fragment {
	
	private static final int NUM_PAGES = 5;
	//pager view
	private ViewPager mPager;
	//pager adapter
	private PagerAdapter mPagerAdapter;
	
	public HomeFragment() {

	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_home, container, false);
		
		//create a new instance of ViewPager and PagerAdapter
		mPager = (ViewPager) rootView.findViewById(R.id.home_pager);
		mPagerAdapter = new HomePagerAdapter(getFragmentManager());
		mPager.setAdapter(mPagerAdapter);
		
		return rootView;
	}
	
	//Page fragment class
	public class HomePageFragment extends Fragment {
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			ViewGroup pageRootView = (ViewGroup) inflater.inflate(
					R.layout.layout_inner_home, container, false);
			return pageRootView;
		}
	}
	
	//Home Pager Adapter
	private class HomePagerAdapter extends FragmentStatePagerAdapter {
		public HomePagerAdapter(FragmentManager fm) {
			super(fm);
		}
		
		@Override
		public Fragment getItem(int arg0) {
			return new HomePageFragment();
		}
		
		@Override
		public int getCount() {
			return NUM_PAGES;
		}
	}
}
