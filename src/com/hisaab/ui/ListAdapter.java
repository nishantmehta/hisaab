package com.hisaab.ui;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hisaab.unitclasses.Friend;
import com.nishant.hisaab.R;

public class ListAdapter extends BaseAdapter {
	private static LayoutInflater inflater=null;
	List<Friend> fd;
	public ListAdapter(Activity a,List<Friend> data)
	{
		fd=data;
		Log.d("resulthisaab", "size of arr " + data.size());
		inflater=(LayoutInflater)a.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return fd.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.d("resulthisaab", "size of arr");
		View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.list_row, null);
 
        TextView title = (TextView)vi.findViewById(R.id.title); // title
        TextView lastTransaction = (TextView)vi.findViewById(R.id.last_transaction); // artist name
        TextView lastUpdate = (TextView)vi.findViewById(R.id.last_update); // duration
        
        Friend current = fd.get(position);
 
        // Setting all values in listview
        title.setText(current.getName());
        lastTransaction.setText(current.getPhoneNumber());
        
        lastUpdate.setText(com.hisaab.imp.routine.Utility.getTimeString(current.getLastTransaction()));
        return vi;
	}

}
