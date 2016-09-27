package com.mfino.domain;

// Generated Sep 27, 2016 5:23:21 PM by Hibernate Tools 3.4.0.CR1

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
 * Adjustments generated by hbm2java
 */
@Entity
@Table(name = "ADJUSTMENTS")
public class Adjustments extends Base implements java.io.Serializable {

	
	private Pocket pocketByDestpocketid;
	private ServiceChargeTxnLog serviceChargeTxnLog;
	private Pocket pocketBySourcepocketid;
	private BigDecimal amount;
	private long adjustmentstatus;
	private Timestamp approveorrejecttime;
	private String approvedorrejectedby;
	private String approveorrejectcomment;
	private String appliedby;
	private Timestamp appliedtime;
	private Long adjustmenttype;
	private String description;

	public Adjustments() {
	}

	public Adjustments(BigDecimal id, Pocket pocketByDestpocketid,
			ServiceChargeTxnLog serviceChargeTxnLog,
			Pocket pocketBySourcepocketid, Timestamp lastupdatetime,
			String updatedby, Timestamp createtime, String createdby,
			BigDecimal amount, long adjustmentstatus) {
		this.id = id;
		this.pocketByDestpocketid = pocketByDestpocketid;
		this.serviceChargeTxnLog = serviceChargeTxnLog;
		this.pocketBySourcepocketid = pocketBySourcepocketid;
		this.lastupdatetime = lastupdatetime;
		this.updatedby = updatedby;
		this.createtime = createtime;
		this.createdby = createdby;
		this.amount = amount;
		this.adjustmentstatus = adjustmentstatus;
	}

	public Adjustments(BigDecimal id, Pocket pocketByDestpocketid,
			ServiceChargeTxnLog serviceChargeTxnLog,
			Pocket pocketBySourcepocketid, Timestamp lastupdatetime,
			String updatedby, Timestamp createtime, String createdby,
			BigDecimal amount, long adjustmentstatus,
			Timestamp approveorrejecttime, String approvedorrejectedby,
			String approveorrejectcomment, String appliedby,
			Timestamp appliedtime, Long adjustmenttype, String description) {
		this.id = id;
		this.pocketByDestpocketid = pocketByDestpocketid;
		this.serviceChargeTxnLog = serviceChargeTxnLog;
		this.pocketBySourcepocketid = pocketBySourcepocketid;
		this.lastupdatetime = lastupdatetime;
		this.updatedby = updatedby;
		this.createtime = createtime;
		this.createdby = createdby;
		this.amount = amount;
		this.adjustmentstatus = adjustmentstatus;
		this.approveorrejecttime = approveorrejecttime;
		this.approvedorrejectedby = approvedorrejectedby;
		this.approveorrejectcomment = approveorrejectcomment;
		this.appliedby = appliedby;
		this.appliedtime = appliedtime;
		this.adjustmenttype = adjustmenttype;
		this.description = description;
	}

	

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DESTPOCKETID", nullable = false)
	public Pocket getPocketByDestpocketid() {
		return this.pocketByDestpocketid;
	}

	public void setPocketByDestpocketid(Pocket pocketByDestpocketid) {
		this.pocketByDestpocketid = pocketByDestpocketid;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SCTLID", nullable = false)
	public ServiceChargeTxnLog getServiceChargeTxnLog() {
		return this.serviceChargeTxnLog;
	}

	public void setServiceChargeTxnLog(ServiceChargeTxnLog serviceChargeTxnLog) {
		this.serviceChargeTxnLog = serviceChargeTxnLog;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SOURCEPOCKETID", nullable = false)
	public Pocket getPocketBySourcepocketid() {
		return this.pocketBySourcepocketid;
	}

	public void setPocketBySourcepocketid(Pocket pocketBySourcepocketid) {
		this.pocketBySourcepocketid = pocketBySourcepocketid;
	}

	

	@Column(name = "AMOUNT", nullable = false, precision = 25, scale = 4)
	public BigDecimal getAmount() {
		return this.amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	@Column(name = "ADJUSTMENTSTATUS", nullable = false, precision = 10, scale = 0)
	public long getAdjustmentstatus() {
		return this.adjustmentstatus;
	}

	public void setAdjustmentstatus(long adjustmentstatus) {
		this.adjustmentstatus = adjustmentstatus;
	}

	@Column(name = "APPROVEORREJECTTIME")
	public Timestamp getApproveorrejecttime() {
		return this.approveorrejecttime;
	}

	public void setApproveorrejecttime(Timestamp approveorrejecttime) {
		this.approveorrejecttime = approveorrejecttime;
	}

	@Column(name = "APPROVEDORREJECTEDBY", length = 1020)
	public String getApprovedorrejectedby() {
		return this.approvedorrejectedby;
	}

	public void setApprovedorrejectedby(String approvedorrejectedby) {
		this.approvedorrejectedby = approvedorrejectedby;
	}

	@Column(name = "APPROVEORREJECTCOMMENT", length = 1020)
	public String getApproveorrejectcomment() {
		return this.approveorrejectcomment;
	}

	public void setApproveorrejectcomment(String approveorrejectcomment) {
		this.approveorrejectcomment = approveorrejectcomment;
	}

	@Column(name = "APPLIEDBY", length = 1020)
	public String getAppliedby() {
		return this.appliedby;
	}

	public void setAppliedby(String appliedby) {
		this.appliedby = appliedby;
	}

	@Column(name = "APPLIEDTIME")
	public Timestamp getAppliedtime() {
		return this.appliedtime;
	}

	public void setAppliedtime(Timestamp appliedtime) {
		this.appliedtime = appliedtime;
	}

	@Column(name = "ADJUSTMENTTYPE", precision = 10, scale = 0)
	public Long getAdjustmenttype() {
		return this.adjustmenttype;
	}

	public void setAdjustmenttype(Long adjustmenttype) {
		this.adjustmenttype = adjustmenttype;
	}

	@Column(name = "DESCRIPTION", length = 1020)
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
