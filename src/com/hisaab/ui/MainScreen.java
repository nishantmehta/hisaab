package com.hisaab.ui;



import java.sql.Date;
import java.util.Calendar;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.hisaab.core.ResultsListener;
import com.hisaab.database.FriendsDataSource;
import com.hisaab.database.TransactionDataSource;
import com.hisaab.imp.routine.RefreshService;
import com.hisaab.unitclasses.Friend;
import com.nishant.hisaab.R;


public class MainScreen extends SherlockActivity implements ResultsListener{
Context ctx;
List<Friend> data;
ListView list;
ListAdapter adapter;
ProgressDialog progressDialog;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_screen);
		ActionBar actionBar = getSupportActionBar();
	    // add the custom view to the action bar
	    actionBar.setCustomView(R.layout.action_bar);
	    actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
	            );
		ctx=this;
		
		
		ImageButton refresh = (ImageButton)actionBar.getCustomView().findViewById(R.id.Refresh);
		refresh.setOnClickListener(new OnClickListener() {

			
			@Override
			public void onClick(View arg0) {
				
				searchFriends();
				setProgressBarIndeterminateVisibility(true);
			      progressDialog = ProgressDialog.show(ctx,
			              "Accounts", "Searching for friends");
				
			}
		});
		
		ImageButton notification = (ImageButton)actionBar.getCustomView().findViewById(R.id.pendingTransaction);
		notification.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent myintent = new Intent(ctx, PendingTransaction.class);
				startActivity(myintent);
				
			}
		});
		
		
		TransactionDataSource TDS = new TransactionDataSource(this);
		TDS.open();
		TDS.close();
		
		SharedPreferences myPrefs = ctx.getSharedPreferences("UserInfoHisaab", MODE_WORLD_READABLE);
		String registered = myPrefs.getString("registered", null);
		if(registered.equalsIgnoreCase("0"))
		{
			Intent registration = new Intent(this, MainActivity.class);
			startActivity(registration);
			finish();
		}
		list=(ListView)findViewById(R.id.list);
		FriendsDataSource FDS = new FriendsDataSource(this);
		FDS.open();
		data=FDS.getAllFriends();
		FDS.close();
		
		adapter= new ListAdapter(this,data);
		list.setAdapter(adapter);
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long id) {
				long ID=data.get(position).getId();
				String phoneNumber=data.get(position).getPhoneNumber();
				Intent myIntent = new Intent(ctx, AccountSummary.class);
				myIntent.putExtra("ID", ID);
				myIntent.putExtra("phoneNumber", phoneNumber);
				ctx.startActivity(myIntent);
				
			}
			
		});
	}
	public void onResume() {
		super.onResume();
		FriendsDataSource FDS = new FriendsDataSource(this);
		FDS.open();
		data=FDS.getAllFriends();
		FDS.close();
		
		adapter= new ListAdapter(this,data);
		list.setAdapter(adapter);
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long id) {
				long ID=data.get(position).getId();
				String phoneNumber=data.get(position).getPhoneNumber();
				Intent myIntent = new Intent(ctx, AccountSummary.class);
				myIntent.putExtra("ID", ID);
				myIntent.putExtra("phoneNumber", phoneNumber);
				ctx.startActivity(myIntent);
				
			}
			
		});
	}
	@Override
	public void onResultsSucceeded(String result) {
		setProgressBarIndeterminateVisibility(false);
		 
		 FriendsDataSource FDS = new FriendsDataSource(this);
			FDS.open();
			data=FDS.getAllFriends();
			FDS.close();
			
			adapter= new ListAdapter(this,data);
			list.setAdapter(adapter);
			list.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View view, int position,
						long id) {
					long ID=data.get(position).getId();
					String phoneNumber=data.get(position).getPhoneNumber();
					Intent myIntent = new Intent(ctx, AccountSummary.class);
					myIntent.putExtra("ID", ID);
					myIntent.putExtra("phoneNumber", phoneNumber);
					ctx.startActivity(myIntent);
					
				}
				
			});
		
			progressDialog.dismiss();
	}
	
	private void searchFriends()
	{
		RefreshService ref = new RefreshService();
		ref.RefreshService(ctx);
		ref.setOnResultsListener((ResultsListener) ctx);
		ref.execute(ctx);
	}
	
	public void makeToast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}


	

}
