package com.mfino.domain;

// Generated Sep 27, 2016 5:23:21 PM by Hibernate Tools 3.4.0.CR1

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * CurrentBalanceInfo generated by hbm2java
 */
@Entity
@Table(name = "CURRENT_BALANCE_INFO")
public class CurrentBalanceInfo implements java.io.Serializable {

	private BigDecimal id;
	private long version;
	private Serializable lastupdatetime;
	private String updatedby;
	private Serializable createtime;
	private String createdby;
	private String currentbalance;
	private BigDecimal subscriberid;
	private BigDecimal kyclevel;

	public CurrentBalanceInfo() {
	}

	public CurrentBalanceInfo(BigDecimal id, Serializable lastupdatetime,
			String updatedby, Serializable createtime, String createdby) {
		this.id = id;
		this.lastupdatetime = lastupdatetime;
		this.updatedby = updatedby;
		this.createtime = createtime;
		this.createdby = createdby;
	}

	public CurrentBalanceInfo(BigDecimal id, Serializable lastupdatetime,
			String updatedby, Serializable createtime, String createdby,
			String currentbalance, BigDecimal subscriberid, BigDecimal kyclevel) {
		this.id = id;
		this.lastupdatetime = lastupdatetime;
		this.updatedby = updatedby;
		this.createtime = createtime;
		this.createdby = createdby;
		this.currentbalance = currentbalance;
		this.subscriberid = subscriberid;
		this.kyclevel = kyclevel;
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

	@Column(name = "LASTUPDATETIME", nullable = false)
	public Serializable getLastupdatetime() {
		return this.lastupdatetime;
	}

	public void setLastupdatetime(Serializable lastupdatetime) {
		this.lastupdatetime = lastupdatetime;
	}

	@Column(name = "UPDATEDBY", nullable = false)
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

	@Column(name = "CREATEDBY", nullable = false)
	public String getCreatedby() {
		return this.createdby;
	}

	public void setCreatedby(String createdby) {
		this.createdby = createdby;
	}

	@Column(name = "CURRENTBALANCE")
	public String getCurrentbalance() {
		return this.currentbalance;
	}

	public void setCurrentbalance(String currentbalance) {
		this.currentbalance = currentbalance;
	}

	@Column(name = "SUBSCRIBERID", precision = 20, scale = 0)
	public BigDecimal getSubscriberid() {
		return this.subscriberid;
	}

	public void setSubscriberid(BigDecimal subscriberid) {
		this.subscriberid = subscriberid;
	}

	@Column(name = "KYCLEVEL", precision = 20, scale = 0)
	public BigDecimal getKyclevel() {
		return this.kyclevel;
	}

	public void setKyclevel(BigDecimal kyclevel) {
		this.kyclevel = kyclevel;
	}

}
