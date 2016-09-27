package com.mfino.domain;

// Generated Sep 27, 2016 5:23:21 PM by Hibernate Tools 3.4.0.CR1

import java.io.Serializable;
import java.math.BigDecimal;
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
 * LetterOfPurchase generated by hbm2java
 */
@Entity
@Table(name = "LETTER_OF_PURCHASE")
public class LetterOfPurchase extends Base implements java.io.Serializable {

	
	private SubscriberMdn subscriberMdn;
	private TransactionLog transactionLog;
	private DistributionChainTemp distributionChainTemp;
	private Merchant merchant;
	private BulkLOP bulkLop;
	private Company company;
	private DistributionChainLvl distributionChainLvl;
	private Serializable lastupdatetime;
	private String updatedby;
	private Serializable createtime;
	private String createdby;
	private Long levelpermissions;
	private String girorefid;
	private String transferdate;
	private BigDecimal actualamountpaid;
	private BigDecimal amountdistributed;
	private String status;
	private BigDecimal commission;
	private String distributedby;
	private Serializable distributetime;
	private String approvedby;
	private Serializable approvaltime;
	private String rejectedby;
	private Serializable rejecttime;
	private String lopcomment;
	private long sourceapplication;
	private String sourceip;
	private String webclientip;
	private BigDecimal units;
	private Short iscommissionchanged;
	private Set<CommodityTransfer> commodityTransfers = new HashSet<CommodityTransfer>(
			0);
	private Set<PendingCommodityTransfer> pendingCommodityTransfers = new HashSet<PendingCommodityTransfer>(
			0);
	private Set<LOPHistory> lopHistories = new HashSet<LOPHistory>(0);

	public LetterOfPurchase() {
	}

	public LetterOfPurchase(BigDecimal id, SubscriberMdn subscriberMdn,
			TransactionLog transactionLog, Merchant merchant, Company company,
			Serializable lastupdatetime, String updatedby,
			Serializable createtime, String createdby, long sourceapplication) {
		this.id = id;
		this.subscriberMdn = subscriberMdn;
		this.transactionLog = transactionLog;
		this.merchant = merchant;
		this.company = company;
		this.lastupdatetime = lastupdatetime;
		this.updatedby = updatedby;
		this.createtime = createtime;
		this.createdby = createdby;
		this.sourceapplication = sourceapplication;
	}

	public LetterOfPurchase(BigDecimal id, SubscriberMdn subscriberMdn,
			TransactionLog transactionLog,
			DistributionChainTemp distributionChainTemp, Merchant merchant,
			BulkLOP bulkLop, Company company,
			DistributionChainLvl distributionChainLvl,
			Serializable lastupdatetime, String updatedby,
			Serializable createtime, String createdby, Long levelpermissions,
			String girorefid, String transferdate, BigDecimal actualamountpaid,
			BigDecimal amountdistributed, String status, BigDecimal commission,
			String distributedby, Serializable distributetime,
			String approvedby, Serializable approvaltime, String rejectedby,
			Serializable rejecttime, String lopcomment, long sourceapplication,
			String sourceip, String webclientip, BigDecimal units,
			Short iscommissionchanged,
			Set<CommodityTransfer> commodityTransfers,
			Set<PendingCommodityTransfer> pendingCommodityTransfers,
			Set<LOPHistory> lopHistories) {
		this.id = id;
		this.subscriberMdn = subscriberMdn;
		this.transactionLog = transactionLog;
		this.distributionChainTemp = distributionChainTemp;
		this.merchant = merchant;
		this.bulkLop = bulkLop;
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
		this.commission = commission;
		this.distributedby = distributedby;
		this.distributetime = distributetime;
		this.approvedby = approvedby;
		this.approvaltime = approvaltime;
		this.rejectedby = rejectedby;
		this.rejecttime = rejecttime;
		this.lopcomment = lopcomment;
		this.sourceapplication = sourceapplication;
		this.sourceip = sourceip;
		this.webclientip = webclientip;
		this.units = units;
		this.iscommissionchanged = iscommissionchanged;
		this.commodityTransfers = commodityTransfers;
		this.pendingCommodityTransfers = pendingCommodityTransfers;
		this.lopHistories = lopHistories;
	}

	

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MDNID", nullable = false)
	public SubscriberMdn getSubscriberMdn() {
		return this.subscriberMdn;
	}

	public void setSubscriberMdn(SubscriberMdn subscriberMdn) {
		this.subscriberMdn = subscriberMdn;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TRANSACTIONID", nullable = false)
	public TransactionLog getTransactionLog() {
		return this.transactionLog;
	}

	public void setTransactionLog(TransactionLog transactionLog) {
		this.transactionLog = transactionLog;
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
	@JoinColumn(name = "SUBSCRIBERID", nullable = false)
	public Merchant getMerchant() {
		return this.merchant;
	}

	public void setMerchant(Merchant merchant) {
		this.merchant = merchant;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "BULKLOPID")
	public BulkLOP getBulkLop() {
		return this.bulkLop;
	}

	public void setBulkLop(BulkLOP bulkLop) {
		this.bulkLop = bulkLop;
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

	@Column(name = "COMMISSION", precision = 25, scale = 4)
	public BigDecimal getCommission() {
		return this.commission;
	}

	public void setCommission(BigDecimal commission) {
		this.commission = commission;
	}

	@Column(name = "DISTRIBUTEDBY", length = 1020)
	public String getDistributedby() {
		return this.distributedby;
	}

	public void setDistributedby(String distributedby) {
		this.distributedby = distributedby;
	}

	@Column(name = "DISTRIBUTETIME")
	public Serializable getDistributetime() {
		return this.distributetime;
	}

	public void setDistributetime(Serializable distributetime) {
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
	public Serializable getApprovaltime() {
		return this.approvaltime;
	}

	public void setApprovaltime(Serializable approvaltime) {
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
	public Serializable getRejecttime() {
		return this.rejecttime;
	}

	public void setRejecttime(Serializable rejecttime) {
		this.rejecttime = rejecttime;
	}

	@Column(name = "LOPCOMMENT", length = 1020)
	public String getLopcomment() {
		return this.lopcomment;
	}

	public void setLopcomment(String lopcomment) {
		this.lopcomment = lopcomment;
	}

	@Column(name = "SOURCEAPPLICATION", nullable = false, precision = 10, scale = 0)
	public long getSourceapplication() {
		return this.sourceapplication;
	}

	public void setSourceapplication(long sourceapplication) {
		this.sourceapplication = sourceapplication;
	}

	@Column(name = "SOURCEIP", length = 1020)
	public String getSourceip() {
		return this.sourceip;
	}

	public void setSourceip(String sourceip) {
		this.sourceip = sourceip;
	}

	@Column(name = "WEBCLIENTIP", length = 1020)
	public String getWebclientip() {
		return this.webclientip;
	}

	public void setWebclientip(String webclientip) {
		this.webclientip = webclientip;
	}

	@Column(name = "UNITS", scale = 0)
	public BigDecimal getUnits() {
		return this.units;
	}

	public void setUnits(BigDecimal units) {
		this.units = units;
	}

	@Column(name = "ISCOMMISSIONCHANGED", precision = 3, scale = 0)
	public Short getIscommissionchanged() {
		return this.iscommissionchanged;
	}

	public void setIscommissionchanged(Short iscommissionchanged) {
		this.iscommissionchanged = iscommissionchanged;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "letterOfPurchase")
	public Set<CommodityTransfer> getCommodityTransfers() {
		return this.commodityTransfers;
	}

	public void setCommodityTransfers(Set<CommodityTransfer> commodityTransfers) {
		this.commodityTransfers = commodityTransfers;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "letterOfPurchase")
	public Set<PendingCommodityTransfer> getPendingCommodityTransfers() {
		return this.pendingCommodityTransfers;
	}

	public void setPendingCommodityTransfers(
			Set<PendingCommodityTransfer> pendingCommodityTransfers) {
		this.pendingCommodityTransfers = pendingCommodityTransfers;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "letterOfPurchase")
	public Set<LOPHistory> getLopHistories() {
		return this.lopHistories;
	}

	public void setLopHistories(Set<LOPHistory> lopHistories) {
		this.lopHistories = lopHistories;
	}

}
