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

import org.hibernate.annotations.Type;

import com.mfino.hibernate.Timestamp;

/**
 * CreditCardTransaction generated by hbm2java
 */
@Entity
@Table(name = "CREDIT_CARD_TRANSACTION")
public class CreditCardTransaction extends Base implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	public static final String FieldName_CCFailureReason = "ccfailurereason";
	public static final String FieldName_MDN = "mdn";
	public static final String FieldName_TransactionID = "transactionid";
	public static final String FieldName_AuthID = "authid";
	public static final String FieldName_BankReference = "bankreference";
	public static final String FieldName_Company = "company";
	public static final String FieldName_TransStatus = "transstatus";
	public static final String FieldName_Operation = "operation";
	
	private Pocket pocket;
	private Subscriber subscriber;
	private Company company;
	private String description;
	private BigDecimal amount;
	private String paymentmethod;
	private String errcode;
	private String usercode;
	private String transstatus;
	private String currcode;
	private String eui;
	private String transactiondate;
	private Timestamp nsiatranscompletiontime;
	private String transtype;
	private String isblacklisted;
	private Long fraudrisklevel;
	private BigDecimal fraudriskscore;
	private String exceedhighrisk;
	private String cardtype;
	private String cardnopartial;
	private String cardname;
	private String acquirerbank;
	private String bankrescode;
	private String bankresmsg;
	private String authid;
	private String bankreference;
	private String whitelistcard;
	private String operation;
	private String mdn;
	private BigDecimal transactionid;
	private BigDecimal billreferencenumber;
	private Long ccfailurereason;
	private String sessionid;
	private String ccbuckettype;
	private String sourceip;
	private Short paymentgatewayedu;
	private Short isvoid;
	private String voidby;
	private Set<PendingCommodityTransfer> pendingCommodityTransfers = new HashSet<PendingCommodityTransfer>(
			0);
	private Set<CommodityTransfer> commodityTransfers = new HashSet<CommodityTransfer>(
			0);

	public CreditCardTransaction() {
	}

	
	

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "POCKETID", nullable = false)
	public Pocket getPocket() {
		return this.pocket;
	}

	public void setPocket(Pocket pocket) {
		this.pocket = pocket;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SUBSCRIBERID", nullable = false)
	public Subscriber getSubscriber() {
		return this.subscriber;
	}

	public void setSubscriber(Subscriber subscriber) {
		this.subscriber = subscriber;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COMPANYID", nullable = false)
	public Company getCompany() {
		return this.company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	

	@Column(name = "DESCRIPTION", length = 1020)
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "AMOUNT", precision = 25, scale = 4)
	public BigDecimal getAmount() {
		return this.amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	@Column(name = "PAYMENTMETHOD", length = 1020)
	public String getPaymentmethod() {
		return this.paymentmethod;
	}

	public void setPaymentmethod(String paymentmethod) {
		this.paymentmethod = paymentmethod;
	}

	@Column(name = "ERRCODE", length = 1020)
	public String getErrcode() {
		return this.errcode;
	}

	public void setErrcode(String errcode) {
		this.errcode = errcode;
	}

	@Column(name = "USERCODE", length = 1020)
	public String getUsercode() {
		return this.usercode;
	}

	public void setUsercode(String usercode) {
		this.usercode = usercode;
	}

	@Column(name = "TRANSSTATUS", length = 1020)
	public String getTransstatus() {
		return this.transstatus;
	}

	public void setTransstatus(String transstatus) {
		this.transstatus = transstatus;
	}

	@Column(name = "CURRCODE", length = 1020)
	public String getCurrcode() {
		return this.currcode;
	}

	public void setCurrcode(String currcode) {
		this.currcode = currcode;
	}

	@Column(name = "EUI", length = 1020)
	public String getEui() {
		return this.eui;
	}

	public void setEui(String eui) {
		this.eui = eui;
	}

	@Column(name = "TRANSACTIONDATE", length = 1020)
	public String getTransactiondate() {
		return this.transactiondate;
	}

	public void setTransactiondate(String transactiondate) {
		this.transactiondate = transactiondate;
	}

	@Type(type = "userDefinedTimeStamp")
	@Column(name = "NSIATRANSCOMPLETIONTIME")
	public Timestamp getNsiatranscompletiontime() {
		return this.nsiatranscompletiontime;
	}

	public void setNsiatranscompletiontime(Timestamp nsiatranscompletiontime) {
		this.nsiatranscompletiontime = nsiatranscompletiontime;
	}

	@Column(name = "TRANSTYPE", length = 1020)
	public String getTranstype() {
		return this.transtype;
	}

	public void setTranstype(String transtype) {
		this.transtype = transtype;
	}

	@Column(name = "ISBLACKLISTED", length = 1020)
	public String getIsblacklisted() {
		return this.isblacklisted;
	}

	public void setIsblacklisted(String isblacklisted) {
		this.isblacklisted = isblacklisted;
	}

	@Column(name = "FRAUDRISKLEVEL", precision = 10, scale = 0)
	public Long getFraudrisklevel() {
		return this.fraudrisklevel;
	}

	public void setFraudrisklevel(Long fraudrisklevel) {
		this.fraudrisklevel = fraudrisklevel;
	}

	@Column(name = "FRAUDRISKSCORE", precision = 25, scale = 4)
	public BigDecimal getFraudriskscore() {
		return this.fraudriskscore;
	}

	public void setFraudriskscore(BigDecimal fraudriskscore) {
		this.fraudriskscore = fraudriskscore;
	}

	@Column(name = "EXCEEDHIGHRISK", length = 1020)
	public String getExceedhighrisk() {
		return this.exceedhighrisk;
	}

	public void setExceedhighrisk(String exceedhighrisk) {
		this.exceedhighrisk = exceedhighrisk;
	}

	@Column(name = "CARDTYPE", length = 1020)
	public String getCardtype() {
		return this.cardtype;
	}

	public void setCardtype(String cardtype) {
		this.cardtype = cardtype;
	}

	@Column(name = "CARDNOPARTIAL", length = 1020)
	public String getCardnopartial() {
		return this.cardnopartial;
	}

	public void setCardnopartial(String cardnopartial) {
		this.cardnopartial = cardnopartial;
	}

	@Column(name = "CARDNAME", length = 1020)
	public String getCardname() {
		return this.cardname;
	}

	public void setCardname(String cardname) {
		this.cardname = cardname;
	}

	@Column(name = "ACQUIRERBANK", length = 1020)
	public String getAcquirerbank() {
		return this.acquirerbank;
	}

	public void setAcquirerbank(String acquirerbank) {
		this.acquirerbank = acquirerbank;
	}

	@Column(name = "BANKRESCODE", length = 1020)
	public String getBankrescode() {
		return this.bankrescode;
	}

	public void setBankrescode(String bankrescode) {
		this.bankrescode = bankrescode;
	}

	@Column(name = "BANKRESMSG", length = 1020)
	public String getBankresmsg() {
		return this.bankresmsg;
	}

	public void setBankresmsg(String bankresmsg) {
		this.bankresmsg = bankresmsg;
	}

	@Column(name = "AUTHID", length = 1020)
	public String getAuthid() {
		return this.authid;
	}

	public void setAuthid(String authid) {
		this.authid = authid;
	}

	@Column(name = "BANKREFERENCE", length = 1020)
	public String getBankreference() {
		return this.bankreference;
	}

	public void setBankreference(String bankreference) {
		this.bankreference = bankreference;
	}

	@Column(name = "WHITELISTCARD", length = 1020)
	public String getWhitelistcard() {
		return this.whitelistcard;
	}

	public void setWhitelistcard(String whitelistcard) {
		this.whitelistcard = whitelistcard;
	}

	@Column(name = "OPERATION", length = 1020)
	public String getOperation() {
		return this.operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	@Column(name = "MDN", length = 1020)
	public String getMdn() {
		return this.mdn;
	}

	public void setMdn(String mdn) {
		this.mdn = mdn;
	}

	@Column(name = "TRANSACTIONID", scale = 0)
	public BigDecimal getTransactionid() {
		return this.transactionid;
	}

	public void setTransactionid(BigDecimal transactionid) {
		this.transactionid = transactionid;
	}

	@Column(name = "BILLREFERENCENUMBER", scale = 0)
	public BigDecimal getBillreferencenumber() {
		return this.billreferencenumber;
	}

	public void setBillreferencenumber(BigDecimal billreferencenumber) {
		this.billreferencenumber = billreferencenumber;
	}

	@Column(name = "CCFAILUREREASON", precision = 10, scale = 0)
	public Long getCcfailurereason() {
		return this.ccfailurereason;
	}

	public void setCcfailurereason(Long ccfailurereason) {
		this.ccfailurereason = ccfailurereason;
	}

	@Column(name = "SESSIONID", length = 1020)
	public String getSessionid() {
		return this.sessionid;
	}

	public void setSessionid(String sessionid) {
		this.sessionid = sessionid;
	}

	@Column(name = "CCBUCKETTYPE", length = 1020)
	public String getCcbuckettype() {
		return this.ccbuckettype;
	}

	public void setCcbuckettype(String ccbuckettype) {
		this.ccbuckettype = ccbuckettype;
	}

	@Column(name = "SOURCEIP", length = 1020)
	public String getSourceip() {
		return this.sourceip;
	}

	public void setSourceip(String sourceip) {
		this.sourceip = sourceip;
	}

	@Column(name = "PAYMENTGATEWAYEDU", precision = 3, scale = 0)
	public Short getPaymentgatewayedu() {
		return this.paymentgatewayedu;
	}

	public void setPaymentgatewayedu(Short paymentgatewayedu) {
		this.paymentgatewayedu = paymentgatewayedu;
	}

	@Column(name = "ISVOID", precision = 3, scale = 0)
	public Short getIsvoid() {
		return this.isvoid;
	}

	public void setIsvoid(Short isvoid) {
		this.isvoid = isvoid;
	}

	@Column(name = "VOIDBY", length = 1020)
	public String getVoidby() {
		return this.voidby;
	}

	public void setVoidby(String voidby) {
		this.voidby = voidby;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "creditCardTransaction")
	public Set<PendingCommodityTransfer> getPendingCommodityTransfers() {
		return this.pendingCommodityTransfers;
	}

	public void setPendingCommodityTransfers(
			Set<PendingCommodityTransfer> pendingCommodityTransfers) {
		this.pendingCommodityTransfers = pendingCommodityTransfers;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "creditCardTransaction")
	public Set<CommodityTransfer> getCommodityTransfers() {
		return this.commodityTransfers;
	}

	public void setCommodityTransfers(Set<CommodityTransfer> commodityTransfers) {
		this.commodityTransfers = commodityTransfers;
	}

}
