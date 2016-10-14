package com.mfino.domain;

// Generated Sep 27, 2016 5:23:21 PM by Hibernate Tools 3.4.0.CR1

import java.math.BigDecimal;
import java.sql.Clob;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import com.mfino.hibernate.Timestamp;

/**
 * BillPayments generated by hbm2java
 */
@Entity
@Table(name = "BILL_PAYMENTS")
public class BillPayments extends Base implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	public static final String FieldName_SctlId = "serviceChargeTxnLog";
	public static final String FieldName_BillerCode = "billercode";
	public static final String FieldName_INTxnId = "intxnid";
	public static final String FieldName_IntegrationCode = "integrationcode";
	public static final String FieldName_BillPayStatus = "billpaystatus";
	private ServiceChargeTxnLog serviceChargeTxnLog;
	private String billercode;
	private String invoicenumber;
	private BigDecimal amount;
	private BigDecimal charges;
	private Long billpaystatus;
	private Long responsecode;
	private Long noofretries;
	private String intxnid;
	private String inresponsecode;
	private String partnerbillercode;
	private String integrationcode;
	private String sourcemdn;
	private String originalintxnid;
	private String info1;
	private String info2;
	private String info3;
	private Short chargesincluded;
	private BigDecimal nominalamount;
	private String info4;
	private String info5;
	private String operatormessage;
	private BigDecimal operatorcharges;
	private Clob billdata;
	private Timestamp transfertime;
	private String info6;
	private String info7;
	private String info8;

	public BillPayments() {
	}

	

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SCTLID")
	public ServiceChargeTxnLog getServiceChargeTxnLog() {
		return this.serviceChargeTxnLog;
	}

	public void setServiceChargeTxnLog(ServiceChargeTxnLog serviceChargeTxnLog) {
		this.serviceChargeTxnLog = serviceChargeTxnLog;
	}

	
	@Column(name = "BILLERCODE", length = 1020)
	public String getBillercode() {
		return this.billercode;
	}

	public void setBillercode(String billercode) {
		this.billercode = billercode;
	}

	@Column(name = "INVOICENUMBER", length = 1020)
	public String getInvoicenumber() {
		return this.invoicenumber;
	}

	public void setInvoicenumber(String invoicenumber) {
		this.invoicenumber = invoicenumber;
	}

	@Column(name = "AMOUNT", precision = 25, scale = 4)
	public BigDecimal getAmount() {
		return this.amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	@Column(name = "CHARGES", precision = 25, scale = 4)
	public BigDecimal getCharges() {
		return this.charges;
	}

	public void setCharges(BigDecimal charges) {
		this.charges = charges;
	}

	@Column(name = "BILLPAYSTATUS", precision = 10, scale = 0)
	public Long getBillpaystatus() {
		return this.billpaystatus;
	}

	public void setBillpaystatus(Long billpaystatus) {
		this.billpaystatus = billpaystatus;
	}

	@Column(name = "RESPONSECODE", precision = 10, scale = 0)
	public Long getResponsecode() {
		return this.responsecode;
	}

	public void setResponsecode(Long responsecode) {
		this.responsecode = responsecode;
	}

	@Column(name = "NOOFRETRIES", precision = 10, scale = 0)
	public Long getNoofretries() {
		return this.noofretries;
	}

	public void setNoofretries(Long noofretries) {
		this.noofretries = noofretries;
	}

	@Column(name = "INTXNID", length = 400)
	public String getIntxnid() {
		return this.intxnid;
	}

	public void setIntxnid(String intxnid) {
		this.intxnid = intxnid;
	}

	@Column(name = "INRESPONSECODE", length = 1020)
	public String getInresponsecode() {
		return this.inresponsecode;
	}

	public void setInresponsecode(String inresponsecode) {
		this.inresponsecode = inresponsecode;
	}

	@Column(name = "PARTNERBILLERCODE")
	public String getPartnerbillercode() {
		return this.partnerbillercode;
	}

	public void setPartnerbillercode(String partnerbillercode) {
		this.partnerbillercode = partnerbillercode;
	}

	@Column(name = "INTEGRATIONCODE")
	public String getIntegrationcode() {
		return this.integrationcode;
	}

	public void setIntegrationcode(String integrationcode) {
		this.integrationcode = integrationcode;
	}

	@Column(name = "SOURCEMDN", length = 80)
	public String getSourcemdn() {
		return this.sourcemdn;
	}

	public void setSourcemdn(String sourcemdn) {
		this.sourcemdn = sourcemdn;
	}

	@Column(name = "ORIGINALINTXNID", length = 400)
	public String getOriginalintxnid() {
		return this.originalintxnid;
	}

	public void setOriginalintxnid(String originalintxnid) {
		this.originalintxnid = originalintxnid;
	}

	@Column(name = "INFO1", length = 1020)
	public String getInfo1() {
		return this.info1;
	}

	public void setInfo1(String info1) {
		this.info1 = info1;
	}

	@Column(name = "INFO2", length = 1020)
	public String getInfo2() {
		return this.info2;
	}

	public void setInfo2(String info2) {
		this.info2 = info2;
	}

	@Column(name = "INFO3", length = 1020)
	public String getInfo3() {
		return this.info3;
	}

	public void setInfo3(String info3) {
		this.info3 = info3;
	}

	@Column(name = "CHARGESINCLUDED", precision = 3, scale = 0)
	public Short getChargesincluded() {
		return this.chargesincluded;
	}

	public void setChargesincluded(Short chargesincluded) {
		this.chargesincluded = chargesincluded;
	}

	@Column(name = "NOMINALAMOUNT", precision = 25, scale = 4)
	public BigDecimal getNominalamount() {
		return this.nominalamount;
	}

	public void setNominalamount(BigDecimal nominalamount) {
		this.nominalamount = nominalamount;
	}

	@Column(name = "INFO4", length = 1020)
	public String getInfo4() {
		return this.info4;
	}

	public void setInfo4(String info4) {
		this.info4 = info4;
	}

	@Column(name = "INFO5", length = 1020)
	public String getInfo5() {
		return this.info5;
	}

	public void setInfo5(String info5) {
		this.info5 = info5;
	}

	@Column(name = "OPERATORMESSAGE", length = 1020)
	public String getOperatormessage() {
		return this.operatormessage;
	}

	public void setOperatormessage(String operatormessage) {
		this.operatormessage = operatormessage;
	}

	@Column(name = "OPERATORCHARGES", precision = 25, scale = 4)
	public BigDecimal getOperatorcharges() {
		return this.operatorcharges;
	}

	public void setOperatorcharges(BigDecimal operatorcharges) {
		this.operatorcharges = operatorcharges;
	}

	@Column(name = "BILLDATA")
	public Clob getBilldata() {
		return this.billdata;
	}

	public void setBilldata(Clob billdata) {
		this.billdata = billdata;
	}

	@Type(type = "userDefinedTimeStamp")
	@Column(name = "TRANSFERTIME")
	public Timestamp getTransfertime() {
		return this.transfertime;
	}

	public void setTransfertime(Timestamp transfertime) {
		this.transfertime = transfertime;
	}

	@Column(name = "INFO6")
	public String getInfo6() {
		return this.info6;
	}

	public void setInfo6(String info6) {
		this.info6 = info6;
	}

	@Column(name = "INFO7")
	public String getInfo7() {
		return this.info7;
	}

	public void setInfo7(String info7) {
		this.info7 = info7;
	}

	@Column(name = "INFO8")
	public String getInfo8() {
		return this.info8;
	}

	public void setInfo8(String info8) {
		this.info8 = info8;
	}

}
