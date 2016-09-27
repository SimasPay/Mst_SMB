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
import com.mfino.hibernate.Timestamp;

/**
 * OfflineReportCompany generated by hbm2java
 */
@Entity
@Table(name = "OFFLINE_REPORT_COMPANY")
public class OfflineReportCompany extends Base implements java.io.Serializable {

	
	private OfflineReport offlineReport;
	private Company company;

	public OfflineReportCompany() {
	}

	public OfflineReportCompany(BigDecimal id, OfflineReport offlineReport,
			Timestamp lastupdatetime, String updatedby,
			Timestamp createtime, String createdby) {
		this.id = id;
		this.offlineReport = offlineReport;
		this.lastupdatetime = lastupdatetime;
		this.updatedby = updatedby;
		this.createtime = createtime;
		this.createdby = createdby;
	}

	public OfflineReportCompany(BigDecimal id, OfflineReport offlineReport,
			Company company, Timestamp lastupdatetime, String updatedby,
			Timestamp createtime, String createdby) {
		this.id = id;
		this.offlineReport = offlineReport;
		this.company = company;
		this.lastupdatetime = lastupdatetime;
		this.updatedby = updatedby;
		this.createtime = createtime;
		this.createdby = createdby;
	}

	

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "REPORTID", nullable = false)
	public OfflineReport getOfflineReport() {
		return this.offlineReport;
	}

	public void setOfflineReport(OfflineReport offlineReport) {
		this.offlineReport = offlineReport;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COMPANYID")
	public Company getCompany() {
		return this.company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	
}
