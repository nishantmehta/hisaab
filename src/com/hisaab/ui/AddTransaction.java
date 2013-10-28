package com.hisaab.ui;

import java.util.Hashtable;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hisaab.core.ConnectionDetector;
import com.hisaab.core.ResultsListener;
import com.hisaab.database.FriendsDataSource;
import com.hisaab.database.TransactionDataSource;
import com.hisaab.imp.routine.GetReply;
import com.hisaab.unitclasses.Friend;
import com.hisaab.unitclasses.Transaction;
import com.hisaab.unitclasses.TransactionPacket;
import com.nishant.hisaab.R;

public class AddTransaction extends SherlockActivity implements ResultsListener{
	TextView description, amount;
	RadioButton toPay;
	RadioGroup type;
	String phoneNumber;
	Long ID;
	Context ctx;
	int success=-99;
	Transaction data,dataForSender;
	String TAG="Transaction update testing";
	Button addTransaction;
	ProgressDialog progressDialog;
	Friend current;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_transaction);
		ctx = this;
		ID = (Long) getIntent().getExtras().getLong("ID");
		phoneNumber = (String) getIntent().getExtras().getString("phoneNumber");
		FriendsDataSource FDS = new FriendsDataSource(ctx);
		FDS.open();
		current= FDS.getFriend((ID));
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
		
		addTransaction = (Button) findViewById(R.id.addTransactionButton);
		
		description = (TextView) findViewById(R.id.descriptionText);
		amount = (TextView) findViewById(R.id.amount);
		type = (RadioGroup) findViewById(R.id.radioToPay);

		addTransaction.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (amount.getText().toString().length()<1)
				{
					makeToast("enter a valid amount");
					return;
				}
				if(description.getText().toString().length()<1)
				{
					makeToast("enter a description");
					return;
				}
				if(findViewById(type.getCheckedRadioButtonId())==null)
				{
					makeToast("please select a transaction type");
					return;
				}
				ConnectionDetector CD = new ConnectionDetector(ctx);
				if(!CD.isConnectingToInternet())
				{makeToast("internet not connected");
				return;}
				setProgressBarIndeterminateVisibility(true);
				progressDialog = ProgressDialog.show(ctx,
						null, "Sending transaction");
				data = new Transaction();
				data.setAmount(Integer.parseInt(amount.getText().toString()));
				data.setDecsription(description.getText().toString());
				data.setCom_id(ID);
				data.setFlag(com.hisaab.ui.TransactionFlagValues.transactionApprovalRequestOnReceiver);
				int typeID = type.getCheckedRadioButtonId();
				toPay = (RadioButton) findViewById(typeID);
				if (toPay.getText().toString()
						.equalsIgnoreCase(ctx.getString(R.string.toPay_text)))
					data.setToPay(1);
				else
					data.setToPay(0);

				Long tsLong = System.currentTimeMillis() / 1000;
				data.set_id(tsLong);
				
				Log.d(TAG,"sending transaction1 with ID : "+ data.get_id()+" and flag = "+ data.getFlag()); 
				
				sendTransaction(data);
				
				
				
				dataForSender=data;

			}
		});
	}

	public boolean sendTransaction(Transaction transactionData)	
	{
		TransactionPacket data1 = new TransactionPacket();
		ConnectionDetector CD = new ConnectionDetector(ctx);
		if(!CD.isConnectingToInternet())
		{makeToast("internet not connected");
		return false;}
		data1=data1.copyTransation(transactionData,ctx,MODE_WORLD_READABLE);
		Log.d(TAG,"sending transaction with ID : "+ data1.getId()+" and flag = "+ data1.getFlag());
		Gson convertor= new Gson();
    	Hashtable data= new Hashtable();
    	data.put("URL",this.getString(R.string.send_transaction_URL));
    	data.put("data",convertor.toJson(data1));
    	GetReply task = new GetReply();
        task.setOnResultsListener(this);
        task.execute(data);
        Log.d(TAG,"sending transaction with ID : "+ data1.getId()+" and flag = "+ data1.getFlag());
		return false;
	}


	@Override
	public void onResultsSucceeded(String result) {
		
		Log.d(TAG,result+ " my result");
		if(result==null)
		{
			makeToast("transaction failed, please check internet connection");
			return;
		}
		Gson gn = new Gson();
		JsonElement r;
		JsonParser jp = new JsonParser();
		
		JsonObject abc = jp.parse(result).getAsJsonObject();
		abc.getAsJsonObject(result);
		r=abc.get("success");
		
		success=Integer.parseInt(r.toString());
		Log.d(TAG,result);
        progressDialog.dismiss();
        if (success==1) {
        	data.setFlag(com.hisaab.ui.TransactionFlagValues.transactionApprovalRequest);
			TransactionDataSource TDS = new TransactionDataSource(ctx);
			TDS.open();
			TDS.createPendingTransaction(data);
			TDS.close();
			
			FriendsDataSource FDS = new FriendsDataSource(ctx);
			FDS.open();
			FDS.updateTimeStamp(FDS.getFriend(dataForSender.getCom_id()));
			FDS.close();
			Log.d(TAG,"Transaction added in pending transaction with ID : "+ data.get_id());
			Toast.makeText(this,"done", Toast.LENGTH_LONG).show();
			finish();
		}
        else
        {
        	Toast.makeText(this,"please retry", Toast.LENGTH_LONG).show();
        }
		
	}

	public void makeToast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}
}
