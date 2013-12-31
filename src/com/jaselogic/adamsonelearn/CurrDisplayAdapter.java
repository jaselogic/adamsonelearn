package com.jaselogic.adamsonelearn;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CurrDisplayAdapter extends BaseAdapter {
	private ArrayList<CurrDisplayListItem> mListItems;
	private LayoutInflater mLayoutInflater;
	
	public CurrDisplayAdapter(Context context, ArrayList<CurrDisplayListItem> arrayList) {
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
    	return 3;
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
        final CurrDisplayListItem listItem = mListItems.get(position);
        
        //check to see if the reused view is null or not, if is not null then reuse it
        if (view == null) {
            holder = new ViewHolder();
            switch(listItem.viewType) {
            	case ITEM_TITLE:
            		view = mLayoutInflater.inflate(R.layout.sem_title, viewGroup, false);
            		holder.mainText = (TextView) view.findViewById(R.id.sem_title_text);
            		break;
            	case ITEM_REGULAR:
            		view = mLayoutInflater.inflate(R.layout.curr_regular_item, viewGroup, false);
            		holder.mainText = (TextView) view.findViewById(R.id.curr_regular_maintext);
            		holder.unitsText = (TextView) view.findViewById(R.id.curr_regular_unittext);
            		holder.prereqText = (TextView) view.findViewById(R.id.curr_regular_prereqtext);
            		holder.coreqText = (TextView) view.findViewById(R.id.curr_regular_coreqtext);
            		break;
            	case ITEM_ELECTIVE:
            		view = mLayoutInflater.inflate(R.layout.curr_elective_item, viewGroup, false);
            		holder.mainText = (TextView) view.findViewById(R.id.curr_elective_maintext);
            		holder.unitsText = (TextView) view.findViewById(R.id.curr_elective_unitstext);
            		holder.prereqText = (TextView) view.findViewById(R.id.curr_elective_prereqtext);
            		holder.coreqText = (TextView) view.findViewById(R.id.curr_elective_coreqtext);
            		holder.elecText = (TextView) view.findViewById(R.id.curr_elective_electext);
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
        		switch(listItem.viewType) {
    			case ITEM_TITLE:
    				holder.mainText.setText(listItem.mainText);
    				break;
    			case ITEM_REGULAR:
    				holder.mainText.setText(listItem.mainText);
    				holder.unitsText.setText(listItem.unitsText);
    				holder.prereqText.setText(listItem.prereqText);
    				holder.coreqText.setText(listItem.coreqText);
    				break;
    			case ITEM_ELECTIVE:
    				holder.mainText.setText(listItem.mainText);
    				holder.unitsText.setText(listItem.unitsText);
    				holder.prereqText.setText(listItem.prereqText);
    				holder.coreqText.setText(listItem.coreqText);
    				holder.elecText.setText(listItem.elecText);
    				break;
        		}
        }
 
        //this method must return the view corresponding to the data at the specified position.
        return view;
 
    }
    
	//static class view holder to prevent repeated calls to findViewById
	private static class ViewHolder {
		protected TextView mainText;
		protected TextView unitsText;
		protected TextView prereqText;
		protected TextView coreqText;
		protected TextView elecText;
	}
	
	public static class CurrDisplayListItem {
		public String mainText;
		public String unitsText;
		public String prereqText;
		public String coreqText;
		public String elecText;
		public ItemType viewType;
	}
	
	public enum ItemType {
		ITEM_TITLE,
		ITEM_REGULAR,
		ITEM_ELECTIVE
	}
}
