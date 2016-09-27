package com.mfino.domain;

// Generated Sep 27, 2016 5:23:21 PM by Hibernate Tools 3.4.0.CR1

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * ExpirationType generated by hbm2java
 */
@Entity
@Table(name = "EXPIRATION_TYPE")
public class ExpirationType implements java.io.Serializable {

	private BigDecimal id;
	private long version;
	private Serializable lastupdatetime;
	private String updatedby;
	private Serializable createtime;
	private String createdby;
	private BigDecimal mspid;
	private Long expirytype;
	private Long expirymode;
	private BigDecimal expiryvalue;
	private String expirydescription;
	private Set<FundDefinition> fundDefinitions = new HashSet<FundDefinition>(0);

	public ExpirationType() {
	}

	public ExpirationType(BigDecimal id, Serializable lastupdatetime,
			String updatedby, Serializable createtime, String createdby,
			BigDecimal mspid) {
		this.id = id;
		this.lastupdatetime = lastupdatetime;
		this.updatedby = updatedby;
		this.createtime = createtime;
		this.createdby = createdby;
		this.mspid = mspid;
	}

	public ExpirationType(BigDecimal id, Serializable lastupdatetime,
			String updatedby, Serializable createtime, String createdby,
			BigDecimal mspid, Long expirytype, Long expirymode,
			BigDecimal expiryvalue, String expirydescription,
			Set<FundDefinition> fundDefinitions) {
		this.id = id;
		this.lastupdatetime = lastupdatetime;
		this.updatedby = updatedby;
		this.createtime = createtime;
		this.createdby = createdby;
		this.mspid = mspid;
		this.expirytype = expirytype;
		this.expirymode = expirymode;
		this.expiryvalue = expiryvalue;
		this.expirydescription = expirydescription;
		this.fundDefinitions = fundDefinitions;
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

	@Column(name = "MSPID", nullable = false, scale = 0)
	public BigDecimal getMspid() {
		return this.mspid;
	}

	public void setMspid(BigDecimal mspid) {
		this.mspid = mspid;
	}

	@Column(name = "EXPIRYTYPE", precision = 10, scale = 0)
	public Long getExpirytype() {
		return this.expirytype;
	}

	public void setExpirytype(Long expirytype) {
		this.expirytype = expirytype;
	}

	@Column(name = "EXPIRYMODE", precision = 10, scale = 0)
	public Long getExpirymode() {
		return this.expirymode;
	}

	public void setExpirymode(Long expirymode) {
		this.expirymode = expirymode;
	}

	@Column(name = "EXPIRYVALUE", scale = 0)
	public BigDecimal getExpiryvalue() {
		return this.expiryvalue;
	}

	public void setExpiryvalue(BigDecimal expiryvalue) {
		this.expiryvalue = expiryvalue;
	}

	@Column(name = "EXPIRYDESCRIPTION")
	public String getExpirydescription() {
		return this.expirydescription;
	}

	public void setExpirydescription(String expirydescription) {
		this.expirydescription = expirydescription;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "expirationType")
	public Set<FundDefinition> getFundDefinitions() {
		return this.fundDefinitions;
	}

	public void setFundDefinitions(Set<FundDefinition> fundDefinitions) {
		this.fundDefinitions = fundDefinitions;
	}

}
