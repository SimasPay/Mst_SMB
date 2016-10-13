package com.mfino.domain;

// Generated Sep 27, 2016 5:23:21 PM by Hibernate Tools 3.4.0.CR1

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.mfino.hibernate.Timestamp;

/**
 * TransactionChargeLog generated by hbm2java
 */
@Entity
@Table(name = "TRANSACTION_CHARGE_LOG")
public class TransactionChargeLog extends Base implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	public static final String FieldName_ServiceChargeTransactionLogID = "servicechargetransactionlogid";
	public static final String FieldName_TransactionCharge = "transactionCharge";
	private TransactionCharge transactionCharge;
	private MfinoServiceProvider mfinoServiceProvider;
	private BigDecimal servicechargetransactionlogid;
	private BigDecimal calculatedcharge;

	public TransactionChargeLog() {
	}

	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TRANSACTIONCHARGEID", nullable = false)
	public TransactionCharge getTransactionCharge() {
		return this.transactionCharge;
	}

	public void setTransactionCharge(TransactionCharge transactionCharge) {
		this.transactionCharge = transactionCharge;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MSPID")
	public MfinoServiceProvider getMfinoServiceProvider() {
		return this.mfinoServiceProvider;
	}

	public void setMfinoServiceProvider(
			MfinoServiceProvider mfinoServiceProvider) {
		this.mfinoServiceProvider = mfinoServiceProvider;
	}

	

	@Column(name = "SERVICECHARGETRANSACTIONLOGID", nullable = false, scale = 0)
	public BigDecimal getServicechargetransactionlogid() {
		return this.servicechargetransactionlogid;
	}

	public void setServicechargetransactionlogid(
			BigDecimal servicechargetransactionlogid) {
		this.servicechargetransactionlogid = servicechargetransactionlogid;
	}

	@Column(name = "CALCULATEDCHARGE", nullable = false, precision = 25, scale = 4)
	public BigDecimal getCalculatedcharge() {
		return this.calculatedcharge;
	}

	public void setCalculatedcharge(BigDecimal calculatedcharge) {
		this.calculatedcharge = calculatedcharge;
	}

}
