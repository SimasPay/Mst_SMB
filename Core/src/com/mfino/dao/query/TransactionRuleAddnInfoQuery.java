package com.mfino.dao.query;


/**
 * @author Srikanth
 * 
 */
public class TransactionRuleAddnInfoQuery extends BaseQuery {
	private Long transactionRuleID;
	private String txnRuleKey;
	private String txnRuleValue;
	private String txnRuleComparator;
	
	public Long getTransactionRuleID() {
		return transactionRuleID;
	}
	public void setTransactionRuleID(Long transactionRuleID) {
		this.transactionRuleID = transactionRuleID;
	}
	public String getTxnRuleKey() {
		return txnRuleKey;
	}
	public void setTxnRuleKey(String txnRuleKey) {
		this.txnRuleKey = txnRuleKey;
	}
	public String getTxnRuleValue() {
		return txnRuleValue;
	}
	public void setTxnRuleValue(String txnRuleValue) {
		this.txnRuleValue = txnRuleValue;
	}
	public String getTxnRuleComparator() {
		return txnRuleComparator;
	}
	public void setTxnRuleComparator(String txnRuleComparator) {
		this.txnRuleComparator = txnRuleComparator;
	}
	
	
}
