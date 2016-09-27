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
import javax.persistence.Version;

/**
 * MfsbillerPartnerMap generated by hbm2java
 */
@Entity
@Table(name = "MFSBILLER_PARTNER_MAP")
public class MfsbillerPartnerMap implements java.io.Serializable {

	private BigDecimal id;
	private long version;
	private Partner partner;
	private MfinoServiceProvider mfinoServiceProvider;
	private MfsBiller mfsBiller;
	private Serializable lastupdatetime;
	private String updatedby;
	private Serializable createtime;
	private String createdby;
	private String partnerbillercode;
	private Long billerpartnertype;
	private String integrationcode;
	private Short chargesincluded;

	public MfsbillerPartnerMap() {
	}

	public MfsbillerPartnerMap(BigDecimal id, Partner partner,
			MfinoServiceProvider mfinoServiceProvider, MfsBiller mfsBiller,
			Serializable lastupdatetime, String updatedby,
			Serializable createtime, String createdby) {
		this.id = id;
		this.partner = partner;
		this.mfinoServiceProvider = mfinoServiceProvider;
		this.mfsBiller = mfsBiller;
		this.lastupdatetime = lastupdatetime;
		this.updatedby = updatedby;
		this.createtime = createtime;
		this.createdby = createdby;
	}

	public MfsbillerPartnerMap(BigDecimal id, Partner partner,
			MfinoServiceProvider mfinoServiceProvider, MfsBiller mfsBiller,
			Serializable lastupdatetime, String updatedby,
			Serializable createtime, String createdby,
			String partnerbillercode, Long billerpartnertype,
			String integrationcode, Short chargesincluded) {
		this.id = id;
		this.partner = partner;
		this.mfinoServiceProvider = mfinoServiceProvider;
		this.mfsBiller = mfsBiller;
		this.lastupdatetime = lastupdatetime;
		this.updatedby = updatedby;
		this.createtime = createtime;
		this.createdby = createdby;
		this.partnerbillercode = partnerbillercode;
		this.billerpartnertype = billerpartnertype;
		this.integrationcode = integrationcode;
		this.chargesincluded = chargesincluded;
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
	@JoinColumn(name = "PARTNERID", nullable = false)
	public Partner getPartner() {
		return this.partner;
	}

	public void setPartner(Partner partner) {
		this.partner = partner;
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
	@JoinColumn(name = "MFSBILLERID", nullable = false)
	public MfsBiller getMfsBiller() {
		return this.mfsBiller;
	}

	public void setMfsBiller(MfsBiller mfsBiller) {
		this.mfsBiller = mfsBiller;
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

	@Column(name = "PARTNERBILLERCODE", length = 1020)
	public String getPartnerbillercode() {
		return this.partnerbillercode;
	}

	public void setPartnerbillercode(String partnerbillercode) {
		this.partnerbillercode = partnerbillercode;
	}

	@Column(name = "BILLERPARTNERTYPE", precision = 10, scale = 0)
	public Long getBillerpartnertype() {
		return this.billerpartnertype;
	}

	public void setBillerpartnertype(Long billerpartnertype) {
		this.billerpartnertype = billerpartnertype;
	}

	@Column(name = "INTEGRATIONCODE", length = 1020)
	public String getIntegrationcode() {
		return this.integrationcode;
	}

	public void setIntegrationcode(String integrationcode) {
		this.integrationcode = integrationcode;
	}

	@Column(name = "CHARGESINCLUDED", precision = 3, scale = 0)
	public Short getChargesincluded() {
		return this.chargesincluded;
	}

	public void setChargesincluded(Short chargesincluded) {
		this.chargesincluded = chargesincluded;
	}

}
