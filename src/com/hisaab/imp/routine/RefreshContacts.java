package com.hisaab.imp.routine;
import com.hisaab.core.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.util.Log;

import com.google.gson.Gson;
import com.hisaab.core.Refresh_data;

public class RefreshContacts {

	final int ID = 0;
	final int PHONENUMBER = 1;
	final int NAME = 2;
	String tempdata[][];
	Context context;
	ArrayList<String> phoneNumber= new ArrayList<String>();
	Boolean flag = false;
	int iRecordNumber = 0;

	
	
	
	public String refresh(Context cxt) throws JSONException {
		context = cxt;
		Log.d("myapp", "it starts here");
		
		// start(context);
		
		Cursor cursor = context.getContentResolver().query(
				ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
		tempdata = new String[cursor.getCount()][3];
		
		Log.d("resulthisaab","cursor count is"+cursor.getCount());
		while (cursor.moveToNext()) {

			String contactId = cursor.getString(cursor
					.getColumnIndex(ContactsContract.Contacts._ID));
			String name = cursor.getString(cursor
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
					this.phoneNumber.add((String)phoneNumber);
					iRecordNumber++;
				}
				
				phones.close();

			}
			

		}
		Log.d("resulthisaab","final coun 	t is"+iRecordNumber);
		cursor.close();
		
		Gson abs = new Gson();
		
		Refresh_data rdata= new Refresh_data();
		rdata.receiver=phoneNumber;
		String data = abs.toJson(rdata);
		Log.d("resulthisaab", "length is " + phoneNumber.size());
		Log.d("resulthisaab", data);
		Log.d("resulthisaab", "done");
		return data;
		/*
		if (flag)
			return true;
		else
			return false;
		*/
	}

	Handler UIupdater = new Handler() {

		public void handleMessage(int tempUser[]) {

			// add data to the global variable

			// add contacts to the database

			// success

		}
	};

	public void start(final Context context) {

		Boolean flag;
		Thread backgroundWork = new Thread(new Runnable() {

			@Override
			public void run() {

				// take all the contacts from the contacts list
				// code to retrive contacts

				// link:
				// http://stackoverflow.com/questions/1721279/how-to-read-contacts-on-android-2-0
				Log.d("myapp", "part 3");
				Cursor people = context.getContentResolver().query(
						ContactsContract.Contacts.CONTENT_URI, null, null,
						null, null);
				Log.d("myapp", String.valueOf(people.getCount()));

				while (people.moveToNext()) {

					Log.d("myapp", "part 3");
					int nameFieldColumnIndex = people
							.getColumnIndex(PhoneLookup.DISPLAY_NAME);
					tempdata[iRecordNumber][NAME] = people
							.getString(nameFieldColumnIndex);
					Log.d("myapp", tempdata[iRecordNumber][NAME]);
					int numberFieldColumnIndex = people
							.getColumnIndex(PhoneLookup.NUMBER);
					tempdata[iRecordNumber][PHONENUMBER] = people
							.getString(numberFieldColumnIndex);
					//phoneNumber[iRecordNumber] = tempdata[iRecordNumber][PHONENUMBER];
					int IDFieldColumnIndex = people
							.getColumnIndex(PhoneLookup._ID);
					tempdata[iRecordNumber][ID] = people
							.getString(IDFieldColumnIndex);
					iRecordNumber++;
				}

				Log.d("myapp", "part 5");
				people.close();

				Gson convertor = new Gson();
				String data = convertor.toJson(phoneNumber).toString();

				Log.d("myapp", String.valueOf(iRecordNumber));
				// create a array containing phone number, name and ID

				// create another array with only phone number

				// callServer(<<data>>)

				// process the data

			}
		});

		backgroundWork.start();
	}

	public String callServer(String data) {
		// send this array to the server

		// get response and give it as return value
		return null;
	}

}

