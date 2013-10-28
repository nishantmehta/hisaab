package com.hisaab.imp.routine;

import java.sql.Date;
import java.util.Calendar;

import com.hisaab.unitclasses.Transaction;

import android.content.Context;
import android.provider.ContactsContract.Contacts.Data;
import android.util.Log;


public class Utility {
	
	
	public static String getTimeString(Long timeStamp)
	{
		
		Long tsLong = timeStamp;
		tsLong=tsLong*1000;
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(tsLong);
		java.util.Date date = cal.getTime();
		Calendar today = Calendar.getInstance();
		today.setTimeInMillis(System.currentTimeMillis());
		java.util.Date todayDate = today.getTime();
		if(date.getDay()>=todayDate.getDay()&& date.getMonth()>=todayDate.getMonth()&&date.getYear()>=todayDate.getYear())
		{
			int hours=date.getHours();
			String minutes = date.getMinutes()+"";
			String day=" AM";
			if (hours>12)
			{
				hours = hours-12;
				day= " PM";
			}
			if(minutes.length()==1)
			{
				minutes="0"+minutes;
			}
			String timeString= hours + ":"+minutes+day;
			return timeString;
		}
		else
		{
			String timeString = date.getDay()+"/"+date.getMonth()+"/"+date.getYear();
			return timeString;
		}
		
		
	}

	public static String getTransactionType(int type)
	{
		if(TransactionFlagValues.activeTransaction==type)
			return "active transaction";
		else if (TransactionFlagValues.settledTransaction==type)
			return "settled transaction";
		else if (TransactionFlagValues.transactionApprovalRequestOnReceiver==type)
			return "you have been requested this transaction";
		else if (TransactionFlagValues.transactionApprovalRequest==type)
			return "transaction requested";
		else if (TransactionFlagValues.settleRequest==type)
			return "you have initiated a settle request";
		else if (TransactionFlagValues.settleRequestOnReceiver==type)
			return "transaction has been requested to be settled";
		else if (TransactionFlagValues.deleteRequest==type)
			return "you have requested to delete this transaction";
		else if (TransactionFlagValues.deleteRequestOnReceiver==type)
			return "transaction has been requested to be deleted";
		return null;
	}
	
	public static String getMessageString(Transaction data)
	{
		String message = data.getDecsription();
		if (message.length()>25)
		{
			String shortMessage = message.substring(0, 24);
			message = shortMessage+"... Amount : "+data.getAmount();
		}
		
		else
			message = message+"... Amount : "+data.getAmount();
		
		return message;
	}
	public static String getStatusString(Transaction data)
	{
		String message= "not know";
		if (data.getFlag()==TransactionFlagValues.deleteRequest)
			message = "You have sent a delete request for this transaction";
		else if (data.getFlag()==TransactionFlagValues.deleteApproval)
			message =  "Delete request has been approved";
		else if (data.getFlag()==TransactionFlagValues.deleteRequestOnReceiver)
			message = "Delete request has been sent for this transaction";
		else if (data.getFlag()==TransactionFlagValues.activeTransaction)
			message = "This transaction is been approved and is active now";
		else if (data.getFlag()==TransactionFlagValues.denyTransaction)
			message = "This transaction has been rejected by the receiver";
		else if (data.getFlag()==TransactionFlagValues.settleApproval)
			message = "Settle request is been approved! the transaction is no longer active ";
		else if (data.getFlag()==TransactionFlagValues.settleRequest)
			message = "You have requested this transaction to be settled";
		else if (data.getFlag()==TransactionFlagValues.settleRequestOnReceiver)
			message = "A settle request has been sent for this transaction";
		else if (data.getFlag()==TransactionFlagValues.settledTransaction)
			message = "This transaction is settled and is no longer active";
		else if (data.getFlag()==TransactionFlagValues.transactionApprovalRequest)
			message = "You have requested this transaction";
		else if (data.getFlag()==TransactionFlagValues.transactionApprovalRequestOnReceiver)
			message = "A new transaction has been requested";
		return message;
	}
	
	public static String getNotificationString(int flag)
	{
		Transaction data = new Transaction();
		data.setFlag(flag);
		String message= "not know";
		if (data.getFlag()==TransactionFlagValues.deleteRequest)
			message = "You have sent a delete request for this transaction";
		else if (data.getFlag()==TransactionFlagValues.deleteApproval)
			message =  "Delete request has been approved";
		else if (data.getFlag()==TransactionFlagValues.deleteRequestOnReceiver)
			message = "A delete request has been sent";
		else if (data.getFlag()==TransactionFlagValues.activeTransaction)
			message = "A transaction is been approved";
		else if (data.getFlag()==TransactionFlagValues.denyTransaction)
			message = "A transaction has been rejected";
		else if (data.getFlag()==TransactionFlagValues.settleApproval)
			message = "Settle request is been approved!";
		else if (data.getFlag()==TransactionFlagValues.settleRequest)
			message = "You have requested this transaction to be settled";
		else if (data.getFlag()==TransactionFlagValues.settleRequestOnReceiver)
			message = "A transaction has been requested to be settled";
		else if (data.getFlag()==TransactionFlagValues.settledTransaction)
			message = "Settle request has been approved";
		else if (data.getFlag()==TransactionFlagValues.transactionApprovalRequest)
			message = "You have requested this transaction";
		else if (data.getFlag()==TransactionFlagValues.transactionApprovalRequestOnReceiver)
			message = "A new transaction has been requested";
		return message;
	}
}
