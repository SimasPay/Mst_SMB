package com.mfino.domain;

// Generated Sep 27, 2016 5:23:21 PM by Hibernate Tools 3.4.0.CR1

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.mfino.hibernate.Timestamp;

/**
 * CashinFirstTime generated by hbm2java
 */
@Entity
@Table(name = "CASHIN_FIRST_TIME")
public class CashinFirstTime extends Base implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	public static final String FieldName_MDN = "mdn";
	
	private BigDecimal mdnid;
	private String mdn;
	private BigDecimal sctlid;
	private BigDecimal transactionamount;

	public CashinFirstTime() {
	}

	public CashinFirstTime(BigDecimal id, Timestamp lastupdatetime,
			String updatedby, Timestamp createtime, String createdby) {
		this.id = id;
		this.lastupdatetime = lastupdatetime;
		this.updatedby = updatedby;
		this.createtime = createtime;
		this.createdby = createdby;
	}

	public CashinFirstTime(BigDecimal id, Timestamp lastupdatetime,
			String updatedby, Timestamp createtime, String createdby,
			BigDecimal mdnid, String mdn, BigDecimal sctlid,
			BigDecimal transactionamount) {
		this.id = id;
		this.lastupdatetime = lastupdatetime;
		this.updatedby = updatedby;
		this.createtime = createtime;
		this.createdby = createdby;
		this.mdnid = mdnid;
		this.mdn = mdn;
		this.sctlid = sctlid;
		this.transactionamount = transactionamount;
	}

	
	@Column(name = "MDNID", scale = 0)
	public BigDecimal getMdnid() {
		return this.mdnid;
	}

	public void setMdnid(BigDecimal mdnid) {
		this.mdnid = mdnid;
	}

	@Column(name = "MDN")
	public String getMdn() {
		return this.mdn;
	}

	public void setMdn(String mdn) {
		this.mdn = mdn;
	}

	@Column(name = "SCTLID", scale = 0)
	public BigDecimal getSctlid() {
		return this.sctlid;
	}

	public void setSctlid(BigDecimal sctlid) {
		this.sctlid = sctlid;
	}

	@Column(name = "TRANSACTIONAMOUNT", precision = 25, scale = 4)
	public BigDecimal getTransactionamount() {
		return this.transactionamount;
	}

	public void setTransactionamount(BigDecimal transactionamount) {
		this.transactionamount = transactionamount;
	}

}
