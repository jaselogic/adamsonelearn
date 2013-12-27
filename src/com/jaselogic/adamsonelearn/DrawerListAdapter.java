package com.jaselogic.adamsonelearn;

import java.util.ArrayList;
import java.util.TreeSet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

class DrawerListAdapter extends BaseAdapter {
	/*private static final int TYPE_ID = 0;
	private static final int TYPE_ITEM = 1;
	private static final int TYPE_SEPARATOR = 2;
	private static final int TYPE_MAX_COUNT = 3;
	
	private ArrayList<String> listData = new ArrayList<String>();
	private LayoutInflater inflater;
	private TreeSet treeSetSeparator;
	
	public DrawerListAdapter(Context context) {
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public void addItem(final String item) {
		listData.add(item);
		notifyDataSetChanged();
	}
	
	public void addSeparatorItem(final String item) {
		listData.add(item);
		treeSetSeparator.add(listData.size() - 1);
		notifyDataSetChanged();
	}
	
	@Override
	public int getItemViewType(int position) {
		return treeSetSeparator.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
	}
	
	@Override
	public int getViewTypeCount() {
		return TYPE_MAX_COUNT;
	}
	
	@Override
	public int getCount() {
		return listData.size();
	}

	@Override
	public String getItem(int position) {
		return listData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		int type = getItemViewType(position); //get proper item view type
		if (convertView == null) { //if view has yet to be inflated
			holder = new ViewHolder(); //create a new viewholder
			switch(type) { //inflate a proper view according to type
				case TYPE_ITEM:
					convertView = inflater.inflate(R.layout.item, null);
					holder.textView = (TextView) convertView.findViewById(R.id.item_text);
					break;
				case TYPE_SEPARATOR:
					convertView = inflater.inflate(R.layout.separator, null);
					holder.textView = (TextView) convertView.findViewById(R.id.item_separator_text);
					break;
			}
			convertView.setTag(holder); //tag to cache
		} else { //if the view has been inflated once
			holder = (ViewHolder)convertView.getTag(); //get the tagged holder
		}
		holder.textView.setText(listData.get(position)); //set the text from the array list
		return convertView;
	}
	*/
	
    private ArrayList<String> mListItems;
    private LayoutInflater mLayoutInflater;
 
    public DrawerListAdapter(Context context, ArrayList<String> arrayList){
 
        mListItems = arrayList;
 
        //get the layout inflater
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
 
        //check to see if the reused view is null or not, if is not null then reuse it
        if (view == null) {
            holder = new ViewHolder();
 
            view = mLayoutInflater.inflate(R.layout.item, viewGroup, false);
            holder.itemName = (TextView) view.findViewById(R.id.item_text);
 
            // the setTag is used to store the data within this view
            view.setTag(holder);
        } else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = (ViewHolder)view.getTag();
        }
 
        //get the string item from the position "position" from array list to put it on the TextView
        String stringItem = mListItems.get(position);
        if (stringItem != null) {
            if (holder.itemName != null) {
                //set the item name on the TextView
                holder.itemName.setText(stringItem);
            }
        }
 
        //this method must return the view corresponding to the data at the specified position.
        return view;
 
    }
    
	//static class view holder to prevent repeated calls to findViewById
	private static class ViewHolder {
		protected TextView itemName;
	}

}
