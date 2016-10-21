package com.mfino.domain;

// Generated Sep 27, 2016 5:23:21 PM by Hibernate Tools 3.4.0.CR1

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Type;

import com.mfino.hibernate.Timestamp;

/**
 * BulkUpload generated by hbm2java
 */
@Entity
@Table(name = "BULK_UPLOAD", uniqueConstraints = @UniqueConstraint(columnNames = {
		"SUBSCRIBERID", "INFILECREATEDATE", "FILETYPE", "TRANSACTIONSCOUNT",
		"TOTALAMOUNT", "VERIFICATIONCHECKSUM" }))
public class BulkUpload extends Base implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final String FieldName_Company = "company";
	public static final String FieldName_BulkUploadFileType = "filetype";
	public static final String FieldName_BulkUploadDeliveryStatus = "deliverystatus";
	public static final String FieldName_BulkUploadDeliveryDate = "deliverydate";
	public static final String FieldName_PaymentDate = "paymentdate";
	public static final String FieldName_BulkUploadInFileName = "infilename";
	public static final String FieldName_User = "mfinoUser";
	public static final String FieldName_ReverseSCTLID = "reversesctlid";
	public static final String FieldName_ServiceChargeTransactionLogID = "servicechargetransactionlogid";
	
	private MfinoUser mfinoUser;
	private Subscriber subscriber;
	private MfinoServiceProvider mfinoServiceProvider;
	private Company company;
	private Pocket pocket;
	private String description;
	private Long mdnid;
	private String username;
	private String mdn;
	private String infilename;
	private String infiledata;
	private String infilecreatedate;
	private String outfilename;
	private String outfiledata;
	private String reportfilename;
	private String reportfiledata;
	private long filetype;
	private Integer deliverystatus;
	private Timestamp deliverydate;
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
	private Timestamp bankuploadlasttrydate;
	private String pin;
	private Timestamp paymentdate;
	private String approvercomments;
	private Long servicechargetransactionlogid;
	private String qrtzjobid;
	private String failurereason;
	private Long reversesctlid;
	private BigDecimal revertamount;
	private String name;
	private Long id;

	public BulkUpload() {
	}

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "id_Sequence")
	@SequenceGenerator(name = "id_Sequence", sequenceName = "bulk_upload_ID_SEQ")
	@Column(name = "ID", unique = true, nullable = false, scale = 0)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
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

	

	@Column(name = "DESCRIPTION", nullable = false, length = 1020)
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "MDNID", scale = 0)
	public Long getMdnid() {
		return this.mdnid;
	}

	public void setMdnid(Long mdnid) {
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
	public String getInfiledata() {
		return this.infiledata;
	}

	public void setInfiledata(String infiledata) {
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
	public String getOutfiledata() {
		return this.outfiledata;
	}

	public void setOutfiledata(String outfiledata) {
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
	public String getReportfiledata() {
		return this.reportfiledata;
	}

	public void setReportfiledata(String reportfiledata) {
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
	public Integer getDeliverystatus() {
		return this.deliverystatus;
	}

	public void setDeliverystatus(Integer deliverystatus) {
		this.deliverystatus = deliverystatus;
	}

	@Type(type = "userDefinedTimeStamp")
	@Column(name = "DELIVERYDATE")
	public Timestamp getDeliverydate() {
		return this.deliverydate;
	}

	public void setDeliverydate(Timestamp deliverydate) {
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

	@Type(type = "userDefinedTimeStamp")
	@Column(name = "BANKUPLOADLASTTRYDATE")
	public Timestamp getBankuploadlasttrydate() {
		return this.bankuploadlasttrydate;
	}

	public void setBankuploadlasttrydate(Timestamp bankuploadlasttrydate) {
		this.bankuploadlasttrydate = bankuploadlasttrydate;
	}

	@Column(name = "PIN", length = 1020)
	public String getPin() {
		return this.pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	@Type(type = "userDefinedTimeStamp")
	@Column(name = "PAYMENTDATE")
	public Timestamp getPaymentdate() {
		return this.paymentdate;
	}

	public void setPaymentdate(Timestamp paymentdate) {
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
	public Long getServicechargetransactionlogid() {
		return this.servicechargetransactionlogid;
	}

	public void setServicechargetransactionlogid(
			Long servicechargetransactionlogid) {
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
	public Long getReversesctlid() {
		return this.reversesctlid;
	}

	public void setReversesctlid(Long reversesctlid) {
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
