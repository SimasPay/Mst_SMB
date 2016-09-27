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
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

/**
 * BulkUpload generated by hbm2java
 */
@Entity
@Table(name = "BULK_UPLOAD", uniqueConstraints = @UniqueConstraint(columnNames = {
		"SUBSCRIBERID", "INFILECREATEDATE", "FILETYPE", "TRANSACTIONSCOUNT",
		"TOTALAMOUNT", "VERIFICATIONCHECKSUM" }))
public class BulkUpload implements java.io.Serializable {

	private BigDecimal id;
	private long version;
	private MfinoUser mfinoUser;
	private Subscriber subscriber;
	private MfinoServiceProvider mfinoServiceProvider;
	private Company company;
	private Pocket pocket;
	private Serializable lastupdatetime;
	private String updatedby;
	private Serializable createtime;
	private String createdby;
	private String description;
	private BigDecimal mdnid;
	private String username;
	private String mdn;
	private String infilename;
	private Clob infiledata;
	private String infilecreatedate;
	private String outfilename;
	private Clob outfiledata;
	private String reportfilename;
	private Clob reportfiledata;
	private long filetype;
	private long deliverystatus;
	private Serializable deliverydate;
	private Long failedtransactionscount;
	private long transactionscount;
	private BigDecimal totalamount;
	private BigDecimal successamount;
	private BigDecimal verificationchecksum;
	private String digitalsignature;
	private Long fileerror;
	private Long processid;
	private String webclientip;
	private Long bankuploadtrycounter;
	private Serializable bankuploadlasttrydate;
	private String pin;
	private Serializable paymentdate;
	private String approvercomments;
	private BigDecimal servicechargetransactionlogid;
	private String qrtzjobid;
	private String failurereason;
	private BigDecimal reversesctlid;
	private BigDecimal revertamount;
	private String name;

	public BulkUpload() {
	}

	public BulkUpload(BigDecimal id, MfinoUser mfinoUser,
			MfinoServiceProvider mfinoServiceProvider, Company company,
			Serializable lastupdatetime, String updatedby,
			Serializable createtime, String createdby, String description,
			String username, String mdn, String infilename, Clob infiledata,
			String infilecreatedate, long filetype, long deliverystatus,
			long transactionscount, BigDecimal totalamount) {
		this.id = id;
		this.mfinoUser = mfinoUser;
		this.mfinoServiceProvider = mfinoServiceProvider;
		this.company = company;
		this.lastupdatetime = lastupdatetime;
		this.updatedby = updatedby;
		this.createtime = createtime;
		this.createdby = createdby;
		this.description = description;
		this.username = username;
		this.mdn = mdn;
		this.infilename = infilename;
		this.infiledata = infiledata;
		this.infilecreatedate = infilecreatedate;
		this.filetype = filetype;
		this.deliverystatus = deliverystatus;
		this.transactionscount = transactionscount;
		this.totalamount = totalamount;
	}

	public BulkUpload(BigDecimal id, MfinoUser mfinoUser,
			Subscriber subscriber, MfinoServiceProvider mfinoServiceProvider,
			Company company, Pocket pocket, Serializable lastupdatetime,
			String updatedby, Serializable createtime, String createdby,
			String description, BigDecimal mdnid, String username, String mdn,
			String infilename, Clob infiledata, String infilecreatedate,
			String outfilename, Clob outfiledata, String reportfilename,
			Clob reportfiledata, long filetype, long deliverystatus,
			Serializable deliverydate, Long failedtransactionscount,
			long transactionscount, BigDecimal totalamount,
			BigDecimal successamount, BigDecimal verificationchecksum,
			String digitalsignature, Long fileerror, Long processid,
			String webclientip, Long bankuploadtrycounter,
			Serializable bankuploadlasttrydate, String pin,
			Serializable paymentdate, String approvercomments,
			BigDecimal servicechargetransactionlogid, String qrtzjobid,
			String failurereason, BigDecimal reversesctlid,
			BigDecimal revertamount, String name) {
		this.id = id;
		this.mfinoUser = mfinoUser;
		this.subscriber = subscriber;
		this.mfinoServiceProvider = mfinoServiceProvider;
		this.company = company;
		this.pocket = pocket;
		this.lastupdatetime = lastupdatetime;
		this.updatedby = updatedby;
		this.createtime = createtime;
		this.createdby = createdby;
		this.description = description;
		this.mdnid = mdnid;
		this.username = username;
		this.mdn = mdn;
		this.infilename = infilename;
		this.infiledata = infiledata;
		this.infilecreatedate = infilecreatedate;
		this.outfilename = outfilename;
		this.outfiledata = outfiledata;
		this.reportfilename = reportfilename;
		this.reportfiledata = reportfiledata;
		this.filetype = filetype;
		this.deliverystatus = deliverystatus;
		this.deliverydate = deliverydate;
		this.failedtransactionscount = failedtransactionscount;
		this.transactionscount = transactionscount;
		this.totalamount = totalamount;
		this.successamount = successamount;
		this.verificationchecksum = verificationchecksum;
		this.digitalsignature = digitalsignature;
		this.fileerror = fileerror;
		this.processid = processid;
		this.webclientip = webclientip;
		this.bankuploadtrycounter = bankuploadtrycounter;
		this.bankuploadlasttrydate = bankuploadlasttrydate;
		this.pin = pin;
		this.paymentdate = paymentdate;
		this.approvercomments = approvercomments;
		this.servicechargetransactionlogid = servicechargetransactionlogid;
		this.qrtzjobid = qrtzjobid;
		this.failurereason = failurereason;
		this.reversesctlid = reversesctlid;
		this.revertamount = revertamount;
		this.name = name;
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
	@JoinColumn(name = "USERID", nullable = false)
	public MfinoUser getMfinoUser() {
		return this.mfinoUser;
	}

	public void setMfinoUser(MfinoUser mfinoUser) {
		this.mfinoUser = mfinoUser;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SUBSCRIBERID")
	public Subscriber getSubscriber() {
		return this.subscriber;
	}

	public void setSubscriber(Subscriber subscriber) {
		this.subscriber = subscriber;
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
	@JoinColumn(name = "COMPANYID", nullable = false)
	public Company getCompany() {
		return this.company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SOURCEPOCKET")
	public Pocket getPocket() {
		return this.pocket;
	}

	public void setPocket(Pocket pocket) {
		this.pocket = pocket;
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

	@Column(name = "DESCRIPTION", nullable = false, length = 1020)
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "MDNID", scale = 0)
	public BigDecimal getMdnid() {
		return this.mdnid;
	}

	public void setMdnid(BigDecimal mdnid) {
		this.mdnid = mdnid;
	}

	@Column(name = "USERNAME", nullable = false, length = 1020)
	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Column(name = "MDN", nullable = false, length = 1020)
	public String getMdn() {
		return this.mdn;
	}

	public void setMdn(String mdn) {
		this.mdn = mdn;
	}

	@Column(name = "INFILENAME", nullable = false, length = 1020)
	public String getInfilename() {
		return this.infilename;
	}

	public void setInfilename(String infilename) {
		this.infilename = infilename;
	}

	@Column(name = "INFILEDATA", nullable = false)
	public Clob getInfiledata() {
		return this.infiledata;
	}

	public void setInfiledata(Clob infiledata) {
		this.infiledata = infiledata;
	}

	@Column(name = "INFILECREATEDATE", nullable = false, length = 1020)
	public String getInfilecreatedate() {
		return this.infilecreatedate;
	}

	public void setInfilecreatedate(String infilecreatedate) {
		this.infilecreatedate = infilecreatedate;
	}

	@Column(name = "OUTFILENAME", length = 1020)
	public String getOutfilename() {
		return this.outfilename;
	}

	public void setOutfilename(String outfilename) {
		this.outfilename = outfilename;
	}

	@Column(name = "OUTFILEDATA")
	public Clob getOutfiledata() {
		return this.outfiledata;
	}

	public void setOutfiledata(Clob outfiledata) {
		this.outfiledata = outfiledata;
	}

	@Column(name = "REPORTFILENAME", length = 1020)
	public String getReportfilename() {
		return this.reportfilename;
	}

	public void setReportfilename(String reportfilename) {
		this.reportfilename = reportfilename;
	}

	@Column(name = "REPORTFILEDATA")
	public Clob getReportfiledata() {
		return this.reportfiledata;
	}

	public void setReportfiledata(Clob reportfiledata) {
		this.reportfiledata = reportfiledata;
	}

	@Column(name = "FILETYPE", nullable = false, precision = 10, scale = 0)
	public long getFiletype() {
		return this.filetype;
	}

	public void setFiletype(long filetype) {
		this.filetype = filetype;
	}

	@Column(name = "DELIVERYSTATUS", nullable = false, precision = 10, scale = 0)
	public long getDeliverystatus() {
		return this.deliverystatus;
	}

	public void setDeliverystatus(long deliverystatus) {
		this.deliverystatus = deliverystatus;
	}

	@Column(name = "DELIVERYDATE")
	public Serializable getDeliverydate() {
		return this.deliverydate;
	}

	public void setDeliverydate(Serializable deliverydate) {
		this.deliverydate = deliverydate;
	}

	@Column(name = "FAILEDTRANSACTIONSCOUNT", precision = 10, scale = 0)
	public Long getFailedtransactionscount() {
		return this.failedtransactionscount;
	}

	public void setFailedtransactionscount(Long failedtransactionscount) {
		this.failedtransactionscount = failedtransactionscount;
	}

	@Column(name = "TRANSACTIONSCOUNT", nullable = false, precision = 10, scale = 0)
	public long getTransactionscount() {
		return this.transactionscount;
	}

	public void setTransactionscount(long transactionscount) {
		this.transactionscount = transactionscount;
	}

	@Column(name = "TOTALAMOUNT", nullable = false, precision = 25, scale = 4)
	public BigDecimal getTotalamount() {
		return this.totalamount;
	}

	public void setTotalamount(BigDecimal totalamount) {
		this.totalamount = totalamount;
	}

	@Column(name = "SUCCESSAMOUNT", precision = 25, scale = 4)
	public BigDecimal getSuccessamount() {
		return this.successamount;
	}

	public void setSuccessamount(BigDecimal successamount) {
		this.successamount = successamount;
	}

	@Column(name = "VERIFICATIONCHECKSUM", scale = 0)
	public BigDecimal getVerificationchecksum() {
		return this.verificationchecksum;
	}

	public void setVerificationchecksum(BigDecimal verificationchecksum) {
		this.verificationchecksum = verificationchecksum;
	}

	@Column(name = "DIGITALSIGNATURE", length = 1020)
	public String getDigitalsignature() {
		return this.digitalsignature;
	}

	public void setDigitalsignature(String digitalsignature) {
		this.digitalsignature = digitalsignature;
	}

	@Column(name = "FILEERROR", precision = 10, scale = 0)
	public Long getFileerror() {
		return this.fileerror;
	}

	public void setFileerror(Long fileerror) {
		this.fileerror = fileerror;
	}

	@Column(name = "PROCESSID", precision = 10, scale = 0)
	public Long getProcessid() {
		return this.processid;
	}

	public void setProcessid(Long processid) {
		this.processid = processid;
	}

	@Column(name = "WEBCLIENTIP", length = 1020)
	public String getWebclientip() {
		return this.webclientip;
	}

	public void setWebclientip(String webclientip) {
		this.webclientip = webclientip;
	}

	@Column(name = "BANKUPLOADTRYCOUNTER", precision = 10, scale = 0)
	public Long getBankuploadtrycounter() {
		return this.bankuploadtrycounter;
	}

	public void setBankuploadtrycounter(Long bankuploadtrycounter) {
		this.bankuploadtrycounter = bankuploadtrycounter;
	}

	@Column(name = "BANKUPLOADLASTTRYDATE")
	public Serializable getBankuploadlasttrydate() {
		return this.bankuploadlasttrydate;
	}

	public void setBankuploadlasttrydate(Serializable bankuploadlasttrydate) {
		this.bankuploadlasttrydate = bankuploadlasttrydate;
	}

	@Column(name = "PIN", length = 1020)
	public String getPin() {
		return this.pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	@Column(name = "PAYMENTDATE")
	public Serializable getPaymentdate() {
		return this.paymentdate;
	}

	public void setPaymentdate(Serializable paymentdate) {
		this.paymentdate = paymentdate;
	}

	@Column(name = "APPROVERCOMMENTS", length = 1020)
	public String getApprovercomments() {
		return this.approvercomments;
	}

	public void setApprovercomments(String approvercomments) {
		this.approvercomments = approvercomments;
	}

	@Column(name = "SERVICECHARGETRANSACTIONLOGID", scale = 0)
	public BigDecimal getServicechargetransactionlogid() {
		return this.servicechargetransactionlogid;
	}

	public void setServicechargetransactionlogid(
			BigDecimal servicechargetransactionlogid) {
		this.servicechargetransactionlogid = servicechargetransactionlogid;
	}

	@Column(name = "QRTZJOBID", length = 1020)
	public String getQrtzjobid() {
		return this.qrtzjobid;
	}

	public void setQrtzjobid(String qrtzjobid) {
		this.qrtzjobid = qrtzjobid;
	}

	@Column(name = "FAILUREREASON", length = 1020)
	public String getFailurereason() {
		return this.failurereason;
	}

	public void setFailurereason(String failurereason) {
		this.failurereason = failurereason;
	}

	@Column(name = "REVERSESCTLID", scale = 0)
	public BigDecimal getReversesctlid() {
		return this.reversesctlid;
	}

	public void setReversesctlid(BigDecimal reversesctlid) {
		this.reversesctlid = reversesctlid;
	}

	@Column(name = "REVERTAMOUNT", precision = 25, scale = 4)
	public BigDecimal getRevertamount() {
		return this.revertamount;
	}

	public void setRevertamount(BigDecimal revertamount) {
		this.revertamount = revertamount;
	}

	@Column(name = "NAME", length = 50)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
