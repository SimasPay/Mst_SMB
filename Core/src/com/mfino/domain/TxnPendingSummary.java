package com.mfino.domain;

// Generated Sep 27, 2016 5:23:21 PM by Hibernate Tools 3.4.0.CR1

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import com.mfino.hibernate.Timestamp;

/**
 * TxnPendingSummary generated by hbm2java
 */
@Entity
@Table(name = "TXN_PENDING_SUMMARY")
public class TxnPendingSummary extends Base implements java.io.Serializable {

	
	private BigDecimal sctlid;
	private Long csraction;
	private Timestamp csractiontime;
	private BigDecimal csruserid;
	private String csrusername;
	private String csrcomment;

	public TxnPendingSummary() {
	}

	public TxnPendingSummary(BigDecimal id, Timestamp lastupdatetime,
			String updatedby, Timestamp createtime, String createdby,
			BigDecimal sctlid) {
		this.id = id;
		this.lastupdatetime = lastupdatetime;
		this.updatedby = updatedby;
		this.createtime = createtime;
		this.createdby = createdby;
		this.sctlid = sctlid;
	}

	public TxnPendingSummary(BigDecimal id, Timestamp lastupdatetime,
			String updatedby, Timestamp createtime, String createdby,
			BigDecimal sctlid, Long csraction, Timestamp csractiontime,
			BigDecimal csruserid, String csrusername, String csrcomment) {
		this.id = id;
		this.lastupdatetime = lastupdatetime;
		this.updatedby = updatedby;
		this.createtime = createtime;
		this.createdby = createdby;
		this.sctlid = sctlid;
		this.csraction = csraction;
		this.csractiontime = csractiontime;
		this.csruserid = csruserid;
		this.csrusername = csrusername;
		this.csrcomment = csrcomment;
	}

	
	@Column(name = "SCTLID", nullable = false, scale = 0)
	public BigDecimal getSctlid() {
		return this.sctlid;
	}

	public void setSctlid(BigDecimal sctlid) {
		this.sctlid = sctlid;
	}

	@Column(name = "CSRACTION", precision = 10, scale = 0)
	public Long getCsraction() {
		return this.csraction;
	}

	public void setCsraction(Long csraction) {
		this.csraction = csraction;
	}

	@Column(name = "CSRACTIONTIME")
	public Timestamp getCsractiontime() {
		return this.csractiontime;
	}

	public void setCsractiontime(Timestamp csractiontime) {
		this.csractiontime = csractiontime;
	}

	@Column(name = "CSRUSERID", scale = 0)
	public BigDecimal getCsruserid() {
		return this.csruserid;
	}

	public void setCsruserid(BigDecimal csruserid) {
		this.csruserid = csruserid;
	}

	@Column(name = "CSRUSERNAME", length = 1020)
	public String getCsrusername() {
		return this.csrusername;
	}

	public void setCsrusername(String csrusername) {
		this.csrusername = csrusername;
	}

	@Column(name = "CSRCOMMENT", length = 1020)
	public String getCsrcomment() {
		return this.csrcomment;
	}

	public void setCsrcomment(String csrcomment) {
		this.csrcomment = csrcomment;
	}

}
