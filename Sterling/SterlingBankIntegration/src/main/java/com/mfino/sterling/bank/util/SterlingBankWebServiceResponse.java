package com.mfino.sterling.bank.util;

import java.util.Date;
import java.util.List;

/**
 * 
 * @author Amar
 *
 */
public class SterlingBankWebServiceResponse {
	
	public class Record
	{
		private String date;
		private Boolean isCredit;
		private String amount;
		
		public Record(){}
		
		public Record(String date, Boolean isCredit, String amount)
		{
			this.date = date;
			this.isCredit = isCredit;
			this.amount = amount;
		}
		
		public String getDate() {
			return date;
		}
		public void setDate(String date) {
			this.date = date;
		}
		public Boolean getIsCredit() {
			return isCredit;
		}
		public void setIsCredit(Boolean isCredit) {
			this.isCredit = isCredit;
		}
		public String getAmount() {
			return amount;
		}
		public void setAmount(String amount) {
			this.amount = amount;
		}		
	}
	
	private String referenceID;
	private String requestType;
	private String responseCode;
	private String responseText;
	private String account;
	private String availableBalance;
	private String book;
	private List<Record> records;
	private String sessionID;
	
	public String getReferenceID() {
		return referenceID;
	}
	
	public void setReferenceID(String referenceID) {
		this.referenceID = referenceID;
	}

	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public String getResponseText() {
		return responseText;
	}

	public void setResponseText(String responseText) {
		this.responseText = responseText;
	}

	public String getAvailableBalance() {
		return availableBalance;
	}

	public void setAvailableBalance(String availableBalance) {
		this.availableBalance = availableBalance;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getBook() {
		return book;
	}

	public void setBook(String book) {
		this.book = book;
	}

	public List<Record> getRecords() {
		return records;
	}

	public void setRecords(List<Record> records) {
		this.records = records;
	}

	public String getSessionID() {
		return sessionID;
	}

	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}	
	
	
}
