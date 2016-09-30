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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import com.mfino.hibernate.Timestamp;

/**
 * ProvinceRegion generated by hbm2java
 */
@Entity
@Table(name = "PROVINCE_REGION")
public class ProvinceRegion extends Base  implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	public static final String FieldName_IdProvince = "province";

	public static final String FieldName_RegionId = "regionid";

	private Province province;
	private String regionid;
	private String displaytext;
	private String biregionid;
	private Set<District> districts = new HashSet<District>(0);

	public ProvinceRegion() {
	}

	public ProvinceRegion(BigDecimal id, Province province,
			Timestamp lastupdatetime, String updatedby,
			Timestamp createtime, String createdby, String regionid,
			String biregionid) {
		this.id = id;
		this.province = province;
		this.lastupdatetime = lastupdatetime;
		this.updatedby = updatedby;
		this.createtime = createtime;
		this.createdby = createdby;
		this.regionid = regionid;
		this.biregionid = biregionid;
	}

	public ProvinceRegion(BigDecimal id, Province province,
			Timestamp lastupdatetime, String updatedby,
			Timestamp createtime, String createdby, String regionid,
			String displaytext, String biregionid, Set<District> districts) {
		this.id = id;
		this.province = province;
		this.lastupdatetime = lastupdatetime;
		this.updatedby = updatedby;
		this.createtime = createtime;
		this.createdby = createdby;
		this.regionid = regionid;
		this.displaytext = displaytext;
		this.biregionid = biregionid;
		this.districts = districts;
	}

	

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "IDPROVINCE", nullable = false)
	public Province getProvince() {
		return this.province;
	}

	public void setProvince(Province province) {
		this.province = province;
	}

	

	@Column(name = "REGIONID", nullable = false, length = 80)
	public String getRegionid() {
		return this.regionid;
	}

	public void setRegionid(String regionid) {
		this.regionid = regionid;
	}

	@Column(name = "DISPLAYTEXT", length = 800)
	public String getDisplaytext() {
		return this.displaytext;
	}

	public void setDisplaytext(String displaytext) {
		this.displaytext = displaytext;
	}

	@Column(name = "BIREGIONID", nullable = false, length = 80)
	public String getBiregionid() {
		return this.biregionid;
	}

	public void setBiregionid(String biregionid) {
		this.biregionid = biregionid;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "provinceRegion")
	public Set<District> getDistricts() {
		return this.districts;
	}

	public void setDistricts(Set<District> districts) {
		this.districts = districts;
	}

}
