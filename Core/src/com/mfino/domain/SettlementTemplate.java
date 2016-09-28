package com.mfino.domain;

// Generated Sep 27, 2016 5:23:21 PM by Hibernate Tools 3.4.0.CR1

import java.io.Serializable;
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
import com.mfino.hibernate.Timestamp;

/**
 * SettlementTemplate generated by hbm2java
 */
@Entity
@Table(name = "SETTLEMENT_TEMPLATE")
public class SettlementTemplate extends Base implements java.io.Serializable {

	
	private ScheduleTemplate scheduleTemplateByScheduletemplateid;
	private Partner partner;
	private mFinoServiceProvider mfinoServiceProvider;
	private ScheduleTemplate scheduleTemplateByCutofftime;
	private Pocket pocket;
	private String settlementname;
	private Set<ServiceSettlementCfg> serviceSettlementCfgs = new HashSet<ServiceSettlementCfg>(
			0);

	public SettlementTemplate() {
	}

	public SettlementTemplate(BigDecimal id,
			ScheduleTemplate scheduleTemplateByScheduletemplateid,
			Partner partner, mFinoServiceProvider mfinoServiceProvider,
			Pocket pocket, Timestamp lastupdatetime, String updatedby,
			Timestamp createtime, String createdby, String settlementname) {
		this.id = id;
		this.scheduleTemplateByScheduletemplateid = scheduleTemplateByScheduletemplateid;
		this.partner = partner;
		this.mfinoServiceProvider = mfinoServiceProvider;
		this.pocket = pocket;
		this.lastupdatetime = lastupdatetime;
		this.updatedby = updatedby;
		this.createtime = createtime;
		this.createdby = createdby;
		this.settlementname = settlementname;
	}

	public SettlementTemplate(BigDecimal id,
			ScheduleTemplate scheduleTemplateByScheduletemplateid,
			Partner partner, mFinoServiceProvider mfinoServiceProvider,
			ScheduleTemplate scheduleTemplateByCutofftime, Pocket pocket,
			Timestamp lastupdatetime, String updatedby,
			Timestamp createtime, String createdby, String settlementname,
			Set<ServiceSettlementCfg> serviceSettlementCfgs) {
		this.id = id;
		this.scheduleTemplateByScheduletemplateid = scheduleTemplateByScheduletemplateid;
		this.partner = partner;
		this.mfinoServiceProvider = mfinoServiceProvider;
		this.scheduleTemplateByCutofftime = scheduleTemplateByCutofftime;
		this.pocket = pocket;
		this.lastupdatetime = lastupdatetime;
		this.updatedby = updatedby;
		this.createtime = createtime;
		this.createdby = createdby;
		this.settlementname = settlementname;
		this.serviceSettlementCfgs = serviceSettlementCfgs;
	}

	

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SCHEDULETEMPLATEID", nullable = false)
	public ScheduleTemplate getScheduleTemplateByScheduletemplateid() {
		return this.scheduleTemplateByScheduletemplateid;
	}

	public void setScheduleTemplateByScheduletemplateid(
			ScheduleTemplate scheduleTemplateByScheduletemplateid) {
		this.scheduleTemplateByScheduletemplateid = scheduleTemplateByScheduletemplateid;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PARTNERID", nullable = false)
	public Partner getPartner() {
		return this.partner;
	}

	public void setPartner(Partner partner) {
		this.partner = partner;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MSPID", nullable = false)
	public mFinoServiceProvider getMfinoServiceProvider() {
		return this.mfinoServiceProvider;
	}

	public void setMfinoServiceProvider(
			mFinoServiceProvider mfinoServiceProvider) {
		this.mfinoServiceProvider = mfinoServiceProvider;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CUTOFFTIME")
	public ScheduleTemplate getScheduleTemplateByCutofftime() {
		return this.scheduleTemplateByCutofftime;
	}

	public void setScheduleTemplateByCutofftime(
			ScheduleTemplate scheduleTemplateByCutofftime) {
		this.scheduleTemplateByCutofftime = scheduleTemplateByCutofftime;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SETTLEMENTPOCKET", nullable = false)
	public Pocket getPocket() {
		return this.pocket;
	}

	public void setPocket(Pocket pocket) {
		this.pocket = pocket;
	}

	
	@Column(name = "SETTLEMENTNAME", nullable = false, length = 1020)
	public String getSettlementname() {
		return this.settlementname;
	}

	public void setSettlementname(String settlementname) {
		this.settlementname = settlementname;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "settlementTemplate")
	public Set<ServiceSettlementCfg> getServiceSettlementCfgs() {
		return this.serviceSettlementCfgs;
	}

	public void setServiceSettlementCfgs(
			Set<ServiceSettlementCfg> serviceSettlementCfgs) {
		this.serviceSettlementCfgs = serviceSettlementCfgs;
	}

}
