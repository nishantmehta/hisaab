package com.hisaab.ui;

import java.util.Hashtable;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
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
import com.hisaab.imp.routine.CheckSummary;
import com.hisaab.imp.routine.GetReply;
import com.hisaab.imp.routine.Utility;
import com.hisaab.unitclasses.Friend;
import com.hisaab.unitclasses.Transaction;
import com.hisaab.unitclasses.TransactionPacket;
import com.nishant.hisaab.R;

public class PendingTransaction extends SherlockActivity implements ResultsListener {

	PendingTransactionAdapter adapter;
	ListView list;
	Context ctx;
	String message;
	Long ID;
	String phoneNumber;
	List<Transaction> transactions;
	Transaction clickedTransaction;
	String TAG = "Transaction update testing";
	int currentFlag = -99;
	ProgressDialog progressDialog;
	
	@Override
	protected void onResume() {
		super.onResume();
		refresh();
		
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		refresh();
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pending_transaction);
		
		
		
		ctx = this;
		
		
		//action bar code 
		ActionBar actionBar = getSupportActionBar();
		actionBar.setCustomView(R.layout.action_bar);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		ImageButton refresh = (ImageButton)actionBar.getCustomView().findViewById(R.id.Refresh);
		ImageButton notification = (ImageButton)actionBar.getCustomView().findViewById(R.id.pendingTransaction);
		refresh.setVisibility(View.INVISIBLE);
		notification.setVisibility(View.INVISIBLE);
		
		TextView title = (TextView) actionBar.getCustomView().findViewById(R.id.actionBarTitle);
		title.setText("Alerts");
		
		ImageButton home = (ImageButton)actionBar.getCustomView().findViewById(R.id.HomeButton);
		home.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		
		
		list = (ListView) findViewById(R.id.listView1);
		TransactionDataSource TDS = new TransactionDataSource(this);
		TDS.open();
		transactions = TDS.getAllPendingTransactions();
		TDS.close();
		
		Log.d("date",transactions.size()+"");
		
		adapter = new PendingTransactionAdapter(this, transactions);

		list.setAdapter(adapter);

		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long id) {
				clickedTransaction = transactions.get(position);
				Log.d(TAG,
						"clicked transaction ID : "
								+ clickedTransaction.get_id());
				ShowCustomDialog(ctx);

			}
		});

	}

	public void ShowCustomDialog(final Context ctx) {
		final Dialog dialog = new Dialog(ctx);
		
		
		dialog.setContentView(R.layout.dialog_layout);
		FriendsDataSource FDS = new FriendsDataSource(ctx);
		FDS.open();
		Friend friend = FDS.getFriend(clickedTransaction.getCom_id());
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
		TextView status = (TextView)dialog.findViewById(R.id.txtStatus);
		status.setText(Utility.getStatusString(clickedTransaction));
		date.setText("at "+com.hisaab.imp.routine.Utility.getTimeString(clickedTransaction.get_id()) + "");
		

		LinearLayout buttonTray = (LinearLayout) dialog
				.findViewById(R.id.ButtonTray);
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		// for view the transaction
		if (clickedTransaction.getFlag() == TransactionFlagValues.transactionApprovalRequestOnReceiver) {

			Button Accept = new Button(ctx);
			Accept.setText("Accept");
			Accept.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					currentFlag = -1;
					Transaction transaction = clickedTransaction;
					transaction
							.setFlag(TransactionFlagValues.activeTransaction);
					sendTransactionUpdate(transaction);
					TransactionDataSource TDS = new TransactionDataSource(ctx);
					TDS.open();
					TDS.createTransaction(transaction);
					TDS.deletePendingTransaction(transaction);
					TDS.close();
					Log.d(TAG, "request sent . transaction with ID : "
							+ transaction.get_id());
					dialog.dismiss();
				}
			});

			buttonTray.addView(Accept);

			Button Reject = new Button(ctx);
			Reject.setText("Reject");
			Reject.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Transaction transaction = clickedTransaction;
					transaction.setFlag(TransactionFlagValues.denyTransaction);
					sendTransactionUpdate(transaction);
					TransactionDataSource TDS = new TransactionDataSource(ctx);
					TDS.open();

					TDS.deletePendingTransaction(transaction);
					TDS.close();
					dialog.dismiss();
				}
			});

			buttonTray.addView(Reject);

			Button Later = new Button(ctx);
			Later.setText("Later");
			Later.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					dialog.dismiss();
				}
			});

			buttonTray.addView(Later);
		} else if (clickedTransaction.getFlag() == TransactionFlagValues.transactionApprovalRequest) {
			Button ok = new Button(ctx);
			ok.setText("Ok");
			ok.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					dialog.dismiss();
				}
			});

			buttonTray.addView(ok);
		}
		else if (clickedTransaction.getFlag() == TransactionFlagValues.settleRequestOnReceiver)
		{
			Button Accept = new Button(ctx);
			Accept.setText("Accept");
			Accept.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Transaction data = clickedTransaction;
					data.setFlag(TransactionFlagValues.settleApproval);
					sendTransactionUpdate(data);
					clickedTransaction.setFlag(TransactionFlagValues.settledTransaction);
					currentFlag=-2;
					dialog.dismiss();
				}
			});
			buttonTray.addView(Accept);
			Button ok = new Button(ctx);
			ok.setText("Later");
			ok.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					dialog.dismiss();
				}
			});

			buttonTray.addView(ok);
		}
		else if (clickedTransaction.getFlag() == TransactionFlagValues.settleRequest)
		{
			Button ok = new Button(ctx);
			ok.setText("Ok");
			ok.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					dialog.dismiss();
				}
			});

			buttonTray.addView(ok);
		}
		else{
			Button ok = new Button(ctx);
			ok.setText("Ok");
			ok.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					dialog.dismiss();
					TransactionDataSource TDS = new TransactionDataSource(ctx);
					TDS.open();
					TDS.deletePendingTransaction(clickedTransaction);
					TDS.close();
					refresh();
				}
			});

			buttonTray.addView(ok);
		}

		dialog.show();

	}

	@Override
	protected Dialog onCreateDialog(int id) {
		// Create out AlterDialog
		Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(message);
		builder.setCancelable(true);
		builder.setPositiveButton("ok", null);
		AlertDialog dialog = builder.create();
		dialog.show();

		return super.onCreateDialog(id);
	}

	public void sendTransactionUpdate(Transaction transaction) {
		TransactionPacket data1 = new TransactionPacket();
		ConnectionDetector CD = new ConnectionDetector(ctx);
		if(!CD.isConnectingToInternet())
		{makeToast("internet not connected");
		return;}
		progressDialog = ProgressDialog.show(ctx,
				null, "Updating transaction");
		data1 = data1.copyTransation(transaction, ctx, MODE_WORLD_READABLE);
		Gson convertor = new Gson();
		Hashtable data = new Hashtable();
		data.put("URL", this.getString(R.string.send_transaction_URL));
		data.put("data", convertor.toJson(data1));
		GetReply task = new GetReply();
		task.setOnResultsListener(this);
		task.execute(data);
	}
	 public void makeToast(String msg) {
		 Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	 }
	@Override
	public void onResultsSucceeded(String result) {
		Gson convertor = new Gson();
		String success = "null";
		progressDialog.dismiss();

		try {
			JSONObject data = new JSONObject(result);
			success = data.getString("success");

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (success.equalsIgnoreCase("1")) {
			if (currentFlag == -1) {
				TransactionDataSource TDS = new TransactionDataSource(ctx);
				TDS.open();
				TDS.deletePendingTransaction(clickedTransaction);
				TDS.createTransaction(clickedTransaction);
				TDS.close();
			}
			if(currentFlag==-2)
			{
				TransactionDataSource TDS = new TransactionDataSource(ctx);
				TDS.open();
				TDS.deletePendingTransaction(clickedTransaction);
				TDS.updateTransaction(clickedTransaction);
				TDS.close();
			}
			refresh();
		}
	}
	public void refresh()
	{
		TransactionDataSource TDS = new TransactionDataSource(this);
		TDS.open();
		transactions = TDS.getAllPendingTransactions();
		TDS.close();
		
		Log.d("date",transactions.size()+"");
		
		adapter = new PendingTransactionAdapter(this, transactions);

		list.setAdapter(adapter);

		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long id) {
				clickedTransaction = transactions.get(position);
				Log.d(TAG,
						"clicked transaction ID : "
								+ clickedTransaction.get_id());
				ShowCustomDialog(ctx);

			}
		});

	}
}
