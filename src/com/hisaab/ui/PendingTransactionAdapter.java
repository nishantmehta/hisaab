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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hisaab.database.FriendsDataSource;
import com.hisaab.imp.routine.Utility;
import com.hisaab.unitclasses.Friend;
import com.hisaab.unitclasses.Transaction;
import com.nishant.hisaab.GCMIntentService;
import com.nishant.hisaab.R;
import com.nishant.hisaab.TransactionFlagValues;

public class PendingTransactionAdapter extends BaseAdapter {
	private static LayoutInflater inflater = null;
	List<Transaction> tr;
	Context ctx;

	public PendingTransactionAdapter(Activity a, List<Transaction> data) {
		ctx = a;
		tr = data;
		Log.d("resulthisaab", "size   arr " + data.size());
		inflater = (LayoutInflater) a
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

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
		
		View vi = convertView;
		
		if (convertView == null) {
			vi = inflater.inflate(R.layout.list_row_pending_transaction, null);

		}

		TextView title = (TextView) vi.findViewById(R.id.title); // title
		TextView message = (TextView) vi.findViewById(R.id.message); // title
		RelativeLayout back = (RelativeLayout) vi.findViewById(R.id.pendingRowBack);
		back.invalidate();
		
		LinearLayout statusNotification = (LinearLayout) vi
				.findViewById(R.id.statusNotification);
		
		LinearLayout container = (LinearLayout) vi
				.findViewById(R.id.pendingTransactionContainer);

		Transaction tran = tr.get(position);
		FriendsDataSource FDS = new FriendsDataSource(ctx);
		FDS.open();
		Friend friend = FDS.getFriend(tran.getCom_id());
		FDS.close();
		// approvals received, does not need user interaction
		int ApprovalGrantedFlag[] = { TransactionFlagValues.transactionApprovalRequestOnReceiver,
				TransactionFlagValues.deleteRequestOnReceiver,
				TransactionFlagValues.settleRequestOnReceiver};
		
		if (tran.getFlag()==ApprovalGrantedFlag[0]||tran.getFlag()==ApprovalGrantedFlag[1]||tran.getFlag()==ApprovalGrantedFlag[2]){
			
			back.setBackgroundColor(Color.parseColor("#356AA0"));
		}
		else
		{
			Log.d("data for view", tran.getFlag()+ "");
			
			back.setBackgroundColor(Color.parseColor("#D1D0CE"));
		}

		title.setText(friend.getName());

		message.setText(Utility.getMessageString(tran));

		Log.d("resulthisaab", tran.getDecsription());
		// Setting all values in listview

		return vi;
	}
	Boolean checkArray(int data[], int value) {
		int length = data.length;
		for (int i = 0; i < length; i++) {
			if (data[i] == value) {
				return true;
			}
		}
		return false;

	}
}
