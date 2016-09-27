package com.mfino.domain;

// Generated Sep 27, 2016 5:23:21 PM by Hibernate Tools 3.4.0.CR1

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Clob;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * PendingTxnsFile generated by hbm2java
 */
@Entity
@Table(name = "PENDING_TXNS_FILE")
public class PendingTxnsFile implements java.io.Serializable {

	private BigDecimal id;
	private long version;
	private Company company;
	private String filename;
	private Clob filedata;
	private String description;
	private Long recordcount;
	private Long totallinecount;
	private Long errorlinecount;
	private long uploadfilestatus;
	private Clob uploadreport;
	private Long recordtype;
	private Serializable fileprocesseddate;
	private Serializable lastupdatetime;
	private String updatedby;
	private Serializable createtime;
	private String createdby;
	private Long resolveas;

	public PendingTxnsFile() {
	}

	public PendingTxnsFile(BigDecimal id, Company company, String filename,
			Clob filedata, long uploadfilestatus, Serializable lastupdatetime,
			String updatedby, Serializable createtime, String createdby) {
		this.id = id;
		this.company = company;
		this.filename = filename;
		this.filedata = filedata;
		this.uploadfilestatus = uploadfilestatus;
		this.lastupdatetime = lastupdatetime;
		this.updatedby = updatedby;
		this.createtime = createtime;
		this.createdby = createdby;
	}

	public PendingTxnsFile(BigDecimal id, Company company, String filename,
			Clob filedata, String description, Long recordcount,
			Long totallinecount, Long errorlinecount, long uploadfilestatus,
			Clob uploadreport, Long recordtype, Serializable fileprocesseddate,
			Serializable lastupdatetime, String updatedby,
			Serializable createtime, String createdby, Long resolveas) {
		this.id = id;
		this.company = company;
		this.filename = filename;
		this.filedata = filedata;
		this.description = description;
		this.recordcount = recordcount;
		this.totallinecount = totallinecount;
		this.errorlinecount = errorlinecount;
		this.uploadfilestatus = uploadfilestatus;
		this.uploadreport = uploadreport;
		this.recordtype = recordtype;
		this.fileprocesseddate = fileprocesseddate;
		this.lastupdatetime = lastupdatetime;
		this.updatedby = updatedby;
		this.createtime = createtime;
		this.createdby = createdby;
		this.resolveas = resolveas;
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
	@JoinColumn(name = "COMPANYID", nullable = false)
	public Company getCompany() {
		return this.company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@Column(name = "FILENAME", nullable = false, length = 1020)
	public String getFilename() {
		return this.filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	@Column(name = "FILEDATA", nullable = false)
	public Clob getFiledata() {
		return this.filedata;
	}

	public void setFiledata(Clob filedata) {
		this.filedata = filedata;
	}

	@Column(name = "DESCRIPTION", length = 1020)
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "RECORDCOUNT", precision = 10, scale = 0)
	public Long getRecordcount() {
		return this.recordcount;
	}

	public void setRecordcount(Long recordcount) {
		this.recordcount = recordcount;
	}

	@Column(name = "TOTALLINECOUNT", precision = 10, scale = 0)
	public Long getTotallinecount() {
		return this.totallinecount;
	}

	public void setTotallinecount(Long totallinecount) {
		this.totallinecount = totallinecount;
	}

	@Column(name = "ERRORLINECOUNT", precision = 10, scale = 0)
	public Long getErrorlinecount() {
		return this.errorlinecount;
	}

	public void setErrorlinecount(Long errorlinecount) {
		this.errorlinecount = errorlinecount;
	}

	@Column(name = "UPLOADFILESTATUS", nullable = false, precision = 10, scale = 0)
	public long getUploadfilestatus() {
		return this.uploadfilestatus;
	}

	public void setUploadfilestatus(long uploadfilestatus) {
		this.uploadfilestatus = uploadfilestatus;
	}

	@Column(name = "UPLOADREPORT")
	public Clob getUploadreport() {
		return this.uploadreport;
	}

	public void setUploadreport(Clob uploadreport) {
		this.uploadreport = uploadreport;
	}

	@Column(name = "RECORDTYPE", precision = 10, scale = 0)
	public Long getRecordtype() {
		return this.recordtype;
	}

	public void setRecordtype(Long recordtype) {
		this.recordtype = recordtype;
	}

	@Column(name = "FILEPROCESSEDDATE")
	public Serializable getFileprocesseddate() {
		return this.fileprocesseddate;
	}

	public void setFileprocesseddate(Serializable fileprocesseddate) {
		this.fileprocesseddate = fileprocesseddate;
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

	@Column(name = "RESOLVEAS", precision = 10, scale = 0)
	public Long getResolveas() {
		return this.resolveas;
	}

	public void setResolveas(Long resolveas) {
		this.resolveas = resolveas;
	}

}
