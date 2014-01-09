package com.jaselogic.adamsonelearn;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TodayListAdapter extends BaseAdapter {
	private ArrayList<TodayListItem> mListItems;
	private LayoutInflater mLayoutInflater;
	
	public TodayListAdapter(Context context, ArrayList<TodayListItem> arrayList) {
		mListItems = arrayList;
		mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
    @Override
    public int getItemViewType(int position) {
    	// TODO Auto-generated method stub
    	//return mListItems.get(position).viewType;
    	//return 0;
    	return mListItems.get(position).viewType.ordinal();
    }
    
    @Override
    public int getViewTypeCount() {
    	return 4;
    }
    
    
    @Override
    public int getCount() {
        //getCount() represents how many items are in the list
        return mListItems.size();
    }
 
    @Override
    //get the data of an item from a specific position
    //i represents the position of the item in the list
    public Object getItem(int i) {
        return null;
    }
 
    @Override
    //get the position id of the item from the list
    public long getItemId(int i) {
        return 0;
    }
    
    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
 
        // create a ViewHolder reference
        ViewHolder holder;
        //get the string item from the position "position" from array list to put it on the TextView
        final TodayListItem listItem = mListItems.get(position);
        
        //check to see if the reused view is null or not, if is not null then reuse it
        if (view == null) {
            holder = new ViewHolder();
            switch(listItem.viewType) {
            	case ITEM_TITLE:
            		view = mLayoutInflater.inflate(R.layout.today_item_title, viewGroup, false);
            		holder.mainText = (TextView) view.findViewById(R.id.today_title_text);
            		break;
            	case ITEM_NOW:
            		view = mLayoutInflater.inflate(R.layout.today_item_now, viewGroup, false);
            		holder.mainText = (TextView) view.findViewById(R.id.today_now_maintext);
            		holder.timeText = (TextView) view.findViewById(R.id.today_now_timetext);
            		holder.roomText = (TextView) view.findViewById(R.id.today_now_roomtext);
            		holder.reminderText = (TextView) view.findViewById(R.id.today_now_reminders);
            		break;
            	case ITEM_NEXT:
            		view = mLayoutInflater.inflate(R.layout.today_item_next, viewGroup, false);
            		holder.mainText = (TextView) view.findViewById(R.id.today_next_maintext);
            		holder.timeText = (TextView) view.findViewById(R.id.today_next_timetext);
            		holder.roomText = (TextView) view.findViewById(R.id.today_next_roomtext);
            		holder.reminderText = (TextView) view.findViewById(R.id.today_next_reminders);
            		break;
            	case ITEM_LATER:
            		view = mLayoutInflater.inflate(R.layout.today_item_later, viewGroup, false);
            		holder.mainText = (TextView) view.findViewById(R.id.today_later_maintext);
            		holder.timeText = (TextView) view.findViewById(R.id.today_later_timetext);
            		holder.roomText = (TextView) view.findViewById(R.id.today_later_roomtext);
            		holder.reminderText = (TextView) view.findViewById(R.id.today_later_reminders);
            		break;
            }
            
            // the setTag is used to store the data within this view
            view.setTag(holder);
        } else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = (ViewHolder)view.getTag();
        }
 
        if (listItem != null) {
        	//TODO: might want to check if each is null.
        		holder.mainText.setText(listItem.mainText);
        		switch(listItem.viewType) {
	    			case ITEM_TITLE:
	    				break;
	    			case ITEM_NOW:
	    			case ITEM_NEXT:
	    			case ITEM_LATER:
	    				holder.timeText.setText(listItem.timeText);
	    				holder.roomText.setText(listItem.roomText);
	    				holder.reminderText.setText(listItem.reminderText);
	    				break;
        		}
        }
 
        //this method must return the view corresponding to the data at the specified position.
        return view;
 
    }
    
	//static class view holder to prevent repeated calls to findViewById
	private static class ViewHolder {
		protected TextView mainText;
		protected TextView timeText;
		protected TextView roomText;
		protected TextView reminderText;
	}
	
	public static class TodayListItem {
		public String mainText;
		public String timeText;
		public String roomText;
		public String reminderText;
		public ItemType viewType;
	}
	
	public enum ItemType {
		ITEM_NOW,
		ITEM_NEXT,
		ITEM_LATER,
		ITEM_TITLE
	}
}
