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
 * Village generated by hbm2java
 */
@Entity
@Table(name = "VILLAGE")
public class Village implements java.io.Serializable {

	private BigDecimal id;
	private long version;
	private District district;
	private Serializable lastupdatetime;
	private String updatedby;
	private Serializable createtime;
	private String createdby;
	private String villageid;
	private String displaytext;

	public Village() {
	}

	public Village(BigDecimal id, District district,
			Serializable lastupdatetime, String updatedby,
			Serializable createtime, String createdby, String villageid) {
		this.id = id;
		this.district = district;
		this.lastupdatetime = lastupdatetime;
		this.updatedby = updatedby;
		this.createtime = createtime;
		this.createdby = createdby;
		this.villageid = villageid;
	}

	public Village(BigDecimal id, District district,
			Serializable lastupdatetime, String updatedby,
			Serializable createtime, String createdby, String villageid,
			String displaytext) {
		this.id = id;
		this.district = district;
		this.lastupdatetime = lastupdatetime;
		this.updatedby = updatedby;
		this.createtime = createtime;
		this.createdby = createdby;
		this.villageid = villageid;
		this.displaytext = displaytext;
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
	@JoinColumn(name = "IDDISTRICT", nullable = false)
	public District getDistrict() {
		return this.district;
	}

	public void setDistrict(District district) {
		this.district = district;
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

	@Column(name = "VILLAGEID", nullable = false, length = 80)
	public String getVillageid() {
		return this.villageid;
	}

	public void setVillageid(String villageid) {
		this.villageid = villageid;
	}

	@Column(name = "DISPLAYTEXT", length = 800)
	public String getDisplaytext() {
		return this.displaytext;
	}

	public void setDisplaytext(String displaytext) {
		this.displaytext = displaytext;
	}

}
