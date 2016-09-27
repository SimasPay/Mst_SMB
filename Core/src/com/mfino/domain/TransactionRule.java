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
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

/**
 * TransactionRule generated by hbm2java
 */
@Entity
@Table(name = "TRANSACTION_RULE", uniqueConstraints = @UniqueConstraint(columnNames = "NAME"))
public class TransactionRule implements java.io.Serializable {

	private BigDecimal id;
	private long version;
	private ChannelCode channelCode;
	private Service service;
	private KycLevel kycLevelBySourcekyc;
	private MfinoServiceProvider mfinoServiceProvider;
	private Partner partner;
	private TransactionType transactionType;
	private KycLevel kycLevelByDestkyc;
	private Serializable lastupdatetime;
	private String updatedby;
	private Serializable createtime;
	private String createdby;
	private String name;
	private long chargemode;
	private Long sourcetype;
	private Long desttype;
	private Long sourcegroup;
	private Long destinationgroup;
	private Set<TxnRuleAddnInfo> txnRuleAddnInfos = new HashSet<TxnRuleAddnInfo>(
			0);
	private Set<TransactionCharge> transactionCharges = new HashSet<TransactionCharge>(
			0);

	public TransactionRule() {
	}

	public TransactionRule(BigDecimal id, ChannelCode channelCode,
			Service service, MfinoServiceProvider mfinoServiceProvider,
			Partner partner, TransactionType transactionType,
			Serializable lastupdatetime, String updatedby,
			Serializable createtime, String createdby, String name,
			long chargemode) {
		this.id = id;
		this.channelCode = channelCode;
		this.service = service;
		this.mfinoServiceProvider = mfinoServiceProvider;
		this.partner = partner;
		this.transactionType = transactionType;
		this.lastupdatetime = lastupdatetime;
		this.updatedby = updatedby;
		this.createtime = createtime;
		this.createdby = createdby;
		this.name = name;
		this.chargemode = chargemode;
	}

	public TransactionRule(BigDecimal id, ChannelCode channelCode,
			Service service, KycLevel kycLevelBySourcekyc,
			MfinoServiceProvider mfinoServiceProvider, Partner partner,
			TransactionType transactionType, KycLevel kycLevelByDestkyc,
			Serializable lastupdatetime, String updatedby,
			Serializable createtime, String createdby, String name,
			long chargemode, Long sourcetype, Long desttype, Long sourcegroup,
			Long destinationgroup, Set<TxnRuleAddnInfo> txnRuleAddnInfos,
			Set<TransactionCharge> transactionCharges) {
		this.id = id;
		this.channelCode = channelCode;
		this.service = service;
		this.kycLevelBySourcekyc = kycLevelBySourcekyc;
		this.mfinoServiceProvider = mfinoServiceProvider;
		this.partner = partner;
		this.transactionType = transactionType;
		this.kycLevelByDestkyc = kycLevelByDestkyc;
		this.lastupdatetime = lastupdatetime;
		this.updatedby = updatedby;
		this.createtime = createtime;
		this.createdby = createdby;
		this.name = name;
		this.chargemode = chargemode;
		this.sourcetype = sourcetype;
		this.desttype = desttype;
		this.sourcegroup = sourcegroup;
		this.destinationgroup = destinationgroup;
		this.txnRuleAddnInfos = txnRuleAddnInfos;
		this.transactionCharges = transactionCharges;
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
	@JoinColumn(name = "CHANNELCODEID", nullable = false)
	public ChannelCode getChannelCode() {
		return this.channelCode;
	}

	public void setChannelCode(ChannelCode channelCode) {
		this.channelCode = channelCode;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SERVICEID", nullable = false)
	public Service getService() {
		return this.service;
	}

	public void setService(Service service) {
		this.service = service;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SOURCEKYC")
	public KycLevel getKycLevelBySourcekyc() {
		return this.kycLevelBySourcekyc;
	}

	public void setKycLevelBySourcekyc(KycLevel kycLevelBySourcekyc) {
		this.kycLevelBySourcekyc = kycLevelBySourcekyc;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MSPID", nullable = false)
	public MfinoServiceProvider getMfinoServiceProvider() {
		return this.mfinoServiceProvider;
	}

	public void setMfinoServiceProvider(
			MfinoServiceProvider mfinoServiceProvider) {
		this.mfinoServiceProvider = mfinoServiceProvider;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SERVICEPROVIDERID", nullable = false)
	public Partner getPartner() {
		return this.partner;
	}

	public void setPartner(Partner partner) {
		this.partner = partner;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TRANSACTIONTYPEID", nullable = false)
	public TransactionType getTransactionType() {
		return this.transactionType;
	}

	public void setTransactionType(TransactionType transactionType) {
		this.transactionType = transactionType;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DESTKYC")
	public KycLevel getKycLevelByDestkyc() {
		return this.kycLevelByDestkyc;
	}

	public void setKycLevelByDestkyc(KycLevel kycLevelByDestkyc) {
		this.kycLevelByDestkyc = kycLevelByDestkyc;
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

	@Column(name = "NAME", unique = true, nullable = false, length = 1020)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "CHARGEMODE", nullable = false, precision = 10, scale = 0)
	public long getChargemode() {
		return this.chargemode;
	}

	public void setChargemode(long chargemode) {
		this.chargemode = chargemode;
	}

	@Column(name = "SOURCETYPE", precision = 10, scale = 0)
	public Long getSourcetype() {
		return this.sourcetype;
	}

	public void setSourcetype(Long sourcetype) {
		this.sourcetype = sourcetype;
	}

	@Column(name = "DESTTYPE", precision = 10, scale = 0)
	public Long getDesttype() {
		return this.desttype;
	}

	public void setDesttype(Long desttype) {
		this.desttype = desttype;
	}

	@Column(name = "SOURCEGROUP", precision = 10, scale = 0)
	public Long getSourcegroup() {
		return this.sourcegroup;
	}

	public void setSourcegroup(Long sourcegroup) {
		this.sourcegroup = sourcegroup;
	}

	@Column(name = "DESTINATIONGROUP", precision = 10, scale = 0)
	public Long getDestinationgroup() {
		return this.destinationgroup;
	}

	public void setDestinationgroup(Long destinationgroup) {
		this.destinationgroup = destinationgroup;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "transactionRule")
	public Set<TxnRuleAddnInfo> getTxnRuleAddnInfos() {
		return this.txnRuleAddnInfos;
	}

	public void setTxnRuleAddnInfos(Set<TxnRuleAddnInfo> txnRuleAddnInfos) {
		this.txnRuleAddnInfos = txnRuleAddnInfos;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "transactionRule")
	public Set<TransactionCharge> getTransactionCharges() {
		return this.transactionCharges;
	}

	public void setTransactionCharges(Set<TransactionCharge> transactionCharges) {
		this.transactionCharges = transactionCharges;
	}

}
