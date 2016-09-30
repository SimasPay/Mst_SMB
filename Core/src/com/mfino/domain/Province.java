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

import com.mfino.hibernate.Timestamp;
/**
 * Province generated by hbm2java
 */
@Entity
@Table(name = "PROVINCE")
public class Province extends Base implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	public static final String FieldName_ProvinceId = "provinceid";
	private String displaytext;
	private String provinceid;
	private Set<ProvinceRegion> provinceRegions = new HashSet<ProvinceRegion>(0);

	public Province() {
	}

	public Province(BigDecimal id, Timestamp lastupdatetime,
			String updatedby, Timestamp createtime, String createdby,
			String provinceid) {
		this.id = id;
		this.lastupdatetime = lastupdatetime;
		this.updatedby = updatedby;
		this.createtime = createtime;
		this.createdby = createdby;
		this.provinceid = provinceid;
	}

	public Province(BigDecimal id, Timestamp lastupdatetime,
			String updatedby, Timestamp createtime, String createdby,
			String displaytext, String provinceid,
			Set<ProvinceRegion> provinceRegions) {
		this.id = id;
		this.lastupdatetime = lastupdatetime;
		this.updatedby = updatedby;
		this.createtime = createtime;
		this.createdby = createdby;
		this.displaytext = displaytext;
		this.provinceid = provinceid;
		this.provinceRegions = provinceRegions;
	}

	

	
	@Column(name = "DISPLAYTEXT", length = 800)
	public String getDisplaytext() {
		return this.displaytext;
	}

	public void setDisplaytext(String displaytext) {
		this.displaytext = displaytext;
	}

	@Column(name = "PROVINCEID", nullable = false, length = 80)
	public String getProvinceid() {
		return this.provinceid;
	}

	public void setProvinceid(String provinceid) {
		this.provinceid = provinceid;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "province")
	public Set<ProvinceRegion> getProvinceRegions() {
		return this.provinceRegions;
	}

	public void setProvinceRegions(Set<ProvinceRegion> provinceRegions) {
		this.provinceRegions = provinceRegions;
	}

}
