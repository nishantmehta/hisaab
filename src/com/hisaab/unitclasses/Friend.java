package com.hisaab.unitclasses;

public class Friend {

	/*
	 * id			: it is used to uniquely identify a friend 
	 * name			: identifies the friend according to the entry in the phonebook
	 * phoneNumber	: This the number stored in the phone book with the username, if a user is associated with multiple
	 * 				 phone number then multiple entries are created in the data base.
	 * lastTransaction: last transaction made on the user
	 */
	private long id;
	private String name;
	private String phoneNumber;
	private Long lastTransaction=(long)999;
	
	public Long getLastTransaction() {
		return lastTransaction;
	}
	public void setLastTransaction(Long lastTransaction) {
		this.lastTransaction = lastTransaction;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		String number= phoneNumber;
		number=number.replaceAll("[^0-9]", "");
		number=number.substring(number.length()-10, number.length());
		id=Long.parseLong(number);
		this.phoneNumber = phoneNumber;
	}
	public long getId() {
		return id;
	}
	

}
