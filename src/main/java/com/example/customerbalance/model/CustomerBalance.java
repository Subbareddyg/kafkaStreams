package com.example.customerbalance.model;

public class CustomerBalance {
	
	String accountId;
	String customerId;
	String phoneNumber;
	float balance;
	public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public float getBalance() {
		return balance;
	}
	public void setBalance(float balance) {
		this.balance = balance;
	}
	public CustomerBalance(String accountId, String customerId, String phoneNumber, float balance) {
		super();
		this.accountId = accountId;
		this.customerId = customerId;
		this.phoneNumber = phoneNumber;
		this.balance = balance;
	}
	public CustomerBalance() {
		// TODO Auto-generated constructor stub
	}
	public CustomerBalance mergeDetails(CustomerBalance customerBalance) {
		// TODO Auto-generated method stub
		this.accountId=customerBalance.accountId;
		this.balance=customerBalance.balance;
		this.customerId=customerBalance.customerId;
		this.phoneNumber=customerBalance.phoneNumber;
		return this;
	}
	public CustomerBalance mergeCustomer(Customer customerDetails) {
		System.out.println("sample customer");
		this.accountId=customerDetails.accountId;
		this.customerId=customerDetails.getCustomerId();
		this.phoneNumber=customerDetails.getPhoneNumber();

		return this;
	}
	public CustomerBalance mergeBalance(Transaction balanceDetails) {
		// TODO Auto-generated method stub
		
		System.out.println("sample balance");
		this.accountId=balanceDetails.accountId;
		this.balance=balanceDetails.balance;
		return null;
	}
	public boolean isComplete() {
		return accountId != null && !"".equals(customerId) && phoneNumber != null;
	}

}
