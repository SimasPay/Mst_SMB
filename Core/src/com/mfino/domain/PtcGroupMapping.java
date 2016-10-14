package com.mfino.domain;

// Generated Sep 27, 2016 5:23:21 PM by Hibernate Tools 3.4.0.CR1

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * PtcGroupMapping generated by hbm2java
 */
@Entity
@Table(name = "PTC_GROUP_MAPPING", uniqueConstraints = @UniqueConstraint(columnNames = {
		"GROUPID", "PTCID" }))
public class PtcGroupMapping extends Base implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	public static final String FieldName_Group = "groups";
	public static final String FieldName_PocketTemplateConfigByPtcID = "pocketTemplateConfig";
	
	private PocketTemplateConfig pocketTemplateConfig;
	private Groups groups;
	

	public PtcGroupMapping() {
	}

	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PTCID")
	public PocketTemplateConfig getPocketTemplateConfig() {
		return this.pocketTemplateConfig;
	}

	public void setPocketTemplateConfig(
			PocketTemplateConfig pocketTemplateConfig) {
		this.pocketTemplateConfig = pocketTemplateConfig;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "GROUPID")
	public Groups getGroups() {
		return this.groups;
	}

	public void setGroups(Groups groups) {
		this.groups = groups;
	}

	

}
