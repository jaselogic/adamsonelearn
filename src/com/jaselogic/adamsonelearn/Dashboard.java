package com.jaselogic.adamsonelearn;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class Dashboard extends ActionBarActivity {
	private DrawerLayout layoutDashboard;
	private ListView lvDrawer;
	private DrawerListAdapter drawerListAdapter;
	
	private String[] testString = {"a", "b", "c"};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dashboard);
		
		//Get reference to dashboard layout and drawer list view
		layoutDashboard = (DrawerLayout) findViewById(R.id.drawerlayout_dashboard);
		lvDrawer = (ListView) findViewById(R.id.drawerlistview_dashboard);
		
		ArrayList<String> list = new ArrayList<String>();

        //for simplicity we will add the same name for 20 times to populate the list view
        for (int i = 0; i < 20; i++){
            list.add("Diana" + i);
        }
		
		//Create new drawer list adapter
		drawerListAdapter = new DrawerListAdapter(Dashboard.this, list);
        /*for (int i = 1; i < 50; i++) {
            drawerListAdapter.addItem("item " + i);
            if (i % 4 == 0) {
                drawerListAdapter.addSeparatorItem("separator " + i);
            }
        }*/
        lvDrawer.setAdapter(drawerListAdapter);
	}
}
