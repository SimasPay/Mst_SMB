package com.mfino.domain;

// Generated Sep 27, 2016 5:23:21 PM by Hibernate Tools 3.4.0.CR1

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Clob;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;
import com.mfino.hibernate.Timestamp;

/**
 * BulkLop generated by hbm2java
 */
@Entity
@Table(name = "BULK_LOP")
public class BulkLOP extends Base implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	private SubscriberMDN subscriberMdn;
	private DistributionChainTemp distributionChainTemp;
	private Merchant merchant;
	private Company company;
	private DistributionChainLvl distributionChainLvl;
	private Long levelpermissions;
	private String girorefid;
	private String transferdate;
	private BigDecimal actualamountpaid;
	private BigDecimal amountdistributed;
	private String status;
	private String distributedby;
	private Timestamp distributetime;
	private String approvedby;
	private Timestamp approvaltime;
	private String rejectedby;
	private Timestamp rejecttime;
	private String lopcomment;
	private Clob filedata;
	private long sourceapplication;
	private Set<LetterOfPurchase> letterOfPurchases = new HashSet<LetterOfPurchase>(
			0);

	public BulkLOP() {
	}

	public BulkLOP(BigDecimal id, SubscriberMDN subscriberMdn,
			Merchant merchant, Company company, Timestamp lastupdatetime,
			String updatedby, Timestamp createtime, String createdby,
			long sourceapplication) {
		this.id = id;
		this.subscriberMdn = subscriberMdn;
		this.merchant = merchant;
		this.company = company;
		this.lastupdatetime = lastupdatetime;
		this.updatedby = updatedby;
		this.createtime = createtime;
		this.createdby = createdby;
		this.sourceapplication = sourceapplication;
	}

	public BulkLOP(BigDecimal id, SubscriberMDN subscriberMdn,
			DistributionChainTemp distributionChainTemp, Merchant merchant,
			Company company, DistributionChainLvl distributionChainLvl,
			Timestamp lastupdatetime, String updatedby,
			Timestamp createtime, String createdby, Long levelpermissions,
			String girorefid, String transferdate, BigDecimal actualamountpaid,
			BigDecimal amountdistributed, String status, String distributedby,
			Timestamp distributetime, String approvedby,
			Timestamp approvaltime, String rejectedby,
			Timestamp rejecttime, String lopcomment, Clob filedata,
			long sourceapplication, Set<LetterOfPurchase> letterOfPurchases) {
		this.id = id;
		this.subscriberMdn = subscriberMdn;
		this.distributionChainTemp = distributionChainTemp;
		this.merchant = merchant;
		this.company = company;
		this.distributionChainLvl = distributionChainLvl;
		this.lastupdatetime = lastupdatetime;
		this.updatedby = updatedby;
		this.createtime = createtime;
		this.createdby = createdby;
		this.levelpermissions = levelpermissions;
		this.girorefid = girorefid;
		this.transferdate = transferdate;
		this.actualamountpaid = actualamountpaid;
		this.amountdistributed = amountdistributed;
		this.status = status;
		this.distributedby = distributedby;
		this.distributetime = distributetime;
		this.approvedby = approvedby;
		this.approvaltime = approvaltime;
		this.rejectedby = rejectedby;
		this.rejecttime = rejecttime;
		this.lopcomment = lopcomment;
		this.filedata = filedata;
		this.sourceapplication = sourceapplication;
		this.letterOfPurchases = letterOfPurchases;
	}


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MDNID", nullable = false)
	public SubscriberMDN getSubscriberMdn() {
		return this.subscriberMdn;
	}

	public void setSubscriberMdn(SubscriberMDN subscriberMdn) {
		this.subscriberMdn = subscriberMdn;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DCTID")
	public DistributionChainTemp getDistributionChainTemp() {
		return this.distributionChainTemp;
	}

	public void setDistributionChainTemp(
			DistributionChainTemp distributionChainTemp) {
		this.distributionChainTemp = distributionChainTemp;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MERCHANTID", nullable = false)
	public Merchant getMerchant() {
		return this.merchant;
	}

	public void setMerchant(Merchant merchant) {
		this.merchant = merchant;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COMPANYID", nullable = false)
	public Company getCompany() {
		return this.company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DCTLEVELID")
	public DistributionChainLvl getDistributionChainLvl() {
		return this.distributionChainLvl;
	}

	public void setDistributionChainLvl(
			DistributionChainLvl distributionChainLvl) {
		this.distributionChainLvl = distributionChainLvl;
	}

	
	@Column(name = "LEVELPERMISSIONS", precision = 10, scale = 0)
	public Long getLevelpermissions() {
		return this.levelpermissions;
	}

	public void setLevelpermissions(Long levelpermissions) {
		this.levelpermissions = levelpermissions;
	}

	@Column(name = "GIROREFID", length = 1020)
	public String getGirorefid() {
		return this.girorefid;
	}

	public void setGirorefid(String girorefid) {
		this.girorefid = girorefid;
	}

	@Column(name = "TRANSFERDATE", length = 1020)
	public String getTransferdate() {
		return this.transferdate;
	}

	public void setTransferdate(String transferdate) {
		this.transferdate = transferdate;
	}

	@Column(name = "ACTUALAMOUNTPAID", precision = 25, scale = 4)
	public BigDecimal getActualamountpaid() {
		return this.actualamountpaid;
	}

	public void setActualamountpaid(BigDecimal actualamountpaid) {
		this.actualamountpaid = actualamountpaid;
	}

	@Column(name = "AMOUNTDISTRIBUTED", precision = 25, scale = 4)
	public BigDecimal getAmountdistributed() {
		return this.amountdistributed;
	}

	public void setAmountdistributed(BigDecimal amountdistributed) {
		this.amountdistributed = amountdistributed;
	}

	@Column(name = "STATUS", length = 1020)
	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Column(name = "DISTRIBUTEDBY", length = 1020)
	public String getDistributedby() {
		return this.distributedby;
	}

	public void setDistributedby(String distributedby) {
		this.distributedby = distributedby;
	}

	@Column(name = "DISTRIBUTETIME")
	public Timestamp getDistributetime() {
		return this.distributetime;
	}

	public void setDistributetime(Timestamp distributetime) {
		this.distributetime = distributetime;
	}

	@Column(name = "APPROVEDBY", length = 1020)
	public String getApprovedby() {
		return this.approvedby;
	}

	public void setApprovedby(String approvedby) {
		this.approvedby = approvedby;
	}

	@Column(name = "APPROVALTIME")
	public Timestamp getApprovaltime() {
		return this.approvaltime;
	}

	public void setApprovaltime(Timestamp approvaltime) {
		this.approvaltime = approvaltime;
	}

	@Column(name = "REJECTEDBY", length = 1020)
	public String getRejectedby() {
		return this.rejectedby;
	}

	public void setRejectedby(String rejectedby) {
		this.rejectedby = rejectedby;
	}

	@Column(name = "REJECTTIME")
	public Timestamp getRejecttime() {
		return this.rejecttime;
	}

	public void setRejecttime(Timestamp rejecttime) {
		this.rejecttime = rejecttime;
	}

	@Column(name = "LOPCOMMENT", length = 1020)
	public String getLopcomment() {
		return this.lopcomment;
	}

	public void setLopcomment(String lopcomment) {
		this.lopcomment = lopcomment;
	}

	@Column(name = "FILEDATA")
	public Clob getFiledata() {
		return this.filedata;
	}

	public void setFiledata(Clob filedata) {
		this.filedata = filedata;
	}

	@Column(name = "SOURCEAPPLICATION", nullable = false, precision = 10, scale = 0)
	public long getSourceapplication() {
		return this.sourceapplication;
	}

	public void setSourceapplication(long sourceapplication) {
		this.sourceapplication = sourceapplication;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "bulkLop")
	public Set<LetterOfPurchase> getLetterOfPurchases() {
		return this.letterOfPurchases;
	}

	public void setLetterOfPurchases(Set<LetterOfPurchase> letterOfPurchases) {
		this.letterOfPurchases = letterOfPurchases;
	}

}
