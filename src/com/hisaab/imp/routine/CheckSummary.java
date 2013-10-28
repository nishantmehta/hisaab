package com.hisaab.imp.routine;

import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.hisaab.core.ResultsListener;
import com.hisaab.database.TransactionDataSource;
import com.hisaab.ui.TransactionFlagValues;
import com.hisaab.unitclasses.Transaction;

public class CheckSummary extends AsyncTask<Long, String, String> implements
		ResultsListener {

	Context context;
	Long ID;
	int amount = 0;
	ResultsListener listener;

	@Override
	public void onResultsSucceeded(String result) {
		// TODO Auto-generated method stub

	}

	public void setOnResultsListener(ResultsListener listener) {
		this.listener = listener;
	}

	public CheckSummary(Context ctx) {
		this.context = ctx;
	}

	@Override
	protected void onPostExecute(String result) {
		listener.onResultsSucceeded(result);

	}

	@Override
	protected String doInBackground(Long... params) {
		amount = 0;
		ID = (Long) params[0];

		TransactionDataSource TDS = new TransactionDataSource(context);
		TDS.open();
		List<Transaction> TransactionList = TDS.getAllTransactions(ID);
		TDS.close();
		int size = TransactionList.size();
		for (int i = 0; i < size; i++) {
			if (TransactionList.get(i).getFlag() == 0) {
				if (TransactionList.get(i).getToPay() == 1) {
					amount = amount + TransactionList.get(i).getAmount();
				} else {
					amount = amount - TransactionList.get(i).getAmount();
				}
			}
		}

		String amountdata = Integer.toString(amount);
		// TODO Auto-generated method stub
		return amountdata;
	}

}
