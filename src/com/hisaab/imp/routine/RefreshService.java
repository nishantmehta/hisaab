package com.hisaab.imp.routine;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Looper;
import android.provider.ContactsContract;
import android.util.Log;

import com.google.gson.Gson;
import com.hisaab.core.Refresh_data;
import com.hisaab.core.ResultsListener;
import com.hisaab.database.FriendsDataSource;
import com.hisaab.ui.MainActivity;
import com.hisaab.ui.MainScreen;
import com.hisaab.unitclasses.Friend;
import com.nishant.hisaab.R;
import com.nishant.hisaab.R.string;

public class RefreshService extends
		AsyncTask<Context, String, String> implements ResultsListener {

	final int ID = 0;
	final int PHONENUMBER = 1;
	final int NAME = 2;
	String tempdata[][];
	private Context context;
	ArrayList<String> phoneNumber = new ArrayList<String>();
	Boolean flag = false;
	int iRecordNumber = 0;
	String data;
	ResultsListener listener;
	ProgressDialog progressDialog;

	// constructor for passing context
	public void RefreshService(Context ctx) {
		this.context = ctx;

	}

	@Override
	public void onResultsSucceeded(String result) {
		// TODO Auto-generated method stub
		String data =  result.substring(result.indexOf('[')+1, result.indexOf(']'));
        String[] resultarr=data.split(",");
        Log.d("resulthisaab",data);
        FriendsDataSource FDS = new FriendsDataSource(context);
        FDS.open();
        Log.d("resulthisaab","DB opened");
        int length=resultarr.length;
        for (int i =0;i<length;i++)
        {
        	if(resultarr[i].equals("1"))
        	{
        		SharedPreferences myPrefs = context.getSharedPreferences("UserInfoHisaab", 0);
                
                String number = myPrefs.getString("phoneNumber", null);
                String phoneNumber = tempdata[i][PHONENUMBER];
				phoneNumber = phoneNumber.replaceAll("[^0-9]", "");
				if (number.equalsIgnoreCase(phoneNumber))
					continue;
					
        		//if the user is registered with server
        		Friend friend=new Friend();
        		friend.setName(tempdata[i][NAME]);
        		friend.setPhoneNumber(tempdata[i][PHONENUMBER]);
        		friend.setLastTransaction((long)9999);
        		FDS.createOrUpdateFriend(friend);
        	}
        }
		
        
        List<Friend> friendsSata = FDS.getAllFriends();
        FDS.close();
        Log.d("resulthisaab","DB closed");
        Log.d("resulthisaab","the size of database is "+friendsSata.size());
        
		
	}

	public void setOnResultsListener(ResultsListener listener) {
		this.listener = listener;
	}
	@Override
	protected void onPostExecute(String result) {
		listener.onResultsSucceeded(result);

	}
	
	@Override
	protected String doInBackground(Context... params) {
		// code to extract phone number form the contact list and populate the
		// array structure
		try {
			
			context=params[0];
			
			if (true) {
				Cursor cursor = context.getContentResolver().query(
						ContactsContract.Contacts.CONTENT_URI, null, null, null,
						null);
				tempdata = new String[cursor.getCount()][3];

				Log.d("resulthisaab", "cursor count is" + cursor.getCount());
				while (cursor.moveToNext()) {

					String contactId = cursor.getString(cursor
							.getColumnIndex(ContactsContract.Contacts._ID));
					String name = cursor
							.getString(cursor
									.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
					String hasPhone = cursor
							.getString(cursor
									.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
					Log.d("myapp", hasPhone);
					if (hasPhone.equals("1")) {
						// You know it has a number so now query it like this

						Cursor phones = context.getContentResolver().query(
								ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
								null,
								ContactsContract.CommonDataKinds.Phone.CONTACT_ID
										+ " = " + contactId, null, null);
						Log.d("myapp", contactId);
						while (phones.moveToNext()) {
							String phoneNumber = phones
									.getString(phones
											.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
							tempdata[iRecordNumber][NAME] = name;
							tempdata[iRecordNumber][PHONENUMBER] = phoneNumber;
							tempdata[iRecordNumber][ID] = contactId;
							this.phoneNumber.add((String) phoneNumber);
							iRecordNumber++;
						}

						phones.close();

					}

				}
				Log.d("resulthisaab", "final coun 	t is" + iRecordNumber);
				cursor.close();

				Gson abs = new Gson();

				Refresh_data rdata = new Refresh_data();
				rdata.receiver = phoneNumber;
				data = abs.toJson(rdata);
				
			}

			// end of code to extract phone number form the contact list and populate the array structure

			// code to send the data to server
			if (true) {
				Hashtable data1 = new Hashtable();
				data1.put("URL", context.getString(R.string.refresh_URL));
				data1.put("data", data);
				GetReply task = new GetReply();
				task.setOnResultsListener(this);
				task.execute(data1);
			}
			// end of code to send the data to server
			return null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.d("resulthisaab",e.getMessage());
			return null;
		}
	}

}
