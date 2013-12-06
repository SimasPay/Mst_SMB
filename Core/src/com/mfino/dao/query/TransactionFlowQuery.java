package com.mfino.dao.query;

public class TransactionFlowQuery extends BaseQuery{
	private String name;
	private Long serviceTransactionID;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getServiceTransactionID() {
		return serviceTransactionID;
	}
	public void setServiceTransactionID(Long serviceTransactionID) {
		this.serviceTransactionID = serviceTransactionID;
	}
}
