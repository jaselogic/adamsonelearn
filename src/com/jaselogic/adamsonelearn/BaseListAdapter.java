package com.jaselogic.adamsonelearn;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public abstract class BaseListAdapter extends BaseAdapter {
	private ArrayList<ViewProducer> mListItems;
	private LayoutInflater mLayoutInflater;
	
	public BaseListAdapter(Context context, ArrayList<ViewProducer> arrayList) {
		mListItems = arrayList;
		mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
    @Override
    public final int getItemViewType(int position) {
    	// TODO Auto-generated method stub
    	//return mListItems.get(position).viewType;
    	//return 0;
    	return mListItems.get(position).getViewTypeOrdinal();
    }
    
    @Override
    public final int getViewTypeCount() {
    	return this.viewTypeCount();
    }

	@Override
    public final int getCount() {
        //getCount() represents how many items are in the list
        return mListItems.size();
    }
 
    @Override
    //get the data of an item from a specific position
    //i represents the position of the item in the list
    public final Object getItem(int i) {
        return null;
    }
 
    @Override
    //get the position id of the item from the list
    public final long getItemId(int i) {
        return 0;
    }
    
    @Override
    public final View getView(int position, View view, ViewGroup viewGroup) {
    	return mListItems.get(position).produceView(view, mLayoutInflater);
    }	

	public abstract int viewTypeCount();
	
	public interface ViewProducer {
		public View produceView(View convertView, LayoutInflater inflater);
		public int getLayoutId();
		public int getViewTypeOrdinal();
	}
}
