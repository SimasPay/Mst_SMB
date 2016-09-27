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

/**
 * SmsPartner generated by hbm2java
 */
@Entity
@Table(name = "SMS_PARTNER")
public class SmsPartner implements java.io.Serializable {

	private BigDecimal id;
	private long version;
	private MfinoUser mfinoUser;
	private Serializable lastupdatetime;
	private String updatedby;
	private Serializable createtime;
	private String createdby;
	private String partnername;
	private String contactname;
	private String contactphone;
	private String contactemail;
	private String serverip;
	private String apikey;
	private Short sendreport;
	private Set<SmscConfiguration> smscConfigurations = new HashSet<SmscConfiguration>(
			0);
	private Set<SmsTransactionLog> smsTransactionLogs = new HashSet<SmsTransactionLog>(
			0);

	public SmsPartner() {
	}

	public SmsPartner(BigDecimal id, MfinoUser mfinoUser,
			Serializable lastupdatetime, String updatedby,
			Serializable createtime, String createdby, String partnername,
			String contactname, String contactphone, String contactemail,
			String serverip, String apikey) {
		this.id = id;
		this.mfinoUser = mfinoUser;
		this.lastupdatetime = lastupdatetime;
		this.updatedby = updatedby;
		this.createtime = createtime;
		this.createdby = createdby;
		this.partnername = partnername;
		this.contactname = contactname;
		this.contactphone = contactphone;
		this.contactemail = contactemail;
		this.serverip = serverip;
		this.apikey = apikey;
	}

	public SmsPartner(BigDecimal id, MfinoUser mfinoUser,
			Serializable lastupdatetime, String updatedby,
			Serializable createtime, String createdby, String partnername,
			String contactname, String contactphone, String contactemail,
			String serverip, String apikey, Short sendreport,
			Set<SmscConfiguration> smscConfigurations,
			Set<SmsTransactionLog> smsTransactionLogs) {
		this.id = id;
		this.mfinoUser = mfinoUser;
		this.lastupdatetime = lastupdatetime;
		this.updatedby = updatedby;
		this.createtime = createtime;
		this.createdby = createdby;
		this.partnername = partnername;
		this.contactname = contactname;
		this.contactphone = contactphone;
		this.contactemail = contactemail;
		this.serverip = serverip;
		this.apikey = apikey;
		this.sendreport = sendreport;
		this.smscConfigurations = smscConfigurations;
		this.smsTransactionLogs = smsTransactionLogs;
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
	@JoinColumn(name = "USERID", nullable = false)
	public MfinoUser getMfinoUser() {
		return this.mfinoUser;
	}

	public void setMfinoUser(MfinoUser mfinoUser) {
		this.mfinoUser = mfinoUser;
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

	@Column(name = "PARTNERNAME", nullable = false, length = 1020)
	public String getPartnername() {
		return this.partnername;
	}

	public void setPartnername(String partnername) {
		this.partnername = partnername;
	}

	@Column(name = "CONTACTNAME", nullable = false, length = 1020)
	public String getContactname() {
		return this.contactname;
	}

	public void setContactname(String contactname) {
		this.contactname = contactname;
	}

	@Column(name = "CONTACTPHONE", nullable = false, length = 1020)
	public String getContactphone() {
		return this.contactphone;
	}

	public void setContactphone(String contactphone) {
		this.contactphone = contactphone;
	}

	@Column(name = "CONTACTEMAIL", nullable = false, length = 1020)
	public String getContactemail() {
		return this.contactemail;
	}

	public void setContactemail(String contactemail) {
		this.contactemail = contactemail;
	}

	@Column(name = "SERVERIP", nullable = false, length = 1020)
	public String getServerip() {
		return this.serverip;
	}

	public void setServerip(String serverip) {
		this.serverip = serverip;
	}

	@Column(name = "APIKEY", nullable = false, length = 1020)
	public String getApikey() {
		return this.apikey;
	}

	public void setApikey(String apikey) {
		this.apikey = apikey;
	}

	@Column(name = "SENDREPORT", precision = 3, scale = 0)
	public Short getSendreport() {
		return this.sendreport;
	}

	public void setSendreport(Short sendreport) {
		this.sendreport = sendreport;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "smsPartner")
	public Set<SmscConfiguration> getSmscConfigurations() {
		return this.smscConfigurations;
	}

	public void setSmscConfigurations(Set<SmscConfiguration> smscConfigurations) {
		this.smscConfigurations = smscConfigurations;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "smsPartner")
	public Set<SmsTransactionLog> getSmsTransactionLogs() {
		return this.smsTransactionLogs;
	}

	public void setSmsTransactionLogs(Set<SmsTransactionLog> smsTransactionLogs) {
		this.smsTransactionLogs = smsTransactionLogs;
	}

}
