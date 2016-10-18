package com.mfino.domain;

// Generated Sep 27, 2016 5:23:21 PM by Hibernate Tools 3.4.0.CR1

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
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
	private Long id;
	

	public PtcGroupMapping() {
	}
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "id_Sequence")
	@SequenceGenerator(name = "id_Sequence", sequenceName = "ptc_group_mapping_ID_SEQ")
	@Column(name = "ID", unique = true, nullable = false, scale = 0)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
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
