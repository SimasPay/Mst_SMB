package com.mfino.domain;

// Generated Sep 27, 2016 5:23:21 PM by Hibernate Tools 3.4.0.CR1

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.mfino.hibernate.Timestamp;

/**
 * Groups generated by hbm2java
 */
@Entity
@Table(name = "GROUPS")
public class Groups extends Base implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	public static final String FieldName_GroupName = "groupname";
	public static final String FieldName_SystemGroup = "systemgroup";
	
	private Long id;
	
	private String groupname;
	private String description;
	private Boolean systemgroup;
	private Set<ActorChannelMapping> actorChannelMappings = new HashSet<ActorChannelMapping>(
			0);
	private Set<PtcGroupMapping> ptcGroupMappings = new HashSet<PtcGroupMapping>(
			0);

	public Groups() {
	}

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "id_Sequence")
	@SequenceGenerator(name = "id_Sequence", sequenceName = "groups_ID_SEQ")
	@Column(name = "ID", unique = true, nullable = false, scale = 0)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	

	@Column(name = "GROUPNAME", nullable = false, length = 1020)
	public String getGroupname() {
		return this.groupname;
	}

	public void setGroupname(String groupname) {
		this.groupname = groupname;
	}

	@Column(name = "DESCRIPTION", length = 1020)
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "SYSTEMGROUP", precision = 3, scale = 0)
	public Boolean getSystemgroup() {
		return this.systemgroup;
	}

	public void setSystemgroup(Boolean systemgroup) {
		this.systemgroup = systemgroup;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "groups")
	public Set<ActorChannelMapping> getActorChannelMappings() {
		return this.actorChannelMappings;
	}

	public void setActorChannelMappings(
			Set<ActorChannelMapping> actorChannelMappings) {
		this.actorChannelMappings = actorChannelMappings;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "groups")
	public Set<PtcGroupMapping> getPtcGroupMappings() {
		return this.ptcGroupMappings;
	}

	public void setPtcGroupMappings(Set<PtcGroupMapping> ptcGroupMappings) {
		this.ptcGroupMappings = ptcGroupMappings;
	}

}
