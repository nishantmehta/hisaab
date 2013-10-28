package com.hisaab.ui;

import java.lang.ref.Reference;
import java.sql.Date;
import java.util.Calendar;
import java.util.Hashtable;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.google.android.gcm.GCMRegistrar;
import com.google.gson.Gson;
import com.hisaab.core.ConnectionDetector;
import com.hisaab.core.ResultsListener;
import com.hisaab.imp.routine.GetReply;
import com.hisaab.imp.routine.RefreshService;
import com.hisaab.unitclasses.RegisterObject;
import com.nishant.hisaab.R;

public class MainActivity extends SherlockActivity implements ResultsListener {

	// UI elements
	Button btnRegister;
	TextView txtPhoneNumber, txtName;
	String GCM_ID = "";
	ProgressDialog progressDialog;
	ConnectionDetector cd;
	Context ctx;
	int workDone = 0;
	String TAG = "Transaction update testing";
	int regDone=-1;
	@Override
	protected void onStop() {
		super.onStart();
		finish();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		checkGCM();
		ctx = this;
		
		// UI bloat
		txtPhoneNumber = (TextView) findViewById(R.id.txtPhoneNumber);
		txtName = (TextView) findViewById(R.id.txtName);
		btnRegister = (Button) findViewById(R.id.btnRegister);
		
		
		// add the custom view to the action bar
		ActionBar actionBar = getSupportActionBar();
		actionBar.setCustomView(R.layout.action_bar);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		ImageButton refresh = (ImageButton)actionBar.getCustomView().findViewById(R.id.Refresh);
		ImageButton notification = (ImageButton)actionBar.getCustomView().findViewById(R.id.pendingTransaction);
		refresh.setVisibility(View.INVISIBLE);
		notification.setVisibility(View.INVISIBLE);
		
		// onClick event
		btnRegister.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String phoneNumber = txtPhoneNumber.getText().toString();
				phoneNumber = phoneNumber.replaceAll("[^0-9]", "");
				if (phoneNumber.length() < 10) {
					makeToast("enter a valid number");
					return;
				}
				if (txtName.getText().toString().length() < 1) {
					makeToast("enter a name");
					return;
				}
				setProgressBarIndeterminateVisibility(true);
				progressDialog = ProgressDialog.show(MainActivity.this,
						"Cloud hisaab", "Checking compatibility..");

				// disable the click button
				btnRegister.setClickable(false);
				
				//*************** To be done *************
				//if shared pref exist then edit
				//*************** To be done *************				
				
				
				SharedPreferences myPrefs = ctx.getSharedPreferences(
						"UserInfoHisaab", MODE_WORLD_READABLE);
				SharedPreferences.Editor prefsEditor = myPrefs.edit();
				prefsEditor.putString("UserName", txtName.getText().toString());

				phoneNumber = phoneNumber.substring(phoneNumber.length() - 10,
						phoneNumber.length());

				prefsEditor.putString("phoneNumber", phoneNumber);
				prefsEditor.commit();

				// check GCM and register
				if (checkGCM()) {

					progressDialog.setMessage("Registering on server ...");
					// creating RegisterObject as per the data
					RegisterObject user = new RegisterObject();
					user.setName(txtName.getText().toString());
					user.setPhone_number(txtPhoneNumber.getText().toString());
					user.setGCM_id(GCM_ID);

					fnRegister(user);

				} else {
					makeToast("Registration failed, Please try again");
				}
				// enable the click button
				btnRegister.setClickable(true);
			}
		});

	}

	public void onResultsSucceeded(String result) {
		String[] flag;
		Log.d("resulthisaab", "work done = " + workDone);
		
		if(regDone==0)
		{
			new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                	Intent myIntent = new Intent(ctx, MainScreen.class);
        			ctx.startActivity(myIntent);
                }
            }, 5000);
			
			
		
		}
		
		
		if (result != null) {
			flag = result.split("\n");

			if (flag[1].contains("true")) {
				Log.d("resulthisaab", "registration done");
				// shared preference update
				
				SharedPreferences myPrefs = ctx.getSharedPreferences(
						"UserInfoHisaab",
						android.content.Context.MODE_WORLD_READABLE);
				SharedPreferences.Editor prefsEditor = myPrefs.edit();
				prefsEditor.putString("registered", "1");
				prefsEditor.commit();

				

				progressDialog.setMessage("Searching for friends...");
				workDone = 1;
				if(regDone==-1)
				{
					checkFriends();
					regDone=0;
				}
				
				
			} else {
				Log.d("resulthisaab", "inside result of main activity" + result);
				progressDialog.dismiss();
				makeToast("Registration failed, please try again");
			}

		}

	}

	public void makeToast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}

	public Boolean checkGCM() {
		cd = new ConnectionDetector(getApplicationContext());

		// Check if Internet present
		if (!cd.isConnectingToInternet()) {
			// Internet Connection is not present

			return false;
		}

		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);
		Log.d(TAG, "Before Registration");
		final String regId = GCMRegistrar.getRegistrationId(this);
		Log.d(TAG, "reg ID is: " + regId);
		GCM_ID = regId;
		if (regId.equals("")) {
			// Registration is not present, register now with GCM
			GCMRegistrar
					.register(this, this.getString(R.string.GCM_Sender_Key));
		} else {

			GCM_ID = regId;
			return true;

		}
		return true;
	}

	private Boolean checkFriends() {
		RefreshService ref = new RefreshService();
		ref.RefreshService(this);
		ref.setOnResultsListener(this);
		ref.execute(this);
		return true;
	}

	public Boolean fnRegister(RegisterObject user) {

		Gson convertor = new Gson();
		Hashtable data = new Hashtable();
		data.put("URL", this.getString(R.string.register_URL));
		data.put("data", convertor.toJson(user));
		Log.d("resulthisaab", convertor.toJson(user));
		GetReply task = new GetReply();
		task.setOnResultsListener(this);
		task.execute(data);
		return true;
	}

	protected void fnInflateUI() {
		// control association
		
	}
}
