package com.hisaab.ui;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hisaab.unitclasses.Transaction;
import com.nishant.hisaab.R;

public class TransactionAdapter extends BaseAdapter {
	private static LayoutInflater inflater=null;
	List<Transaction> tr;
	public TransactionAdapter(Activity a,List<Transaction> data)
	{
		tr=data;
		Log.d("resulthisaab", "size of arr " + data.size());
		inflater=(LayoutInflater)a.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return tr.size();
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
        {
            vi = inflater.inflate(R.layout.transaction_tiles, null);

            
        }
        
        TextView title = (TextView)vi.findViewById(R.id.description); // title
        
        title.setBackgroundColor(Color.LTGRAY);
        
        Transaction tran = tr.get(position);
        LinearLayout wrapper = (LinearLayout)vi.findViewById(R.id.wrapper);
        if(tran.getToPay()==0)
        {
        	wrapper.setGravity(Gravity.LEFT);
        	title.setBackgroundColor(Color.LTGRAY);
        }
        else
        {
        	wrapper.setGravity(Gravity.RIGHT);
        	title.setBackgroundColor(Color.LTGRAY);
        }
        if (tran.getFlag()== TransactionFlagValues.settledTransaction)
        {
        	title.setBackgroundColor(Color.GRAY);
        }
        Log.d("resulthisaab",tran.getDecsription());
        // Setting all values in listview
        title.setText(tran.getDecsription());
        return vi;
	}

}
