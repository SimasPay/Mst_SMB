package com.mfino.domain;

// Generated Sep 27, 2016 5:23:21 PM by Hibernate Tools 3.4.0.CR1

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * SubscriberGroups generated by hbm2java
 */
@Entity
@Table(name = "SUBSCRIBER_GROUPS")
public class SubscriberGroups implements java.io.Serializable {

	private long id;
	private long version;
	private Serializable lastupdatetime;
	private String updatedby;
	private Serializable createtime;
	private String createdby;
	private Long subscriberid;
	private Long groupid;

	public SubscriberGroups() {
	}

	public SubscriberGroups(long id, Serializable lastupdatetime,
			String updatedby, Serializable createtime, String createdby) {
		this.id = id;
		this.lastupdatetime = lastupdatetime;
		this.updatedby = updatedby;
		this.createtime = createtime;
		this.createdby = createdby;
	}

	public SubscriberGroups(long id, Serializable lastupdatetime,
			String updatedby, Serializable createtime, String createdby,
			Long subscriberid, Long groupid) {
		this.id = id;
		this.lastupdatetime = lastupdatetime;
		this.updatedby = updatedby;
		this.createtime = createtime;
		this.createdby = createdby;
		this.subscriberid = subscriberid;
		this.groupid = groupid;
	}

	@Id
	@Column(name = "ID", unique = true, nullable = false, precision = 10, scale = 0)
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
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

	@Column(name = "SUBSCRIBERID", precision = 10, scale = 0)
	public Long getSubscriberid() {
		return this.subscriberid;
	}

	public void setSubscriberid(Long subscriberid) {
		this.subscriberid = subscriberid;
	}

	@Column(name = "GROUPID", precision = 10, scale = 0)
	public Long getGroupid() {
		return this.groupid;
	}

	public void setGroupid(Long groupid) {
		this.groupid = groupid;
	}

}
