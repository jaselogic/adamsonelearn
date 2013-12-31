package com.jaselogic.adamsonelearn;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class NonSwipeViewPager extends ViewPager {

	public NonSwipeViewPager(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public NonSwipeViewPager(Context context, AttributeSet attrs) {
		// TODO Auto-generated constructor stub
		super(context,attrs);
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		if(getCurrentItem() == 0)
			return false;
		return super.onInterceptTouchEvent(arg0);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		if(getCurrentItem() == 0)
			return false;
		return super.onTouchEvent(arg0);
	}
	
	
}
