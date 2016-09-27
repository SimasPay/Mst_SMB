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
 * ChannelSessionMgmt generated by hbm2java
 */
@Entity
@Table(name = "CHANNEL_SESSION_MGMT")
public class ChannelSessionMgmt implements java.io.Serializable {

	private BigDecimal id;
	private long version;
	private SubscriberMdn subscriberMdn;
	private Serializable lastupdatetime;
	private String updatedby;
	private Serializable createtime;
	private String createdby;
	private String sessionkey;
	private Long requestcountafterlogin;
	private Serializable lastrequesttime;
	private Serializable lastlogintime;

	public ChannelSessionMgmt() {
	}

	public ChannelSessionMgmt(BigDecimal id, SubscriberMdn subscriberMdn,
			Serializable lastupdatetime, String updatedby,
			Serializable createtime, String createdby) {
		this.id = id;
		this.subscriberMdn = subscriberMdn;
		this.lastupdatetime = lastupdatetime;
		this.updatedby = updatedby;
		this.createtime = createtime;
		this.createdby = createdby;
	}

	public ChannelSessionMgmt(BigDecimal id, SubscriberMdn subscriberMdn,
			Serializable lastupdatetime, String updatedby,
			Serializable createtime, String createdby, String sessionkey,
			Long requestcountafterlogin, Serializable lastrequesttime,
			Serializable lastlogintime) {
		this.id = id;
		this.subscriberMdn = subscriberMdn;
		this.lastupdatetime = lastupdatetime;
		this.updatedby = updatedby;
		this.createtime = createtime;
		this.createdby = createdby;
		this.sessionkey = sessionkey;
		this.requestcountafterlogin = requestcountafterlogin;
		this.lastrequesttime = lastrequesttime;
		this.lastlogintime = lastlogintime;
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
	@JoinColumn(name = "MDNID", nullable = false)
	public SubscriberMdn getSubscriberMdn() {
		return this.subscriberMdn;
	}

	public void setSubscriberMdn(SubscriberMdn subscriberMdn) {
		this.subscriberMdn = subscriberMdn;
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

	@Column(name = "SESSIONKEY", length = 1020)
	public String getSessionkey() {
		return this.sessionkey;
	}

	public void setSessionkey(String sessionkey) {
		this.sessionkey = sessionkey;
	}

	@Column(name = "REQUESTCOUNTAFTERLOGIN", precision = 10, scale = 0)
	public Long getRequestcountafterlogin() {
		return this.requestcountafterlogin;
	}

	public void setRequestcountafterlogin(Long requestcountafterlogin) {
		this.requestcountafterlogin = requestcountafterlogin;
	}

	@Column(name = "LASTREQUESTTIME")
	public Serializable getLastrequesttime() {
		return this.lastrequesttime;
	}

	public void setLastrequesttime(Serializable lastrequesttime) {
		this.lastrequesttime = lastrequesttime;
	}

	@Column(name = "LASTLOGINTIME")
	public Serializable getLastlogintime() {
		return this.lastlogintime;
	}

	public void setLastlogintime(Serializable lastlogintime) {
		this.lastlogintime = lastlogintime;
	}

}
