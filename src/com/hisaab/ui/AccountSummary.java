package com.hisaab.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.hisaab.core.ResultsListener;
import com.hisaab.database.FriendsDataSource;
import com.hisaab.imp.routine.CheckSummary;
import com.hisaab.unitclasses.Friend;
import com.nishant.hisaab.R;

public class AccountSummary extends SherlockActivity implements ResultsListener {
	Context ctx;
	Long ID;
	String phoneNumber;
	TextView tv;

	@Override
	protected void onResume() {
		super.onResume();
		CheckSummary CS = new CheckSummary(ctx);
		CS.setOnResultsListener(this);
		CS.execute(ID);
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		CheckSummary CS = new CheckSummary(ctx);
		CS.setOnResultsListener(this);
		CS.execute(ID);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ID = (Long) getIntent().getExtras().getLong("ID");
		phoneNumber = (String) getIntent().getExtras().getString("phoneNumber");

		ctx = this;
		FriendsDataSource FDS = new FriendsDataSource(ctx);
		FDS.open();
		Friend current= FDS.getFriend((ID));
		FDS.close();
		// action bar code
		ActionBar actionBar = getSupportActionBar();
		actionBar.setCustomView(R.layout.action_bar);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		ImageButton refresh = (ImageButton) actionBar.getCustomView()
				.findViewById(R.id.Refresh);
		ImageButton notification = (ImageButton) actionBar.getCustomView()
				.findViewById(R.id.pendingTransaction);
		refresh.setVisibility(View.INVISIBLE);
		notification.setVisibility(View.INVISIBLE);

		TextView title = (TextView) actionBar.getCustomView().findViewById(
				R.id.actionBarTitle);
		title.setText(current.getName());

		ImageButton home = (ImageButton) actionBar.getCustomView()
				.findViewById(R.id.HomeButton);
		home.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		
		
		setContentView(R.layout.activity_account_summary);

		CheckSummary CS = new CheckSummary(ctx);
		CS.setOnResultsListener(this);
		CS.execute(ID);

		tv = (TextView) findViewById(R.id.transaction_text);
		Button add_transaction = (Button) findViewById(R.id.addTransaction);
		add_transaction.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent myIntent = new Intent(ctx, AddTransaction.class);
				myIntent.putExtra("ID", ID);
				myIntent.putExtra("phoneNumber", phoneNumber);
				ctx.startActivity(myIntent);

			}
		});

		Button view_summary = (Button) findViewById(R.id.summary);
		view_summary.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent myIntent = new Intent(ctx, TransactionActivity.class);
				myIntent.putExtra("ID", ID);
				myIntent.putExtra("phoneNumber", phoneNumber);
				ctx.startActivity(myIntent);

			}
		});
	}

	

	@Override
	public void onResultsSucceeded(String result) {
		int amount = Integer.parseInt(result);
		String text;
		if (amount > 0) {
			text = "you have to pay " + amount;
		} else if (amount < 0) {
			text = "you have to collect " + amount * -1;
		} else {
			text = "The account is settled";
		}
		tv.setText(text);

	}

}
