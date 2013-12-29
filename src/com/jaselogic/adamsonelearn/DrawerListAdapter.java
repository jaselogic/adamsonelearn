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

class DrawerListAdapter extends BaseAdapter {
    private ArrayList<DrawerListItem> mListItems;
    private LayoutInflater mLayoutInflater;
    private String avatarSrc;
 
    public DrawerListAdapter(Context context, ArrayList<DrawerListItem> arrayList, String imgSrc){
    	avatarSrc = imgSrc;
        mListItems = arrayList;
        //get the layout inflater
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    
    @Override
    public int getItemViewType(int position) {
    	// TODO Auto-generated method stub
    	return mListItems.get(position).itemType.ordinal();
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
        DrawerListItem listItem = mListItems.get(position);
        
        //check to see if the reused view is null or not, if is not null then reuse it
        if (view == null) {
            holder = new ViewHolder();
            switch(listItem.itemType) {
            	case SIMPLE:
            		view = mLayoutInflater.inflate(R.layout.item, viewGroup, false);
                    holder.itemName = (TextView) view.findViewById(R.id.item_text);
                    holder.iconImageView = (ImageView) view.findViewById(R.id.icon_imageview);
            		break;
            	case SEPARATOR:
            		view = mLayoutInflater.inflate(R.layout.separator, viewGroup, false);
            		holder.itemName = (TextView) view.findViewById(R.id.item_separator_text);
            		break;
            	case NAME:
            		view = mLayoutInflater.inflate(R.layout.studinfo, viewGroup, false);
            		holder.itemName = (TextView) view.findViewById(R.id.studinfo_name);
            		holder.iconImageView = (ImageView) view.findViewById(R.id.studinfo_avatar);
            		break;
            }
 
            // the setTag is used to store the data within this view
            view.setTag(holder);
        } else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = (ViewHolder)view.getTag();
        }
 
        if (listItem != null) {
            if (holder.itemName != null) {
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
            }
        }
 
        //this method must return the view corresponding to the data at the specified position.
        return view;
 
    }
    
	//static class view holder to prevent repeated calls to findViewById
	private static class ViewHolder {
		protected TextView itemName;
		protected ImageView iconImageView;
	}

}
