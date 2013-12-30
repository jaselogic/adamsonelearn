package com.jaselogic.adamsonelearn;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CurriculumFragment extends Fragment {
	//non swipe viewpager
	private static final int NUM_PAGES = 2;
	
	private NonSwipeViewPager mViewPager;
	private PagerAdapter mPagerAdapter;
	
	public CurriculumFragment() {
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//inflate the primary view
		View rootView = inflater.inflate(R.layout.fragment_curriculum,
				container, false);
		//create the pageradapter instance
		mViewPager = (NonSwipeViewPager) rootView
				.findViewById(R.id.curriculum_pager);
		mPagerAdapter = new CurriculumPagerAdapter(getFragmentManager());
		mViewPager.setAdapter(mPagerAdapter);
		
		return rootView;
	}
	
	private class CurriculumPagerAdapter extends FragmentStatePagerAdapter {
		public CurriculumPagerAdapter(FragmentManager fm) {
			super(fm);
		}
		
		@Override
		public int getCount() {
			return NUM_PAGES;
		}
		
		@Override
		public CharSequence getPageTitle(int position) {
			return "Curriculum Page";
		}
		
		@Override
		public Fragment getItem(int arg0) {
			if(position == 0) return CurriculumPageFragment.YearSelectFragment();
			
			return CurriculumPageFragment.YearPageFragment();
		}
	}
}
