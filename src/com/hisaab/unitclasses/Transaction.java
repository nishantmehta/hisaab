package com.hisaab.unitclasses;

public class Transaction {

	/*
	 * _id				: used to identify a transaction uniquely
	 * description		: Mentions details of the transaction
	 * amount			: mentions the units of amount involved in the transaction
	 * com_id			: is the id of the person whom the transaction is done.
	 * toPay			: 1 indicates that money is to be paid
	 * 					  0 indicates that money needs to be taken
	 * flag				: this may be used to maintain delivery reports and other status signals
	 * 					: -1 indicates rejected
	 * 					  0 indicates not accepted status 
	 * 					  1 indicates accepted 
	 * 					  2 indicates accounts settled 
	 */
	
	long _id=0, com_id;
	String decsription;
	int toPay;
	long lastUpdated;

	public long getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(long lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public int getToPay() {
		return toPay;
	}

	public void setToPay(int toPay) {
		this.toPay = toPay;
	}

	int amount, flag;

	public long get_id() {
		return _id;
	}

	public void set_id(long _id) {
		this._id = _id;
	}

	public long getCom_id() {
		return com_id;
	}

	public void setCom_id(long com_id) {
		this.com_id = com_id;
	}

	public String getDecsription() {
		return decsription;
	}

	public void setDecsription(String decsription) {
		this.decsription = decsription;
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

}
