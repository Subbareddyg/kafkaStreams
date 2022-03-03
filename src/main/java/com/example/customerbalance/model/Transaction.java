package com.example.customerbalance.model;

public class Transaction {
	String balanceId;
	String accountId;
	float balance;
	public String getBalanceId() {
		return balanceId;
	}
	public void setBalanceId(String balanceId) {
		this.balanceId = balanceId;
	}
	public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	public Transaction() {
		super();
		// TODO Auto-generated constructor stub
	}
	public float getBalance() {
		return balance;
	}
	public void setBalance(float balance) {
		this.balance = balance;
	}
	public Transaction(String balanceId, String accountId, float balance) {
		super();
		this.balanceId = balanceId;
		this.accountId = accountId;
		this.balance = balance;
	}

}
