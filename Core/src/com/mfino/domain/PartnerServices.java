package com.mfino.domain;

// Generated Sep 27, 2016 5:23:21 PM by Hibernate Tools 3.4.0.CR1

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * PartnerServices generated by hbm2java
 */
@Entity
@Table(name = "PARTNER_SERVICES")
public class PartnerServices extends Base implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	public static final String FieldName_Partner = "partnerByPartnerid";
	public static final String FieldName_PartnerByServiceProviderID = "partnerByServiceproviderid";
	public static final String FieldName_Service = "service";
	public static final String FieldName_DistributionChainTemplate = "distributionChainTemp";
	public static final String FieldName_PartnerByParentID = "partnerByParentid";
	public static final String FieldName_PartnerServiceStatus = "status";
	public static final String FieldName_CollectorPocket = "collectorpocket";
	private Partner partnerByPartnerid;
	private Service service;
	private Partner partnerByParentid;
	private Pocket pocketByDestpocketid;
	private DistributionChainTemp distributionChainTemp;
	private MfinoServiceProvider mfinoServiceProvider;
	private Partner partnerByServiceproviderid;
	private Pocket pocketBySourcepocket;
	private Long pslevel;
	private long status;
	private BigDecimal collectorpocket;
	private Long isservicechargeshare;
	private Set<ServiceSettlementCfg> serviceSettlementCfgs = new HashSet<ServiceSettlementCfg>(
			0);

	public PartnerServices() {
	}

	
	

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PARTNERID", nullable = false)
	public Partner getPartnerByPartnerid() {
		return this.partnerByPartnerid;
	}

	public void setPartnerByPartnerid(Partner partnerByPartnerid) {
		this.partnerByPartnerid = partnerByPartnerid;
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
	@JoinColumn(name = "PARENTID")
	public Partner getPartnerByParentid() {
		return this.partnerByParentid;
	}

	public void setPartnerByParentid(Partner partnerByParentid) {
		this.partnerByParentid = partnerByParentid;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DESTPOCKETID")
	public Pocket getPocketByDestpocketid() {
		return this.pocketByDestpocketid;
	}

	public void setPocketByDestpocketid(Pocket pocketByDestpocketid) {
		this.pocketByDestpocketid = pocketByDestpocketid;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DISTRIBUTIONCHAINTEMPLATEID")
	public DistributionChainTemp getDistributionChainTemp() {
		return this.distributionChainTemp;
	}

	public void setDistributionChainTemp(
			DistributionChainTemp distributionChainTemp) {
		this.distributionChainTemp = distributionChainTemp;
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
	public Partner getPartnerByServiceproviderid() {
		return this.partnerByServiceproviderid;
	}

	public void setPartnerByServiceproviderid(Partner partnerByServiceproviderid) {
		this.partnerByServiceproviderid = partnerByServiceproviderid;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SOURCEPOCKET")
	public Pocket getPocketBySourcepocket() {
		return this.pocketBySourcepocket;
	}

	public void setPocketBySourcepocket(Pocket pocketBySourcepocket) {
		this.pocketBySourcepocket = pocketBySourcepocket;
	}

	

	@Column(name = "PSLEVEL", precision = 10, scale = 0)
	public Long getPslevel() {
		return this.pslevel;
	}

	public void setPslevel(Long pslevel) {
		this.pslevel = pslevel;
	}

	@Column(name = "STATUS", nullable = false, precision = 10, scale = 0)
	public long getStatus() {
		return this.status;
	}

	public void setStatus(long status) {
		this.status = status;
	}

	@Column(name = "COLLECTORPOCKET", scale = 0)
	public BigDecimal getCollectorpocket() {
		return this.collectorpocket;
	}

	public void setCollectorpocket(BigDecimal collectorpocket) {
		this.collectorpocket = collectorpocket;
	}

	@Column(name = "ISSERVICECHARGESHARE", precision = 10, scale = 0)
	public Long getIsservicechargeshare() {
		return this.isservicechargeshare;
	}

	public void setIsservicechargeshare(Long isservicechargeshare) {
		this.isservicechargeshare = isservicechargeshare;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "partnerServices")
	public Set<ServiceSettlementCfg> getServiceSettlementCfgs() {
		return this.serviceSettlementCfgs;
	}

	public void setServiceSettlementCfgs(
			Set<ServiceSettlementCfg> serviceSettlementCfgs) {
		this.serviceSettlementCfgs = serviceSettlementCfgs;
	}

}
