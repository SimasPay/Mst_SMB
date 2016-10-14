package com.mfino.domain;

// Generated Sep 27, 2016 5:23:21 PM by Hibernate Tools 3.4.0.CR1

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * AgentCommissionFee generated by hbm2java
 */
@Entity
@Table(name = "AGENT_COMMISSION_FEE", uniqueConstraints = @UniqueConstraint(columnNames = {
		"PARTNERID", "MONTH", "YEAR" }))
public class AgentCommissionFee extends Base implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	public static final String FieldName_PartnerID = "partnerid";
	public static final String FieldName_Month = "month";
	public static final String FieldName_Year = "year";
	
	private BigDecimal partnerid;
	private String month;
	private long year;
	private BigDecimal customerbalancefee;
	private BigDecimal openaccountfee;

	public AgentCommissionFee() {
	}


	@Column(name = "PARTNERID", nullable = false, scale = 0)
	public BigDecimal getPartnerid() {
		return this.partnerid;
	}

	public void setPartnerid(BigDecimal partnerid) {
		this.partnerid = partnerid;
	}

	@Column(name = "MONTH", nullable = false, length = 20)
	public String getMonth() {
		return this.month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	@Column(name = "YEAR", nullable = false, precision = 10, scale = 0)
	public long getYear() {
		return this.year;
	}

	public void setYear(long year) {
		this.year = year;
	}

	@Column(name = "CUSTOMERBALANCEFEE", nullable = false, precision = 25, scale = 4)
	public BigDecimal getCustomerbalancefee() {
		return this.customerbalancefee;
	}

	public void setCustomerbalancefee(BigDecimal customerbalancefee) {
		this.customerbalancefee = customerbalancefee;
	}

	@Column(name = "OPENACCOUNTFEE", nullable = false, precision = 25, scale = 4)
	public BigDecimal getOpenaccountfee() {
		return this.openaccountfee;
	}

	public void setOpenaccountfee(BigDecimal openaccountfee) {
		this.openaccountfee = openaccountfee;
	}

}
