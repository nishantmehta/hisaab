package com.nishant.hisaab;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class TransationRequest extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_transation_request);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_transation_request, menu);
		return true;
	}

}
