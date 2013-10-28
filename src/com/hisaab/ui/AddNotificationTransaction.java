package com.hisaab.ui;



import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.hisaab.database.TransactionDataSource;
import com.hisaab.unitclasses.Transaction;
import com.hisaab.unitclasses.TransactionPacket;
import com.nishant.hisaab.R;

public class AddNotificationTransaction extends Activity{

	TransactionAdapter adapter;
	ListView list;
	Context ctx;
	String message;
	Long ID;
	String phoneNumber;
	List<Transaction> transactions;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.transaction);
		
		
		ctx=this;
		list=(ListView)findViewById(R.id.listView1);
		TransactionDataSource TDS = new TransactionDataSource(this);
		TDS.open();
		Log.d("resulthisaab","ID "+ID);
		transactions=TDS.getAllPendingTransactions();
		TDS.close();
		
		adapter = new TransactionAdapter(this,transactions);
		
		list.setAdapter(adapter);
		
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long id) {
					Transaction data=transactions.get(position);
					message=data.getDecsription()+" of amount "+data.getAmount()+" is to be taken/paid";
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
			}
		});
		
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
}
