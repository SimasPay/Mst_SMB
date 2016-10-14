package com.mfino.domain;

// Generated Sep 27, 2016 5:23:21 PM by Hibernate Tools 3.4.0.CR1

import java.sql.Clob;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * OfflineReport generated by hbm2java
 */
@Entity
@Table(name = "OFFLINE_REPORT")
public class OfflineReport extends Base implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	public static final String FieldName_ReportClass = "reportclass";
	public static final String FieldName_ReportName = "name";
	public static final String FieldName_IsOnlineReport = "isonlinereport";
	
	private String name;
	private Clob reportsql;
	private String reportclass;
	private Short triggerenable;
	private Short isdaily;
	private Short ismonthly;
	private Short isonlinereport;
	private Set<OfflineReportReceiver> offlineReportReceivers = new HashSet<OfflineReportReceiver>(
			0);
	private Set<OfflineReportCompany> offlineReportCompanies = new HashSet<OfflineReportCompany>(
			0);

	public OfflineReport() {
	}

	
	
	@Column(name = "NAME", nullable = false, length = 1020)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "REPORTSQL")
	public Clob getReportsql() {
		return this.reportsql;
	}

	public void setReportsql(Clob reportsql) {
		this.reportsql = reportsql;
	}

	@Column(name = "REPORTCLASS", length = 1020)
	public String getReportclass() {
		return this.reportclass;
	}

	public void setReportclass(String reportclass) {
		this.reportclass = reportclass;
	}

	@Column(name = "TRIGGERENABLE", precision = 3, scale = 0)
	public Short getTriggerenable() {
		return this.triggerenable;
	}

	public void setTriggerenable(Short triggerenable) {
		this.triggerenable = triggerenable;
	}

	@Column(name = "ISDAILY", precision = 3, scale = 0)
	public Short getIsdaily() {
		return this.isdaily;
	}

	public void setIsdaily(Short isdaily) {
		this.isdaily = isdaily;
	}

	@Column(name = "ISMONTHLY", precision = 3, scale = 0)
	public Short getIsmonthly() {
		return this.ismonthly;
	}

	public void setIsmonthly(Short ismonthly) {
		this.ismonthly = ismonthly;
	}

	@Column(name = "ISONLINEREPORT", precision = 3, scale = 0)
	public Short getIsonlinereport() {
		return this.isonlinereport;
	}

	public void setIsonlinereport(Short isonlinereport) {
		this.isonlinereport = isonlinereport;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "offlineReport")
	public Set<OfflineReportReceiver> getOfflineReportReceivers() {
		return this.offlineReportReceivers;
	}

	public void setOfflineReportReceivers(
			Set<OfflineReportReceiver> offlineReportReceivers) {
		this.offlineReportReceivers = offlineReportReceivers;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "offlineReport")
	public Set<OfflineReportCompany> getOfflineReportCompanies() {
		return this.offlineReportCompanies;
	}

	public void setOfflineReportCompanies(
			Set<OfflineReportCompany> offlineReportCompanies) {
		this.offlineReportCompanies = offlineReportCompanies;
	}

}
