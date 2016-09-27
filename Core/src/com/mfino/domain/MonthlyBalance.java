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
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import com.mfino.hibernate.Timestamp;

/**
 * MonthlyBalance generated by hbm2java
 */
@Entity
@Table(name = "MONTHLY_BALANCE", uniqueConstraints = @UniqueConstraint(columnNames = {
		"POCKETID", "MONTH", "YEAR" }))
public class MonthlyBalance extends Base implements java.io.Serializable {

	
	private Pocket pocket;
	private String month;
	private long year;
	private BigDecimal averagemonthlybalance;
	private BigDecimal interestcalculated;
	private BigDecimal agentcommissioncalculated;

	public MonthlyBalance() {
	}

	public MonthlyBalance(BigDecimal id, Pocket pocket,
			Timestamp lastupdatetime, String updatedby,
			Timestamp createtime, String createdby, String month, long year,
			BigDecimal averagemonthlybalance, BigDecimal interestcalculated,
			BigDecimal agentcommissioncalculated) {
		this.id = id;
		this.pocket = pocket;
		this.lastupdatetime = lastupdatetime;
		this.updatedby = updatedby;
		this.createtime = createtime;
		this.createdby = createdby;
		this.month = month;
		this.year = year;
		this.averagemonthlybalance = averagemonthlybalance;
		this.interestcalculated = interestcalculated;
		this.agentcommissioncalculated = agentcommissioncalculated;
	}

	

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "POCKETID", nullable = false)
	public Pocket getPocket() {
		return this.pocket;
	}

	public void setPocket(Pocket pocket) {
		this.pocket = pocket;
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

	@Column(name = "AVERAGEMONTHLYBALANCE", nullable = false, precision = 25, scale = 4)
	public BigDecimal getAveragemonthlybalance() {
		return this.averagemonthlybalance;
	}

	public void setAveragemonthlybalance(BigDecimal averagemonthlybalance) {
		this.averagemonthlybalance = averagemonthlybalance;
	}

	@Column(name = "INTERESTCALCULATED", nullable = false, precision = 25, scale = 4)
	public BigDecimal getInterestcalculated() {
		return this.interestcalculated;
	}

	public void setInterestcalculated(BigDecimal interestcalculated) {
		this.interestcalculated = interestcalculated;
	}

	@Column(name = "AGENTCOMMISSIONCALCULATED", nullable = false, precision = 25, scale = 4)
	public BigDecimal getAgentcommissioncalculated() {
		return this.agentcommissioncalculated;
	}

	public void setAgentcommissioncalculated(
			BigDecimal agentcommissioncalculated) {
		this.agentcommissioncalculated = agentcommissioncalculated;
	}

}
