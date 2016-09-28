package com.mfino.domain;

// Generated Sep 27, 2016 5:23:21 PM by Hibernate Tools 3.4.0.CR1

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.mfino.hibernate.Timestamp;

/**
 * VisafoneTxnGenerator generated by hbm2java
 */
@Entity
@Table(name = "VISAFONE_TXN_GENERATOR")
public class VisafoneTxnGenerator extends Base implements java.io.Serializable {

	
	
	private Timestamp txntimestamp;
	private Long txncount;

	public VisafoneTxnGenerator() {
	}

	public VisafoneTxnGenerator(BigDecimal id, Timestamp lastupdatetime,
			String updatedby, Timestamp createtime, String createdby) {
		this.id = id;
		this.lastupdatetime = lastupdatetime;
		this.updatedby = updatedby;
		this.createtime = createtime;
		this.createdby = createdby;
	}

	public VisafoneTxnGenerator(BigDecimal id, Timestamp lastupdatetime,
			String updatedby, Timestamp createtime, String createdby,
			Timestamp txntimestamp, Long txncount) {
		this.id = id;
		this.lastupdatetime = lastupdatetime;
		this.updatedby = updatedby;
		this.createtime = createtime;
		this.createdby = createdby;
		this.txntimestamp = txntimestamp;
		this.txncount = txncount;
	}

	

	
	
	@Column(name = "TXNTIMESTAMP")
	public Timestamp getTxntimestamp() {
		return this.txntimestamp;
	}

	public void setTxntimestamp(Timestamp txntimestamp) {
		this.txntimestamp = txntimestamp;
	}

	@Column(name = "TXNCOUNT", precision = 10, scale = 0)
	public Long getTxncount() {
		return this.txncount;
	}

	public void setTxncount(Long txncount) {
		this.txncount = txncount;
	}

}
