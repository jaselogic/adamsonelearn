package com.jaselogic.adamsonelearn;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class YearSelectAdapter extends BaseAdapter {
	private ArrayList<YearSelectListItem> mListItems;
	private LayoutInflater mLayoutInflater;
	
	public YearSelectAdapter(Context context, ArrayList<YearSelectListItem> arrayList) {
		mListItems = arrayList;
		mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
    @Override
    public int getItemViewType(int position) {
    	// TODO Auto-generated method stub
    	//return mListItems.get(position).viewType;
    	return 0;
    }
    
    @Override
    public int getViewTypeCount() {
    	return 1;
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
        final YearSelectListItem listItem = mListItems.get(position);
        
        //check to see if the reused view is null or not, if is not null then reuse it
        if (view == null) {
            holder = new ViewHolder();
            
    		view = mLayoutInflater.inflate(R.layout.yearselect_list_item, viewGroup, false);
    		holder.yearTextView = (TextView) view.findViewById(R.id.yearselect_year);
            holder.iconImageView = (ImageView) view.findViewById(R.id.yearselect_icon);
            
            // the setTag is used to store the data within this view
            view.setTag(holder);
        } else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = (ViewHolder)view.getTag();
        }
 
        if (listItem != null) {
        	//TODO: might want to check if each is null.

        	holder.yearTextView.setText(listItem.year);
        	holder.iconImageView.setImageResource(listItem.imageResId);
        }
 
        //this method must return the view corresponding to the data at the specified position.
        return view;
 
    }
    
	//static class view holder to prevent repeated calls to findViewById
	private static class ViewHolder {
		protected TextView yearTextView;
		protected ImageView iconImageView;
	}
	
	public static class YearSelectListItem {
		public String year;
		public int imageResId;
	}
}
