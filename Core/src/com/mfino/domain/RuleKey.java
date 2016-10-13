package com.mfino.domain;

// Generated Sep 27, 2016 5:23:21 PM by Hibernate Tools 3.4.0.CR1

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import com.mfino.hibernate.Timestamp;

/**
 * RuleKey generated by hbm2java
 */
@Entity
@Table(name = "RULE_KEY")
public class RuleKey extends Base implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	public static final String FieldName_Service = "service";
	public static final String FieldName_TransactionType = "transactionType";
	public static final String FieldName_TxnRuleKey = "txnrulekey";
	public static final String FieldName_TxnRuleKeyType = "txnrulekeytype";
	public static final String FieldName_TxnRuleKeyPriority = "txnrulekeypriority";
	public static final String FieldName_TxnRuleKeyComparision = "txnrulekeycomparision";
	private Service service;
	private TransactionType transactionType;
	private String txnrulekey;
	private String txnrulekeytype;
	private long txnrulekeypriority;
	private String txnrulekeycomparision;

	public RuleKey() {
	}

	

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SERVICEID", nullable = false)
	public Service getService() {
		return this.service;
	}

	public void setService(Service service) {
		this.service = service;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TRANSACTIONTYPEID", nullable = false)
	public TransactionType getTransactionType() {
		return this.transactionType;
	}

	public void setTransactionType(TransactionType transactionType) {
		this.transactionType = transactionType;
	}

	
	@Column(name = "TXNRULEKEY", nullable = false)
	public String getTxnrulekey() {
		return this.txnrulekey;
	}

	public void setTxnrulekey(String txnrulekey) {
		this.txnrulekey = txnrulekey;
	}

	@Column(name = "TXNRULEKEYTYPE", nullable = false)
	public String getTxnrulekeytype() {
		return this.txnrulekeytype;
	}

	public void setTxnrulekeytype(String txnrulekeytype) {
		this.txnrulekeytype = txnrulekeytype;
	}

	@Column(name = "TXNRULEKEYPRIORITY", nullable = false, precision = 10, scale = 0)
	public long getTxnrulekeypriority() {
		return this.txnrulekeypriority;
	}

	public void setTxnrulekeypriority(long txnrulekeypriority) {
		this.txnrulekeypriority = txnrulekeypriority;
	}

	@Column(name = "TXNRULEKEYCOMPARISION", nullable = false)
	public String getTxnrulekeycomparision() {
		return this.txnrulekeycomparision;
	}

	public void setTxnrulekeycomparision(String txnrulekeycomparision) {
		this.txnrulekeycomparision = txnrulekeycomparision;
	}

}
