package com.mfino.dao.query;

public class TransactionIdentifierQuery extends BaseQuery {
	private String transactionIdentifier;
    private String serviceChargeTransactionLogID;
	public String getTransactionIdentifier() {
		return transactionIdentifier;
	}
	public void setTransactionIdentifier(String transactionIdentifier) {
		this.transactionIdentifier = transactionIdentifier;
	}
	public String getServiceChargeTransactionLogID() {
		return serviceChargeTransactionLogID;
	}
	public void setServiceChargeTransactionLogID(String sctlID) {
		serviceChargeTransactionLogID = sctlID;
	}
}
