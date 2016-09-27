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
 * BulkUploadFile generated by hbm2java
 */
@Entity
@Table(name = "BULK_UPLOAD_FILE")
public class BulkUploadFile extends Base implements java.io.Serializable {

	
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
	private Timestamp fileprocesseddate;
	private Set<BulkUploadFileEntry> bulkUploadFileEntries = new HashSet<BulkUploadFileEntry>(
			0);

	public BulkUploadFile() {
	}

	public BulkUploadFile(BigDecimal id, Company company,
			Timestamp lastupdatetime, String updatedby,
			Timestamp createtime, String createdby, String filename,
			long uploadfilestatus) {
		this.id = id;
		this.company = company;
		this.lastupdatetime = lastupdatetime;
		this.updatedby = updatedby;
		this.createtime = createtime;
		this.createdby = createdby;
		this.filename = filename;
		this.uploadfilestatus = uploadfilestatus;
	}

	public BulkUploadFile(BigDecimal id, Company company,
			Timestamp lastupdatetime, String updatedby,
			Timestamp createtime, String createdby, String filename,
			Clob filedata, String description, Long recordcount,
			Long totallinecount, Long errorlinecount, long uploadfilestatus,
			Clob uploadreport, Long recordtype, Timestamp fileprocesseddate,
			Set<BulkUploadFileEntry> bulkUploadFileEntries) {
		this.id = id;
		this.company = company;
		this.lastupdatetime = lastupdatetime;
		this.updatedby = updatedby;
		this.createtime = createtime;
		this.createdby = createdby;
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
		this.bulkUploadFileEntries = bulkUploadFileEntries;
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

	@Column(name = "FILEDATA")
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
	public Timestamp getFileprocesseddate() {
		return this.fileprocesseddate;
	}

	public void setFileprocesseddate(Timestamp fileprocesseddate) {
		this.fileprocesseddate = fileprocesseddate;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "bulkUploadFile")
	public Set<BulkUploadFileEntry> getBulkUploadFileEntries() {
		return this.bulkUploadFileEntries;
	}

	public void setBulkUploadFileEntries(
			Set<BulkUploadFileEntry> bulkUploadFileEntries) {
		this.bulkUploadFileEntries = bulkUploadFileEntries;
	}

}
