package com.mfino.dao.query;


/**
 * @author Srikanth
 * 
 */
public class RuleKeyQuery extends BaseQuery {
	private Long serviceID;
	private Long transactionTypeID;
	private String txnRuleKey;
	private String txnRuleKeyType;
	private Integer txnRuleKeyPriority;
	private String txnRuleKeyComparision;
	private boolean sortByPriority;
	
	public Long getServiceID() {
		return serviceID;
	}
	public void setServiceID(Long serviceID) {
		this.serviceID = serviceID;
	}
	public Long getTransactionTypeID() {
		return transactionTypeID;
	}
	public void setTransactionTypeID(Long transactionTypeID) {
		this.transactionTypeID = transactionTypeID;
	}
	public String getTxnRuleKey() {
		return txnRuleKey;
	}
	public void setTxnRuleKey(String txnRuleKey) {
		this.txnRuleKey = txnRuleKey;
	}
	public String getTxnRuleKeyType() {
		return txnRuleKeyType;
	}
	public void setTxnRuleKeyType(String txnRuleKeyType) {
		this.txnRuleKeyType = txnRuleKeyType;
	}
	public Integer getTxnRuleKeyPriority() {
		return txnRuleKeyPriority;
	}
	public void setTxnRuleKeyPriority(Integer txnRuleKeyPriority) {
		this.txnRuleKeyPriority = txnRuleKeyPriority;
	}
	public String getTxnRuleKeyComparision() {
		return txnRuleKeyComparision;
	}
	public void setTxnRuleKeyComparision(String txnRuleKeyComparision) {
		this.txnRuleKeyComparision = txnRuleKeyComparision;
	}
	public boolean isSortByPriority() {
		return sortByPriority;
	}
	public void setSortByPriority(boolean sortByPriority) {
		this.sortByPriority = sortByPriority;
	}
}
