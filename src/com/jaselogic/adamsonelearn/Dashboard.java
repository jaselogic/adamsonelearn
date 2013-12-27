package com.jaselogic.adamsonelearn;

import java.util.ArrayList;

import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class Dashboard extends ActionBarActivity {
	private DrawerLayout layoutDashboard;
	private ListView lvDrawer;
	
	private DrawerListAdapter drawerListAdapter;
	private ActionBarDrawerToggle drawerToggle;
	
	private CharSequence title;
	private CharSequence drawerTitle;
	
	private String[] testString = {"a", "b", "c"};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dashboard);
		
		//get title of application and store to title
		title = drawerTitle = getTitle();
		
		//Get reference to dashboard layout and drawer list view
		layoutDashboard = (DrawerLayout) findViewById(R.id.drawerlayout_dashboard);
		lvDrawer = (ListView) findViewById(R.id.drawerlistview_dashboard);
		
		ArrayList<String> list = new ArrayList<String>();
		ArrayList<DrawerListItem> drawerItemList = new ArrayList<DrawerListItem>();
		String[] itemsArray = getResources().getStringArray(R.array.drawer_items_array);
		TypedArray icons = getResources().obtainTypedArray(R.array.icons_array);

        for (int i = 0, j = 0; i < itemsArray.length; i++){
        	DrawerListItem item = new DrawerListItem();
        	item.imageResource = icons.getResourceId(j, 0);
        	if(itemsArray[i].charAt(0) != '*') {
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
		drawerListAdapter = new DrawerListAdapter(Dashboard.this, drawerItemList);
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
}
