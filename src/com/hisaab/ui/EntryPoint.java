package com.hisaab.ui;

import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.hisaab.core.ResultsListener;
import com.hisaab.imp.routine.RefreshService;
import com.nishant.hisaab.R;

public class EntryPoint extends SherlockActivity implements OnClickListener, ResultsListener {
	Context ct;
	ActionBar actionbar;
	ProgressDialog progressDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_entry_point);
		//new RefreshService().execute(ct);
		Button login = (Button) findViewById(R.id.loginscreen);
		ActionBar actionBar = getSupportActionBar();
	    // add the custom view to the action bar
	    actionBar.setCustomView(R.layout.action_bar);
	    actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
	            );
		Button fl = (Button) findViewById(R.id.friends);
		String number="nisahnt\nnishant\nhbdjaf";
		
		ct = this;
		String[] list =number.split("\n");
		Toast.makeText(ct,list[0], Toast.LENGTH_LONG).show();
		login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent myintent = new Intent(ct, MainActivity.class);
				startActivity(myintent);

			}
		});
		
		fl.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent myintent = new Intent(ct, MainScreen.class);
				startActivity(myintent);
			}
		});
		
		Button refresh = (Button)findViewById(R.id.refresh_list);
		Button pendingTransaction = (Button)findViewById(R.id.pendingTransaction);
		pendingTransaction.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent myintent = new Intent(ct, PendingTransaction.class);
				startActivity(myintent);
			}
		});
		refresh.setOnClickListener(this);
		// tran.setOnClickListener(this);
		/*getSupportActionBar();
		actionbar=getSupportActionBar();
		actionbar.setDisplayShowCustomEnabled(true);
		actionbar.setCustomView(R.layout.action_bar);
		actionbar.setDisplayShowHomeEnabled(false);*/
		
		
	}

	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		case R.id.refresh_list:{
			RefreshService ref = new RefreshService();
			ref.RefreshService(this);
			ref.setOnResultsListener(this);
			ref.execute(this);
			setProgressBarIndeterminateVisibility(true);
		      progressDialog = ProgressDialog.show(EntryPoint.this,
		              "ProgressDialog", "Wait!");
			break;
		}
		}
		
	}
	
	


	@Override
	public void onResultsSucceeded(String result) {
		setProgressBarIndeterminateVisibility(false);
	 progressDialog.dismiss();
		
	        Toast.makeText(this,"list refreshed", Toast.LENGTH_LONG).show();
	       
	}







	

}
