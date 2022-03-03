package com.example.customerbalance.model;

public class Customer {
	public Customer() {
		super();
		// TODO Auto-generated constructor stub
	}
	String customerId;
	String name;
	String phoneNumber;
	String accountId;
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
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
		this.phoneNumber = phoneNumber;
	}
	public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	public Customer(String customerId, String name, String phoneNumber, String accountId) {
		super();
		this.customerId = customerId;
		this.name = name;
		this.phoneNumber = phoneNumber;
		this.accountId = accountId;
	}

}
