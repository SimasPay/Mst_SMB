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

/**
 * CloseAcctSetlMdn generated by hbm2java
 */
@Entity
@Table(name = "CLOSE_ACCT_SETL_MDN")
public class CloseAcctSetlMdn implements java.io.Serializable {

	private BigDecimal id;
	private long version;
	private SubscriberMdn subscriberMdn;
	private Serializable lastupdatetime;
	private String updatedby;
	private Serializable createtime;
	private String createdby;
	private Short tobankaccount;
	private String settlementmdn;
	private String settlementaccountnumber;
	private Long approvalstate;
	private Serializable approveorrejecttime;
	private String approvedorrejectedby;
	private String approveorrejectcomment;

	public CloseAcctSetlMdn() {
	}

	public CloseAcctSetlMdn(BigDecimal id, SubscriberMdn subscriberMdn,
			Serializable lastupdatetime, String updatedby,
			Serializable createtime, String createdby) {
		this.id = id;
		this.subscriberMdn = subscriberMdn;
		this.lastupdatetime = lastupdatetime;
		this.updatedby = updatedby;
		this.createtime = createtime;
		this.createdby = createdby;
	}

	public CloseAcctSetlMdn(BigDecimal id, SubscriberMdn subscriberMdn,
			Serializable lastupdatetime, String updatedby,
			Serializable createtime, String createdby, Short tobankaccount,
			String settlementmdn, String settlementaccountnumber,
			Long approvalstate, Serializable approveorrejecttime,
			String approvedorrejectedby, String approveorrejectcomment) {
		this.id = id;
		this.subscriberMdn = subscriberMdn;
		this.lastupdatetime = lastupdatetime;
		this.updatedby = updatedby;
		this.createtime = createtime;
		this.createdby = createdby;
		this.tobankaccount = tobankaccount;
		this.settlementmdn = settlementmdn;
		this.settlementaccountnumber = settlementaccountnumber;
		this.approvalstate = approvalstate;
		this.approveorrejecttime = approveorrejecttime;
		this.approvedorrejectedby = approvedorrejectedby;
		this.approveorrejectcomment = approveorrejectcomment;
	}

	@Id
	@Column(name = "ID", unique = true, nullable = false, scale = 0)
	public BigDecimal getId() {
		return this.id;
	}

	public void setId(BigDecimal id) {
		this.id = id;
	}

	@Version
	@Column(name = "VERSION", nullable = false, precision = 10, scale = 0)
	public long getVersion() {
		return this.version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MDNID", nullable = false)
	public SubscriberMdn getSubscriberMdn() {
		return this.subscriberMdn;
	}

	public void setSubscriberMdn(SubscriberMdn subscriberMdn) {
		this.subscriberMdn = subscriberMdn;
	}

	@Column(name = "LASTUPDATETIME", nullable = false)
	public Serializable getLastupdatetime() {
		return this.lastupdatetime;
	}

	public void setLastupdatetime(Serializable lastupdatetime) {
		this.lastupdatetime = lastupdatetime;
	}

	@Column(name = "UPDATEDBY", nullable = false)
	public String getUpdatedby() {
		return this.updatedby;
	}

	public void setUpdatedby(String updatedby) {
		this.updatedby = updatedby;
	}

	@Column(name = "CREATETIME", nullable = false)
	public Serializable getCreatetime() {
		return this.createtime;
	}

	public void setCreatetime(Serializable createtime) {
		this.createtime = createtime;
	}

	@Column(name = "CREATEDBY", nullable = false)
	public String getCreatedby() {
		return this.createdby;
	}

	public void setCreatedby(String createdby) {
		this.createdby = createdby;
	}

	@Column(name = "TOBANKACCOUNT", precision = 3, scale = 0)
	public Short getTobankaccount() {
		return this.tobankaccount;
	}

	public void setTobankaccount(Short tobankaccount) {
		this.tobankaccount = tobankaccount;
	}

	@Column(name = "SETTLEMENTMDN")
	public String getSettlementmdn() {
		return this.settlementmdn;
	}

	public void setSettlementmdn(String settlementmdn) {
		this.settlementmdn = settlementmdn;
	}

	@Column(name = "SETTLEMENTACCOUNTNUMBER")
	public String getSettlementaccountnumber() {
		return this.settlementaccountnumber;
	}

	public void setSettlementaccountnumber(String settlementaccountnumber) {
		this.settlementaccountnumber = settlementaccountnumber;
	}

	@Column(name = "APPROVALSTATE", precision = 10, scale = 0)
	public Long getApprovalstate() {
		return this.approvalstate;
	}

	public void setApprovalstate(Long approvalstate) {
		this.approvalstate = approvalstate;
	}

	@Column(name = "APPROVEORREJECTTIME")
	public Serializable getApproveorrejecttime() {
		return this.approveorrejecttime;
	}

	public void setApproveorrejecttime(Serializable approveorrejecttime) {
		this.approveorrejecttime = approveorrejecttime;
	}

	@Column(name = "APPROVEDORREJECTEDBY")
	public String getApprovedorrejectedby() {
		return this.approvedorrejectedby;
	}

	public void setApprovedorrejectedby(String approvedorrejectedby) {
		this.approvedorrejectedby = approvedorrejectedby;
	}

	@Column(name = "APPROVEORREJECTCOMMENT")
	public String getApproveorrejectcomment() {
		return this.approveorrejectcomment;
	}

	public void setApproveorrejectcomment(String approveorrejectcomment) {
		this.approveorrejectcomment = approveorrejectcomment;
	}

}
