package com.mfino.domain;

// Generated Sep 27, 2016 5:23:21 PM by Hibernate Tools 3.4.0.CR1

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import com.mfino.hibernate.Timestamp;

/**
 * SapGroupid generated by hbm2java
 */
@Entity
@Table(name = "SAP_GROUPID")
public class SAPGroupID extends Base implements java.io.Serializable {

	
	private String groupid;
	private String groupidname;

	public SAPGroupID() {
	}

	public SAPGroupID(BigDecimal id, Timestamp lastupdatetime,
			String updatedby, Timestamp createtime, String createdby,
			String groupid, String groupidname) {
		this.id = id;
		this.lastupdatetime = lastupdatetime;
		this.updatedby = updatedby;
		this.createtime = createtime;
		this.createdby = createdby;
		this.groupid = groupid;
		this.groupidname = groupidname;
	}

	
	@Column(name = "GROUPID", nullable = false, length = 1020)
	public String getGroupid() {
		return this.groupid;
	}

	public void setGroupid(String groupid) {
		this.groupid = groupid;
	}

	@Column(name = "GROUPIDNAME", nullable = false, length = 1020)
	public String getGroupidname() {
		return this.groupidname;
	}

	public void setGroupidname(String groupidname) {
		this.groupidname = groupidname;
	}

}
