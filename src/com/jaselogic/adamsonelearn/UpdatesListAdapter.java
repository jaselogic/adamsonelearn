package com.jaselogic.adamsonelearn;

import java.util.ArrayList;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

class UpdatesListAdapter extends BaseAdapter {
    private ArrayList<UpdatesListItem> mListItems;
    private LayoutInflater mLayoutInflater;
 
    public UpdatesListAdapter(Context context, ArrayList<UpdatesListItem> arrayList){
        mListItems = arrayList;
        //get the layout inflater
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    
    @Override
    public int getItemViewType(int position) {
    	// TODO Auto-generated method stub
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
        UpdatesListItem listItem = mListItems.get(position);
        
        //check to see if the reused view is null or not, if is not null then reuse it
        if (view == null) {
            holder = new ViewHolder();
    		view = mLayoutInflater.inflate(R.layout.updates_list_item, viewGroup, false);
            holder.nameTextView = (TextView) view.findViewById(R.id.updates_name);
            holder.subjectTextView = (TextView) view.findViewById(R.id.updates_subject);
            holder.titleTextView = (TextView) view.findViewById(R.id.updates_title);
            holder.bodyTextView = (TextView) view.findViewById(R.id.updates_body);
            holder.dateAddedTextView = (TextView) view.findViewById(R.id.updates_date);
            holder.avatarImageView = (ImageView) view.findViewById(R.id.updates_avatar);
 
            // the setTag is used to store the data within this view
            view.setTag(holder);
        } else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = (ViewHolder)view.getTag();
        }
 
        if (listItem != null) {
        	//TODO: might want to check if each is null.
            /*if (holder.itemName != null) {
                //set the item name on the TextView
                holder.itemName.setText(listItem.label);
            }
            if (holder.iconImageView != null) {
            	switch (listItem.itemType) {
            		case NAME:
            			UrlImageViewHelper.setUrlDrawable(holder.iconImageView, avatarSrc);
            			break;
            		case SIMPLE:
            			holder.iconImageView.setImageResource(listItem.imageResource);
            			break;
            	}
            }*/
        	holder.nameTextView.setText(listItem.name);
        	holder.subjectTextView.setText(listItem.subject);
        	holder.titleTextView.setText(listItem.title);
        	holder.bodyTextView.setText(listItem.body);
        	holder.dateAddedTextView.setText(listItem.dateAdded);
        	UrlImageViewHelper.setUrlDrawable(holder.avatarImageView, listItem.avatarSrc);
        }
 
        //this method must return the view corresponding to the data at the specified position.
        return view;
 
    }
    
	//static class view holder to prevent repeated calls to findViewById
	private static class ViewHolder {
		protected TextView nameTextView;
		protected TextView subjectTextView;
		protected TextView titleTextView;
		protected TextView bodyTextView;
		protected TextView dateAddedTextView;
		protected ImageView avatarImageView;
	}
	
	//Drawer list item
	public static class UpdatesListItem {
		public String name;
		public String subject;
		public String title;
		public String body;
		public String dateAdded;
		public String avatarSrc;
	}
}
