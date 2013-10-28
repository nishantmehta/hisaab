package com.hisaab.unitclasses;

public class RegisterObject {
	
	/*
	 * id	_gcm	: This is the GCM ID used to identify a android device to send updates via google cloud messaging servicce 
	 * name			: identifies the friend according to the entry in the phonebook
	 * phoneNumber	: This the number stored in the phone book with the username, if a user is associated with multiple
	 * 				 phone number then multiple entries are created in the data base.
	 */
	
	private String name;
	private String phoneNumber;
	private String id_gcm;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
	public String getGCM_id() {
		return id_gcm;
	}
	public void setGCM_id(String gCM_id) {
		id_gcm = gCM_id;
	}
	public String getPhone_number() {
		return phoneNumber;
	}
	public void setPhone_number(String phone_number) {
		this.phoneNumber = phone_number;
	}
	

}
