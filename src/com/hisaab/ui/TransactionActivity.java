package com.hisaab.ui;

import java.util.Hashtable;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.google.gson.Gson;
import com.hisaab.core.ConnectionDetector;
import com.hisaab.core.ResultsListener;
import com.hisaab.database.FriendsDataSource;
import com.hisaab.database.TransactionDataSource;
import com.hisaab.imp.routine.GetReply;
import com.hisaab.imp.routine.Utility;
import com.hisaab.unitclasses.Friend;
import com.hisaab.unitclasses.Transaction;
import com.hisaab.unitclasses.TransactionPacket;
import com.nishant.hisaab.R;

public class TransactionActivity extends SherlockActivity implements ResultsListener{

	TransactionAdapter adapter;
	ListView list;
	Context ctx;
	String message;
	Long ID;
	String phoneNumber;
	List<Transaction> transactions;
	Transaction clickedTransaction;
	String TAG="Transaction update testing";
	ProgressDialog progressDialog;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.transaction);
		ID=(Long)getIntent().getExtras().getLong("ID");
		phoneNumber=(String)getIntent().getExtras().getString("phoneNumber");
		ctx=this;
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
		Log.d(TAG,"opening transaction for Com ID : "+ ID);
		list=(ListView)findViewById(R.id.listView1);
		TransactionDataSource TDS = new TransactionDataSource(this);
		TDS.open();
		Log.d("resulthisaab","ID "+ID);
		transactions=TDS.getAllTransactions(ID);
		TDS.close();
		
		adapter = new TransactionAdapter(this,transactions);
		
		list.setAdapter(adapter);
		
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long id) {
					clickedTransaction=transactions.get(position);
					ShowCustomDialog(ctx);
					//message=data.getDecsription()+" of amount "+data.getAmount()+" is to be taken/paid";
					showDialog(0);
				
			}
		});
		
		Button addTran= (Button)findViewById(R.id.addTransaction);
		addTran.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent myIntent = new Intent(ctx,AddTransaction.class);
				myIntent.putExtra("ID", ID);
				myIntent.putExtra("phoneNumber", phoneNumber);
				ctx.startActivity(myIntent);
				finish();
			}
		});
		
	}
	
	public void ShowCustomDialog(Context ctx)
	{
		final Dialog dialog = new Dialog(ctx);
		
		dialog.setContentView(R.layout.dialog_layout);
		FriendsDataSource FDS = new FriendsDataSource(ctx);
		FDS.open();
		Friend friend= FDS.getFriend(clickedTransaction.getCom_id());
		dialog.setTitle(friend.getName());
		FDS.close();
		
		TextView message = (TextView) dialog.findViewById(R.id.txtDescription);
		TextView amount = (TextView) dialog.findViewById(R.id.txtAmount);
		TextView date = (TextView) dialog.findViewById(R.id.txtDate);
		TextView type = (TextView) dialog.findViewById(R.id.txtType);
		
		if (clickedTransaction.getToPay()==1)
		{
			type.setText("To pay");
		}
		else
			type.setText("To take");
		message.setText(clickedTransaction.getDecsription());
		amount.setText("Amount "+clickedTransaction.getAmount() + " for");
		date.setText("at "+com.hisaab.imp.routine.Utility.getTimeString(clickedTransaction.get_id()) + "");
		TextView status = (TextView)dialog.findViewById(R.id.txtStatus);
		status.setText(Utility.getStatusString(clickedTransaction));
		
		LinearLayout buttonTray = (LinearLayout) dialog
				.findViewById(R.id.ButtonTray);
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		//for view the transaction
		if (clickedTransaction.getFlag()== TransactionFlagValues.activeTransaction)
		{
		Button Ok = new Button(ctx);
		Ok.setText("Cancel");
		Ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				dialog.dismiss();	
			}
		});
		
		
		Button settle = new Button(ctx);
		settle.setText("Settle");
		settle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Transaction data = clickedTransaction;
				data.setFlag(TransactionFlagValues.settleRequestOnReceiver);
				sendTransactionUpdate(data);
				clickedTransaction.setFlag(TransactionFlagValues.settleRequest);
				dialog.dismiss();	
			}
		});
		buttonTray.addView(settle);
		buttonTray.addView(Ok);
		}
		else if(clickedTransaction.getFlag()==TransactionFlagValues.settleApproval)
		{
			Button Ok = new Button(ctx);
			Ok.setText("Ok");
			Ok.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					dialog.dismiss();	
				}
			});
			buttonTray.addView(Ok);
		}
		else
		{
			Button Ok = new Button(ctx);
			Ok.setText("Ok");
			Ok.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					dialog.dismiss();	
				}
			});
			buttonTray.addView(Ok);
		}
		dialog.show();
		
		
		
	}
	public void sendTransactionUpdate(Transaction transaction) {
		ConnectionDetector CD = new ConnectionDetector(ctx);
		if(!CD.isConnectingToInternet())
		{makeToast("internet not connected");
		return;}
		progressDialog = ProgressDialog.show(ctx,
				null, "Updating transaction");
		TransactionPacket data1 = new TransactionPacket();
		data1 = data1.copyTransation(transaction, ctx, MODE_WORLD_READABLE);
		Gson convertor = new Gson();
		Hashtable data = new Hashtable();
		data.put("URL", this.getString(R.string.send_transaction_URL));
		data.put("data", convertor.toJson(data1));
		GetReply task = new GetReply();
		task.setOnResultsListener(this);
		task.execute(data);
	}
	 @Override
	  protected Dialog onCreateDialog(int id) {
	      // Create out AlterDialog
	      
	    return super.onCreateDialog(id);
	  }

	 @Override
		public void onResultsSucceeded(String result) {
			Gson convertor = new Gson();
			String success="null";
			Log.d(TAG,result);
			 
			try {
				progressDialog.dismiss();
				JSONObject data= new JSONObject(result);
				success  = data.getString("success");
				
			} catch (JSONException e) {
				Toast.makeText(this,"There was some error, please try again", Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}
			
			if(success.equalsIgnoreCase("1"))
			{
				//modify
					TransactionDataSource TDS = new TransactionDataSource(ctx);
					TDS.open();
					TDS.createOrUpdatePendingTransaction(clickedTransaction);
					TDS.close();
			
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
