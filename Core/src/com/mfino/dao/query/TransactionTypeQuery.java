package com.mfino.dao.query;

/**
 * @author sasidhar
 *
 */
public class TransactionTypeQuery extends BaseQuery{
	
	private Long serviceId;
	private String transactionName;
	
	public Long getServiceId() {
		return serviceId;
	}
	
	public void setServiceId(Long serviceId) {
		this.serviceId = serviceId;
	}
	
	public String getTransactionName() {
		return transactionName;
	}
	
	public void setTransactionName(String transactionName) {
		this.transactionName = transactionName;
	}
}
