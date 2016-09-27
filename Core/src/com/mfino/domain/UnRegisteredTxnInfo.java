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

/**
 * UnregisteredTxnInfo generated by hbm2java
 */
@Entity
@Table(name = "UNREGISTERED_TXN_INFO")
public class UnregisteredTxnInfo implements java.io.Serializable {

	private BigDecimal id;
	private long version;
	private SubscriberMdn subscriberMdn;
	private ServiceChargeTxnLog serviceChargeTxnLog;
	private FundDefinition fundDefinition;
	private Serializable lastupdatetime;
	private String updatedby;
	private Serializable createtime;
	private String createdby;
	private BigDecimal transferctid;
	private BigDecimal cashoutsctlid;
	private BigDecimal cashoutctid;
	private String digestedpin;
	private Long unregisteredtxnstatus;
	private String transactionname;
	private BigDecimal amount;
	private String failurereason;
	private Long failurereasoncode;
	private String reversalreason;
	private Serializable expirytime;
	private BigDecimal availableamount;
	private String withdrawalmdn;
	private Long withdrawalfailureattempt;
	private String partnercode;
	private Set<FundDistributionInfo> fundDistributionInfos = new HashSet<FundDistributionInfo>(
			0);

	public UnregisteredTxnInfo() {
	}

	public UnregisteredTxnInfo(BigDecimal id,
			ServiceChargeTxnLog serviceChargeTxnLog,
			Serializable lastupdatetime, String updatedby,
			Serializable createtime, String createdby) {
		this.id = id;
		this.serviceChargeTxnLog = serviceChargeTxnLog;
		this.lastupdatetime = lastupdatetime;
		this.updatedby = updatedby;
		this.createtime = createtime;
		this.createdby = createdby;
	}

	public UnregisteredTxnInfo(BigDecimal id, SubscriberMdn subscriberMdn,
			ServiceChargeTxnLog serviceChargeTxnLog,
			FundDefinition fundDefinition, Serializable lastupdatetime,
			String updatedby, Serializable createtime, String createdby,
			BigDecimal transferctid, BigDecimal cashoutsctlid,
			BigDecimal cashoutctid, String digestedpin,
			Long unregisteredtxnstatus, String transactionname,
			BigDecimal amount, String failurereason, Long failurereasoncode,
			String reversalreason, Serializable expirytime,
			BigDecimal availableamount, String withdrawalmdn,
			Long withdrawalfailureattempt, String partnercode,
			Set<FundDistributionInfo> fundDistributionInfos) {
		this.id = id;
		this.subscriberMdn = subscriberMdn;
		this.serviceChargeTxnLog = serviceChargeTxnLog;
		this.fundDefinition = fundDefinition;
		this.lastupdatetime = lastupdatetime;
		this.updatedby = updatedby;
		this.createtime = createtime;
		this.createdby = createdby;
		this.transferctid = transferctid;
		this.cashoutsctlid = cashoutsctlid;
		this.cashoutctid = cashoutctid;
		this.digestedpin = digestedpin;
		this.unregisteredtxnstatus = unregisteredtxnstatus;
		this.transactionname = transactionname;
		this.amount = amount;
		this.failurereason = failurereason;
		this.failurereasoncode = failurereasoncode;
		this.reversalreason = reversalreason;
		this.expirytime = expirytime;
		this.availableamount = availableamount;
		this.withdrawalmdn = withdrawalmdn;
		this.withdrawalfailureattempt = withdrawalfailureattempt;
		this.partnercode = partnercode;
		this.fundDistributionInfos = fundDistributionInfos;
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
	@JoinColumn(name = "MDNID")
	public SubscriberMdn getSubscriberMdn() {
		return this.subscriberMdn;
	}

	public void setSubscriberMdn(SubscriberMdn subscriberMdn) {
		this.subscriberMdn = subscriberMdn;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TRANSFERSCTLID", nullable = false)
	public ServiceChargeTxnLog getServiceChargeTxnLog() {
		return this.serviceChargeTxnLog;
	}

	public void setServiceChargeTxnLog(ServiceChargeTxnLog serviceChargeTxnLog) {
		this.serviceChargeTxnLog = serviceChargeTxnLog;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FUNDDEFINITIONID")
	public FundDefinition getFundDefinition() {
		return this.fundDefinition;
	}

	public void setFundDefinition(FundDefinition fundDefinition) {
		this.fundDefinition = fundDefinition;
	}

	@Column(name = "LASTUPDATETIME", nullable = false)
	public Serializable getLastupdatetime() {
		return this.lastupdatetime;
	}

	public void setLastupdatetime(Serializable lastupdatetime) {
		this.lastupdatetime = lastupdatetime;
	}

	@Column(name = "UPDATEDBY", nullable = false, length = 1020)
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

	@Column(name = "CREATEDBY", nullable = false, length = 1020)
	public String getCreatedby() {
		return this.createdby;
	}

	public void setCreatedby(String createdby) {
		this.createdby = createdby;
	}

	@Column(name = "TRANSFERCTID", scale = 0)
	public BigDecimal getTransferctid() {
		return this.transferctid;
	}

	public void setTransferctid(BigDecimal transferctid) {
		this.transferctid = transferctid;
	}

	@Column(name = "CASHOUTSCTLID", scale = 0)
	public BigDecimal getCashoutsctlid() {
		return this.cashoutsctlid;
	}

	public void setCashoutsctlid(BigDecimal cashoutsctlid) {
		this.cashoutsctlid = cashoutsctlid;
	}

	@Column(name = "CASHOUTCTID", scale = 0)
	public BigDecimal getCashoutctid() {
		return this.cashoutctid;
	}

	public void setCashoutctid(BigDecimal cashoutctid) {
		this.cashoutctid = cashoutctid;
	}

	@Column(name = "DIGESTEDPIN", length = 1020)
	public String getDigestedpin() {
		return this.digestedpin;
	}

	public void setDigestedpin(String digestedpin) {
		this.digestedpin = digestedpin;
	}

	@Column(name = "UNREGISTEREDTXNSTATUS", precision = 10, scale = 0)
	public Long getUnregisteredtxnstatus() {
		return this.unregisteredtxnstatus;
	}

	public void setUnregisteredtxnstatus(Long unregisteredtxnstatus) {
		this.unregisteredtxnstatus = unregisteredtxnstatus;
	}

	@Column(name = "TRANSACTIONNAME", length = 1020)
	public String getTransactionname() {
		return this.transactionname;
	}

	public void setTransactionname(String transactionname) {
		this.transactionname = transactionname;
	}

	@Column(name = "AMOUNT", precision = 25, scale = 4)
	public BigDecimal getAmount() {
		return this.amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	@Column(name = "FAILUREREASON", length = 1020)
	public String getFailurereason() {
		return this.failurereason;
	}

	public void setFailurereason(String failurereason) {
		this.failurereason = failurereason;
	}

	@Column(name = "FAILUREREASONCODE", precision = 10, scale = 0)
	public Long getFailurereasoncode() {
		return this.failurereasoncode;
	}

	public void setFailurereasoncode(Long failurereasoncode) {
		this.failurereasoncode = failurereasoncode;
	}

	@Column(name = "REVERSALREASON")
	public String getReversalreason() {
		return this.reversalreason;
	}

	public void setReversalreason(String reversalreason) {
		this.reversalreason = reversalreason;
	}

	@Column(name = "EXPIRYTIME")
	public Serializable getExpirytime() {
		return this.expirytime;
	}

	public void setExpirytime(Serializable expirytime) {
		this.expirytime = expirytime;
	}

	@Column(name = "AVAILABLEAMOUNT", precision = 25, scale = 4)
	public BigDecimal getAvailableamount() {
		return this.availableamount;
	}

	public void setAvailableamount(BigDecimal availableamount) {
		this.availableamount = availableamount;
	}

	@Column(name = "WITHDRAWALMDN")
	public String getWithdrawalmdn() {
		return this.withdrawalmdn;
	}

	public void setWithdrawalmdn(String withdrawalmdn) {
		this.withdrawalmdn = withdrawalmdn;
	}

	@Column(name = "WITHDRAWALFAILUREATTEMPT", precision = 10, scale = 0)
	public Long getWithdrawalfailureattempt() {
		return this.withdrawalfailureattempt;
	}

	public void setWithdrawalfailureattempt(Long withdrawalfailureattempt) {
		this.withdrawalfailureattempt = withdrawalfailureattempt;
	}

	@Column(name = "PARTNERCODE")
	public String getPartnercode() {
		return this.partnercode;
	}

	public void setPartnercode(String partnercode) {
		this.partnercode = partnercode;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "unregisteredTxnInfo")
	public Set<FundDistributionInfo> getFundDistributionInfos() {
		return this.fundDistributionInfos;
	}

	public void setFundDistributionInfos(
			Set<FundDistributionInfo> fundDistributionInfos) {
		this.fundDistributionInfos = fundDistributionInfos;
	}

}
