package com.nishant.hisaab;

import static com.hisaab.core.CommonUtilities.SENDER_ID;

import java.util.Hashtable;
import java.util.List;

import android.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.ViewDebug.FlagToString;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.gson.Gson;
import com.hisaab.core.ResultsListener;
import com.hisaab.database.FriendsDataSource;
import com.hisaab.database.TransactionDataSource;
import com.hisaab.imp.routine.GetReply;
import com.hisaab.imp.routine.Utility;
import com.hisaab.ui.AddNotificationTransaction;
import com.hisaab.ui.PendingTransaction;
import com.hisaab.unitclasses.Friend;
import com.hisaab.unitclasses.RegisterObject;
import com.hisaab.unitclasses.Transaction;

public class GCMIntentService extends GCMBaseIntentService {
	String TAG="Transaction update testing";
	public GCMIntentService() {
		super(SENDER_ID);
	}

	@Override
	protected void onError(Context arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onMessage(Context arg0, Intent arg1) {
		// TODO Auto-generated method stub
		Log.d(TAG,"message received");
		String message = arg1.getExtras().getString("message");
		String sender1 = arg1.getExtras().getString("sender");
		
		//create the transaction object from intent
		
		Transaction pendingTransaction = new Transaction();
		pendingTransaction.set_id(Long.parseLong(arg1.getExtras().getString(
				"id")));
		pendingTransaction.setAmount(Integer.parseInt(arg1.getExtras()
				.getString("amount")));
		pendingTransaction.setCom_id(Long.parseLong(arg1.getExtras().getString(
				"sender")));
		pendingTransaction
				.setDecsription(arg1.getExtras().getString("message"));
		pendingTransaction.setFlag(Integer.parseInt(arg1.getExtras().getString(
				"flag")));
		pendingTransaction.setToPay(Integer.parseInt(arg1.getExtras()
				.getString("toPay")));
		
		//to reverse the transaction when received on the other side 
		if(pendingTransaction.getToPay()==0)
			pendingTransaction.setToPay(1);
		else
			pendingTransaction.setToPay(0);
		
		
		Log.d(TAG,"received transaction with ID : "+ pendingTransaction.get_id()+" and flag = "+ pendingTransaction.getFlag());
		
		FriendsDataSource FDS1 = new FriendsDataSource(this);
		FDS1.open();
		FDS1.createOrUpdateFriend(FDS1.getFriend(pendingTransaction.getCom_id()));
		FDS1.close();
		
		//array of option for which a pending request needs to be generated 
		int pendingTransactionFlag[] = {
				TransactionFlagValues.transactionApprovalRequestOnReceiver,
				TransactionFlagValues.deleteRequestOnReceiver,
				TransactionFlagValues.settleRequestOnReceiver };

		//approvals received, does not need user interaction 
		int ApprovalGrantedFlag[] = { TransactionFlagValues.deleteApproval,
				TransactionFlagValues.settleApproval,
				TransactionFlagValues.activeTransaction};

		if (checkArray(pendingTransactionFlag, pendingTransaction.getFlag())) {
			TransactionDataSource TDS = new TransactionDataSource(this);
			TDS.open();
			TDS.createPendingTransaction(pendingTransaction);
			TDS.close();
		}
		
		if(pendingTransaction.getFlag()== TransactionFlagValues.denyTransaction)
		{
			TransactionDataSource TDS = new TransactionDataSource(this);
			TDS.open();
			TDS.deletePendingTransaction(pendingTransaction);
			TDS.createPendingTransaction(pendingTransaction);
			TDS.close();
		}
		//auto action to be taken 
		if (checkArray(ApprovalGrantedFlag, pendingTransaction.getFlag())) {
			TransactionDataSource TDS = new TransactionDataSource(this);
			Log.d(TAG,"approved t");

			// if a delete transaction request is approved
			// the transaction is deleted from pending transaction and normal
			// transaction list
			if (pendingTransaction.getFlag() == TransactionFlagValues.deleteApproval) {
				TDS.open();
				TDS.deletePendingTransaction(pendingTransaction);
				TDS.createPendingTransaction(pendingTransaction);
				TDS.deleteTransaction(pendingTransaction);
				TDS.close();
			}

			// if settle request is approved
			// then delete the transaction form pending and normal
			// create a settled transaction in the database
			if (pendingTransaction.getFlag() == TransactionFlagValues.settleApproval) {
				TDS.open();
				TDS.deletePendingTransaction(pendingTransaction);
				pendingTransaction.setFlag(TransactionFlagValues.settledTransaction);
				TDS.createPendingTransaction(pendingTransaction);
				TDS.updateTransaction(pendingTransaction);
				TDS.close();
			}

			// if transaction is approved
			// then delete the transaction from pending and add it to the normal
			// transaction database
			if (pendingTransaction.getFlag() == TransactionFlagValues.activeTransaction) {
				TDS.open();
				TDS.deletePendingTransaction(pendingTransaction);
				TDS.createPendingTransaction(pendingTransaction);
				TDS.createTransaction(pendingTransaction);
				TDS.close();
				Log.d(TAG,"approved transaction with ID : "+ pendingTransaction.get_id() + " for user ID : "+ pendingTransaction.getCom_id());
			}

		}

		
		Long sender = Long.parseLong(sender1);
		FriendsDataSource FDS = new FriendsDataSource(arg0);
		FDS.open();
		List<Friend> list = FDS.getAllFriends();
		FDS.close();
		int size = list.size();
		int i;
		boolean flag = true;
		
		for (i = 0; i < size; i++) {
			long number = list.get(i).getId();
			Log.d("resulthisaab", number + " other" + sender);
			if (sender == number) {
				setNotification(arg0, list.get(i), arg1);
				flag = false;
			}

		}
		if (flag) {
			setNotification(arg0, null, arg1);
		}
	}

	private void setNotification(Context context, Friend friend, Intent arg1) {
		String name;

		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Intent notificationIntent = new Intent(this,
				PendingTransaction.class);
		int flag= Integer.parseInt(arg1.getExtras().getString(
				"flag"));
		if (friend == null) {
			name = arg1.getExtras().getString("sender");
		} else {
			name = friend.getName();

		}
		int icon = R.drawable.ic_lock_power_off;
		long when = System.currentTimeMillis();

		Notification notification = new Notification(icon, arg1.getExtras()
				.getString("message"), when);

		String title = name;

		// set intent so it does not start a new activity
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent intent = PendingIntent.getActivity(context, 0,
				notificationIntent, 0);
		notification.setLatestEventInfo(context, title, Utility.getNotificationString(flag), intent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		// Play default notification sound
		notification.defaults |= Notification.DEFAULT_SOUND;

		// Vibrate if vibrate is enabled
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		notificationManager.notify(0, notification);

	}

	@Override
	protected void onRegistered(Context arg0, String arg1) {
		// TODO Auto-generated method stub
		Log.d("GCM abc", "device reg :" + arg1);
		SharedPreferences myPrefs = arg0.getSharedPreferences("UserInfoHisaab", MODE_WORLD_READABLE);
		
		RegisterObject user = new RegisterObject();
		
		user.setName(myPrefs.getString("UserName", ""));
		user.setPhone_number(myPrefs.getString("phoneNumber", ""));
		user.setGCM_id(arg1);
		String URL = "http://hisaab.hostoi.com/index.php/register_controller/register_user/";
		if(!user.getPhone_number().equalsIgnoreCase(""))
		{fnRegister(user,URL);
		Log.d("GCM abc", "device reg :" + arg1);}
		
	}

	@Override
	protected void onUnregistered(Context arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	public static Boolean checkArray(int data[], int value) {
		int length = data.length;
		for (int i = 0; i < length; i++) {
			if (data[i] == value) {
				return true;
			}
		}
		return false;

	}
	
	public Boolean fnRegister(RegisterObject user,String URL) {

		Gson convertor = new Gson();
		Hashtable data = new Hashtable();
		data.put("URL", URL);
		data.put("data", convertor.toJson(user));
		try {
			Log.d("resulthisaab", convertor.toJson(user));
			GetReply task = new GetReply();
			task.execute(data);
			task.setOnResultsListener(new ResultsListener() {
				
				@Override
				public void onResultsSucceeded(String result) {
					// TODO Auto-generated method stub
					
				}
			});
			Log.d("GCM abc", "device reg done");
		} catch (Exception e) {
			// TODO: handle exception
		}
		return true;
		
	}
}
