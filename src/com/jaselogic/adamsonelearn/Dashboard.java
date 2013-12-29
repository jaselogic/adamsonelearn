package com.jaselogic.adamsonelearn;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

public class Dashboard extends ActionBarActivity {
	private DrawerLayout layoutDashboard;
	private ListView lvDrawer;
	
	private DrawerListAdapter drawerListAdapter;
	private ActionBarDrawerToggle drawerToggle;
	
	private CharSequence title;
	private CharSequence drawerTitle;
	
	public String cookie;
	public Bundle studinfo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dashboard);

		//get extras
		studinfo = getIntent().getExtras();
		
		//store cookie
		cookie = studinfo.getString("PHPSESSID");
		Log.d("Jus", "DashboardCookie: " + cookie);
		
		//get title of application and store to title
		title = drawerTitle = getTitle();
		
		//Get reference to dashboard layout and drawer list view
		layoutDashboard = (DrawerLayout) findViewById(R.id.drawerlayout_dashboard);
		lvDrawer = (ListView) findViewById(R.id.drawerlistview_dashboard);
		
		ArrayList<DrawerListItem> drawerItemList = new ArrayList<DrawerListItem>();
		String[] itemsArray = getResources().getStringArray(R.array.drawer_items_array);
		TypedArray icons = getResources().obtainTypedArray(R.array.icons_array);

		//add student avatar on top of list
		DrawerListItem avatar = new DrawerListItem();
		avatar.itemType = DrawerListItem.ItemType.NAME;
		avatar.label = setWordCaps(studinfo.getString("name"));
		drawerItemList.add(avatar);
		
        for (int i = 0, j = 0; i < itemsArray.length; i++){
        	DrawerListItem item = new DrawerListItem();
        	item.imageResource = icons.getResourceId(j, 0);
        	if(!itemsArray[i].startsWith("*")) {
        		item.label = itemsArray[i];
        		item.itemType = DrawerListItem.ItemType.SIMPLE;
        		j++; 
        	} else {
        		item.label = itemsArray[i].substring(1).toUpperCase();
        		item.itemType = DrawerListItem.ItemType.SEPARATOR;
        	}
            drawerItemList.add(item);
        }
		
		//Create new drawer list adapter
		drawerListAdapter = new DrawerListAdapter(Dashboard.this, drawerItemList, studinfo.getString("avatarSrc"));
        /*for (int i = 1; i < 50; i++) {
            drawerListAdapter.addItem("item " + i);
            if (i % 4 == 0) {
                drawerListAdapter.addSeparatorItem("separator " + i);
            }
        }*/
        lvDrawer.setAdapter(drawerListAdapter);
        
        //enable action bar icon to toggle drawer display
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        
        drawerToggle = new ActionBarDrawerToggle(
        		Dashboard.this, 
        		layoutDashboard,
        		R.drawable.ic_drawer, 
        		R.string.drawer_open, 
        		R.string.drawer_close)  {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(title);
                supportInvalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(drawerTitle); //set ActionBar title to drawertitle
                supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        layoutDashboard.setDrawerListener(drawerToggle);
        
        //Display home page
        //TODO: Check this on save instance state
        displayPage(Page.HOME);
	}
	
	//called via supportInvalidateOptionsMenu
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		drawerToggle.syncState();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		drawerToggle.onConfigurationChanged(newConfig);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//Pass event to ActionBarDrawerToggle,
		//if true, it has handled app icon touchevent
		if (drawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
	
	//Word caps case for all caps name
	private String setWordCaps(String input) {
		StringBuffer buf = new StringBuffer();
	    Matcher m = Pattern.compile("([a-z])([a-z]*)",
		Pattern.CASE_INSENSITIVE).matcher(input);
	    while (m.find()) {
	    	m.appendReplacement(buf, 
			m.group(1).toUpperCase() + m.group(2).toLowerCase());
	    }
	    return m.appendTail(buf).toString();
	}
	
	//displays page fragment
	private void displayPage(Page p) {
		Fragment fragment = null;
		switch(p) {
			case HOME:
				fragment = new HomeFragment();
				break;
		}
		
		// Insert fragment to content frame
		FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment)
        .addToBackStack(null).commit();
        
        // Set item checked in drawer, then close drawer
        lvDrawer.setItemChecked(p.ordinal() + 1, true);
        layoutDashboard.closeDrawer(lvDrawer);
	}
	
	//pages enumeration
	private enum Page {
		HOME
	}
}
