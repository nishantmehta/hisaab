package com.hisaab.unitclasses;

import android.content.Context;
import android.content.SharedPreferences;

import com.hisaab.database.FriendsDataSource;

public class TransactionPacket {
	
	/*
	 * flag				: this may be used to maintain delivery reports and other status signals
	 * 					: -1 indicates rejected
	 * 					  0 indicates not accepted status 
	 * 					  1 indicates accepted 
	 * 					  2 indicates accounts settled 
	 */
	
	String sender;
	long id;
	Long receiver;
	String message;
	int toPay;
	int amount, flag;
	long lastUpdated;
	
	public String getSender() {
		return sender;
	}



	public void setSender(String sender) {
		this.sender = sender;
	}



	public long getId() {
		return id;
	}



	public void setId(long id) {
		this.id = id;
	}



	public Long getReceiver() {
		return receiver;
	}



	public void setReceiver(Long receiver) {
		this.receiver = receiver;
	}



	public String getMessage() {
		return message;
	}



	public void setMessage(String message) {
		this.message = message;
	}



	public int getToPay() {
		return toPay;
	}



	public void setToPay(int toPay) {
		this.toPay = toPay;
	}



	public int getAmount() {
		return amount;
	}



	public void setAmount(int amount) {
		this.amount = amount;
	}



	public int getFlag() {
		return flag;
	}



	public void setFlag(int flag) {
		this.flag = flag;
	}



	
	
	
	
	public TransactionPacket copyTransation(Transaction data,Context context, int value)
	{
		FriendsDataSource FDS = new FriendsDataSource(context);
		FDS.open();
		TransactionPacket newData = new TransactionPacket();
		newData.id=data.get_id();
		newData.message=data.getDecsription();
		newData.receiver=data.getCom_id();
		newData.toPay=data.getToPay();
		newData.amount=data.getAmount();
		newData.flag=data.getFlag();
		SharedPreferences myPrefs = context.getSharedPreferences("UserInfoHisaab", value);
        
        String number = myPrefs.getString("phoneNumber", null);
		
		newData.sender=number;
		FDS.close();
		return newData;
	}

}
