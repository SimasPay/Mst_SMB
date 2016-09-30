package com.mfino.domain;

// Generated Sep 27, 2016 5:23:21 PM by Hibernate Tools 3.4.0.CR1

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.mfino.hibernate.Timestamp;

/**
 * ActorChannelMapping generated by hbm2java
 */
@Entity
@Table(name = "ACTOR_CHANNEL_MAPPING", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "BUSINESSPARTNERTYPE", "SERVICEID",
				"TRANSACTIONTYPEID", "CHANNELCODEID", "GROUPID" }),
		@UniqueConstraint(columnNames = { "SUBSCRIBERTYPE", "KYCLEVEL",
				"SERVICEID", "TRANSACTIONTYPEID", "CHANNELCODEID", "GROUPID" }) })
public class ActorChannelMapping extends Base implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	public static final String FieldName_SubscriberType = "subscribertype";
	public static final String FieldName_BusinessPartnerType = "businesspartnertype";
	public static final String FieldName_Service = "service";

	public static final String FieldName_TransactionType = "transactionType";

	public static final String FieldName_ChannelCode = "channelCode";

	public static final String FieldName_KYCLevelByKYCLevel = "kycLevel";

	public static final String FieldName_Group = "groups";
	
	private ChannelCode channelCode;
	private Service service;
	private KYCLevel kycLevel;
	private Groups groups;
	private TransactionType transactionType;
	private short isallowed;
	private long subscribertype;
	private Long businesspartnertype;

	public ActorChannelMapping() {
	}

	public ActorChannelMapping(BigDecimal id, ChannelCode channelCode,
			Service service, TransactionType transactionType,
			Timestamp lastupdatetime, String updatedby,
			Timestamp createtime, String createdby, short isallowed,
			long subscribertype) {
		this.id = id;
		this.channelCode = channelCode;
		this.service = service;
		this.transactionType = transactionType;
		this.lastupdatetime = lastupdatetime;
		this.updatedby = updatedby;
		this.createtime = createtime;
		this.createdby = createdby;
		this.isallowed = isallowed;
		this.subscribertype = subscribertype;
	}

	public ActorChannelMapping(BigDecimal id, ChannelCode channelCode,
			Service service, KYCLevel kycLevel, Groups groups,
			TransactionType transactionType, Timestamp lastupdatetime,
			String updatedby, Timestamp createtime, String createdby,
			short isallowed, long subscribertype, Long businesspartnertype) {
		this.id = id;
		this.channelCode = channelCode;
		this.service = service;
		this.kycLevel = kycLevel;
		this.groups = groups;
		this.transactionType = transactionType;
		this.lastupdatetime = lastupdatetime;
		this.updatedby = updatedby;
		this.createtime = createtime;
		this.createdby = createdby;
		this.isallowed = isallowed;
		this.subscribertype = subscribertype;
		this.businesspartnertype = businesspartnertype;
	}

	

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CHANNELCODEID", nullable = false)
	public ChannelCode getChannelCode() {
		return this.channelCode;
	}

	public void setChannelCode(ChannelCode channelCode) {
		this.channelCode = channelCode;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SERVICEID", nullable = false)
	public Service getService() {
		return this.service;
	}

	public void setService(Service service) {
		this.service = service;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "KYCLEVEL")
	public KYCLevel getKycLevel() {
		return this.kycLevel;
	}

	public void setKycLevel(KYCLevel kycLevel) {
		this.kycLevel = kycLevel;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "GROUPID")
	public Groups getGroups() {
		return this.groups;
	}

	public void setGroups(Groups groups) {
		this.groups = groups;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TRANSACTIONTYPEID", nullable = false)
	public TransactionType getTransactionType() {
		return this.transactionType;
	}

	public void setTransactionType(TransactionType transactionType) {
		this.transactionType = transactionType;
	}

	
	@Column(name = "ISALLOWED", nullable = false, precision = 3, scale = 0)
	public short getIsallowed() {
		return this.isallowed;
	}

	public void setIsallowed(short isallowed) {
		this.isallowed = isallowed;
	}

	@Column(name = "SUBSCRIBERTYPE", nullable = false, precision = 10, scale = 0)
	public long getSubscribertype() {
		return this.subscribertype;
	}

	public void setSubscribertype(long subscribertype) {
		this.subscribertype = subscribertype;
	}

	@Column(name = "BUSINESSPARTNERTYPE", precision = 10, scale = 0)
	public Long getBusinesspartnertype() {
		return this.businesspartnertype;
	}

	public void setBusinesspartnertype(Long businesspartnertype) {
		this.businesspartnertype = businesspartnertype;
	}

}
