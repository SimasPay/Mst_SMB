package com.mfino.domain;

// Generated Sep 27, 2016 5:23:21 PM by Hibernate Tools 3.4.0.CR1

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import com.mfino.hibernate.Timestamp;

/**
 * TransactionIdentifier generated by hbm2java
 */
@Entity
@Table(name = "TRANSACTION_IDENTIFIER")
public class TransactionIdentifier extends Base implements java.io.Serializable {

	
	private String transactionidentifier;
	private BigDecimal servicechargetransactionlogid;

	public TransactionIdentifier() {
	}

	public TransactionIdentifier(BigDecimal id, Timestamp lastupdatetime,
			String updatedby, Timestamp createtime, String createdby,
			String transactionidentifier) {
		this.id = id;
		this.lastupdatetime = lastupdatetime;
		this.updatedby = updatedby;
		this.createtime = createtime;
		this.createdby = createdby;
		this.transactionidentifier = transactionidentifier;
	}

	public TransactionIdentifier(BigDecimal id, Timestamp lastupdatetime,
			String updatedby, Timestamp createtime, String createdby,
			String transactionidentifier,
			BigDecimal servicechargetransactionlogid) {
		this.id = id;
		this.lastupdatetime = lastupdatetime;
		this.updatedby = updatedby;
		this.createtime = createtime;
		this.createdby = createdby;
		this.transactionidentifier = transactionidentifier;
		this.servicechargetransactionlogid = servicechargetransactionlogid;
	}

	

	@Column(name = "TRANSACTIONIDENTIFIER", nullable = false)
	public String getTransactionidentifier() {
		return this.transactionidentifier;
	}

	public void setTransactionidentifier(String transactionidentifier) {
		this.transactionidentifier = transactionidentifier;
	}

	@Column(name = "SERVICECHARGETRANSACTIONLOGID", scale = 0)
	public BigDecimal getServicechargetransactionlogid() {
		return this.servicechargetransactionlogid;
	}

	public void setServicechargetransactionlogid(
			BigDecimal servicechargetransactionlogid) {
		this.servicechargetransactionlogid = servicechargetransactionlogid;
	}

}
