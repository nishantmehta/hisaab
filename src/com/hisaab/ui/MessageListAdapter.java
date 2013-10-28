package com.hisaab.ui;

import java.util.List;

import com.hisaab.database.FriendsDataSource;
import com.hisaab.imp.routine.Utility;
import com.hisaab.unitclasses.Friend;
import com.hisaab.unitclasses.Transaction;
import com.nishant.hisaab.R;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MessageListAdapter extends BaseAdapter{
	private static LayoutInflater inflater=null;
	List<Transaction> tr;
	Context ctx;
	public MessageListAdapter(Activity a,List<Transaction> data)
	{
		ctx=a;
		tr=data;
		Log.d("resulthisaab", "size   arr " + data.size());
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
            vi = inflater.inflate(R.layout.message_tile, null);

            
        }
        
        TextView title = (TextView)vi.findViewById(R.id.name); // title
        TextView message = (TextView)vi.findViewById(R.id.message); // title
        TextView status = (TextView)vi.findViewById(R.id.status); // title
        Transaction tran = tr.get(position);
        FriendsDataSource FDS = new FriendsDataSource(ctx);
        FDS.open();
        Friend friend = FDS.getFriend(tran.getCom_id());
        FDS.close();
        
        
        title.setText(friend.getName());
        
        
        message.setText(Utility.getMessageString(tran));
        status.setText(Utility.getStatusString(tran));
        Log.d("resulthisaab",tran.getDecsription());
        // Setting all values in listview
        
        return vi;
	}
}
