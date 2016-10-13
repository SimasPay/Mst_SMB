package com.mfino.domain;

// Generated Sep 27, 2016 5:23:21 PM by Hibernate Tools 3.4.0.CR1

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.mfino.hibernate.Timestamp;

/**
 * MoneyClearanceGraved generated by hbm2java
 */
@Entity
@Table(name = "MONEY_CLEARANCE_GRAVED", uniqueConstraints = @UniqueConstraint(columnNames = {
		"MDNID", "POCKETID" }))
public class MoneyClearanceGraved extends Base implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	public static final String FieldName_SubscriberMDNByMDNID = "subscriberMdnByMdnid";
	public static final String FieldName_Pocket = "pocketByPocketid";
	public static final String FieldName_SctlId = "serviceChargeTxnLogBySctlid";
	public static final String FieldName_SubscriberMDNByRefundMDNID = "subscriberMdnByRefundmdnid";
	public static final String FieldName_RefundPocketID = "pocketByRefundpocketid";
	public static final String FieldName_RefundSctlID = "serviceChargeTxnLogByRefundsctlid";
	
	private Pocket pocketByPocketid;
	private ServiceChargeTxnLog serviceChargeTxnLogByRefundsctlid;
	private SubscriberMdn subscriberMdnByRefundmdnid;
	private SubscriberMdn subscriberMdnByMdnid;
	private Pocket pocketByRefundpocketid;
	private ServiceChargeTxnLog serviceChargeTxnLogBySctlid;
	private BigDecimal amount;
	private String refundaccountnumber;
	private long mcstatus;

	public MoneyClearanceGraved() {
	}

	

	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "POCKETID", nullable = false)
	public Pocket getPocketByPocketid() {
		return this.pocketByPocketid;
	}

	public void setPocketByPocketid(Pocket pocketByPocketid) {
		this.pocketByPocketid = pocketByPocketid;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "REFUNDSCTLID")
	public ServiceChargeTxnLog getServiceChargeTxnLogByRefundsctlid() {
		return this.serviceChargeTxnLogByRefundsctlid;
	}

	public void setServiceChargeTxnLogByRefundsctlid(
			ServiceChargeTxnLog serviceChargeTxnLogByRefundsctlid) {
		this.serviceChargeTxnLogByRefundsctlid = serviceChargeTxnLogByRefundsctlid;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "REFUNDMDNID")
	public SubscriberMdn getSubscriberMdnByRefundmdnid() {
		return this.subscriberMdnByRefundmdnid;
	}

	public void setSubscriberMdnByRefundmdnid(
			SubscriberMdn subscriberMdnByRefundmdnid) {
		this.subscriberMdnByRefundmdnid = subscriberMdnByRefundmdnid;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MDNID", nullable = false)
	public SubscriberMdn getSubscriberMdnByMdnid() {
		return this.subscriberMdnByMdnid;
	}

	public void setSubscriberMdnByMdnid(SubscriberMdn subscriberMdnByMdnid) {
		this.subscriberMdnByMdnid = subscriberMdnByMdnid;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "REFUNDPOCKETID")
	public Pocket getPocketByRefundpocketid() {
		return this.pocketByRefundpocketid;
	}

	public void setPocketByRefundpocketid(Pocket pocketByRefundpocketid) {
		this.pocketByRefundpocketid = pocketByRefundpocketid;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SCTLID", nullable = false)
	public ServiceChargeTxnLog getServiceChargeTxnLogBySctlid() {
		return this.serviceChargeTxnLogBySctlid;
	}

	public void setServiceChargeTxnLogBySctlid(
			ServiceChargeTxnLog serviceChargeTxnLogBySctlid) {
		this.serviceChargeTxnLogBySctlid = serviceChargeTxnLogBySctlid;
	}

	@Column(name = "AMOUNT", nullable = false, precision = 25, scale = 4)
	public BigDecimal getAmount() {
		return this.amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	@Column(name = "REFUNDACCOUNTNUMBER", length = 16)
	public String getRefundaccountnumber() {
		return this.refundaccountnumber;
	}

	public void setRefundaccountnumber(String refundaccountnumber) {
		this.refundaccountnumber = refundaccountnumber;
	}

	@Column(name = "MCSTATUS", nullable = false, precision = 11, scale = 0)
	public long getMcstatus() {
		return this.mcstatus;
	}

	public void setMcstatus(long mcstatus) {
		this.mcstatus = mcstatus;
	}

}
