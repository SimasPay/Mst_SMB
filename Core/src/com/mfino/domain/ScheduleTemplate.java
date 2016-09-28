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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;
import com.mfino.hibernate.Timestamp;

/**
 * ScheduleTemplate generated by hbm2java
 */
@Entity
@Table(name = "SCHEDULE_TEMPLATE")
public class ScheduleTemplate extends Base implements java.io.Serializable {

	
	private String name;
	private String modetype;
	private String dayofweek;
	private String dayofmonth;
	private String cron;
	private BigDecimal mspid;
	private Long timervaluehh;
	private Long timervaluemm;
	private Long month;
	private String description;
	private Set<SettlementTemplate> settlementTemplatesForCutofftime = new HashSet<SettlementTemplate>(
			0);
	private Set<SettlementTemplate> settlementTemplatesForScheduletemplateid = new HashSet<SettlementTemplate>(
			0);

	public ScheduleTemplate() {
	}

	public ScheduleTemplate(BigDecimal id, Timestamp lastupdatetime,
			String updatedby, Timestamp createtime, String createdby,
			String name, String cron, BigDecimal mspid) {
		this.id = id;
		this.lastupdatetime = lastupdatetime;
		this.updatedby = updatedby;
		this.createtime = createtime;
		this.createdby = createdby;
		this.name = name;
		this.cron = cron;
		this.mspid = mspid;
	}

	public ScheduleTemplate(BigDecimal id, Timestamp lastupdatetime,
			String updatedby, Timestamp createtime, String createdby,
			String name, String modetype, String dayofweek, String dayofmonth,
			String cron, BigDecimal mspid, Long timervaluehh,
			Long timervaluemm, Long month, String description,
			Set<SettlementTemplate> settlementTemplatesForCutofftime,
			Set<SettlementTemplate> settlementTemplatesForScheduletemplateid) {
		this.id = id;
		this.lastupdatetime = lastupdatetime;
		this.updatedby = updatedby;
		this.createtime = createtime;
		this.createdby = createdby;
		this.name = name;
		this.modetype = modetype;
		this.dayofweek = dayofweek;
		this.dayofmonth = dayofmonth;
		this.cron = cron;
		this.mspid = mspid;
		this.timervaluehh = timervaluehh;
		this.timervaluemm = timervaluemm;
		this.month = month;
		this.description = description;
		this.settlementTemplatesForCutofftime = settlementTemplatesForCutofftime;
		this.settlementTemplatesForScheduletemplateid = settlementTemplatesForScheduletemplateid;
	}

	
	@Column(name = "NAME", nullable = false)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "MODETYPE")
	public String getModetype() {
		return this.modetype;
	}

	public void setModetype(String modetype) {
		this.modetype = modetype;
	}

	@Column(name = "DAYOFWEEK")
	public String getDayofweek() {
		return this.dayofweek;
	}

	public void setDayofweek(String dayofweek) {
		this.dayofweek = dayofweek;
	}

	@Column(name = "DAYOFMONTH")
	public String getDayofmonth() {
		return this.dayofmonth;
	}

	public void setDayofmonth(String dayofmonth) {
		this.dayofmonth = dayofmonth;
	}

	@Column(name = "CRON", nullable = false)
	public String getCron() {
		return this.cron;
	}

	public void setCron(String cron) {
		this.cron = cron;
	}

	@Column(name = "MSPID", nullable = false, scale = 0)
	public BigDecimal getMspid() {
		return this.mspid;
	}

	public void setMspid(BigDecimal mspid) {
		this.mspid = mspid;
	}

	@Column(name = "TIMERVALUEHH", precision = 10, scale = 0)
	public Long getTimervaluehh() {
		return this.timervaluehh;
	}

	public void setTimervaluehh(Long timervaluehh) {
		this.timervaluehh = timervaluehh;
	}

	@Column(name = "TIMERVALUEMM", precision = 10, scale = 0)
	public Long getTimervaluemm() {
		return this.timervaluemm;
	}

	public void setTimervaluemm(Long timervaluemm) {
		this.timervaluemm = timervaluemm;
	}

	@Column(name = "MONTH", precision = 10, scale = 0)
	public Long getMonth() {
		return this.month;
	}

	public void setMonth(Long month) {
		this.month = month;
	}

	@Column(name = "DESCRIPTION")
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "scheduleTemplateByCutofftime")
	public Set<SettlementTemplate> getSettlementTemplatesForCutofftime() {
		return this.settlementTemplatesForCutofftime;
	}

	public void setSettlementTemplatesForCutofftime(
			Set<SettlementTemplate> settlementTemplatesForCutofftime) {
		this.settlementTemplatesForCutofftime = settlementTemplatesForCutofftime;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "scheduleTemplateByScheduletemplateid")
	public Set<SettlementTemplate> getSettlementTemplatesForScheduletemplateid() {
		return this.settlementTemplatesForScheduletemplateid;
	}

	public void setSettlementTemplatesForScheduletemplateid(
			Set<SettlementTemplate> settlementTemplatesForScheduletemplateid) {
		this.settlementTemplatesForScheduletemplateid = settlementTemplatesForScheduletemplateid;
	}

}
