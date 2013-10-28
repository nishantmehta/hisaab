package com.hisaab.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

import com.nishant.hisaab.R;

public class AccountDetails extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account_details);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_account_details, menu);
		return true;
	}

}
