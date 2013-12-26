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
	private static final int TYPE_ID = 0;
	private static final int TYPE_ITEM = 1;
	private static final int TYPE_SEPARATOR = 2;
	private static final int TYPE_MAX_COUNT = 3;
	
	private ArrayList<String> listData = new ArrayList();
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
					holder.textView = (TextView) convertView.findViewById(R.id.item_separator);
					break;
			}
			convertView.setTag(holder); //tag to cache
		} else { //if the view has been inflated once
			holder = (ViewHolder)convertView.getTag(); //get the tagged holder
		}
		holder.textView.setText(listData.get(position)); //set the text from the array list
		return convertView;
	}
	
	private static class ViewHolder {
		protected TextView textView;
	}

}
