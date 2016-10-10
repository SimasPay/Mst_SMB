package com.mfino.domain;

// Generated Sep 27, 2016 5:23:21 PM by Hibernate Tools 3.4.0.CR1

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.mfino.hibernate.Timestamp;

/**
 * ChannelCode generated by hbm2java
 */
@Entity
@Table(name = "CHANNEL_CODE", uniqueConstraints = @UniqueConstraint(columnNames = "CHANNELCODE"))
public class ChannelCode extends Base implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	public static final String FieldName_ChannelCode = "channelcode";
	public static final String FieldName_ChannelName = "channelname";
	public static final String FieldName_ChannelSourceApplication = "channelsourceapplication";
	
	private String channelcode;
	private String channelname;
	private String description;
	private long channelsourceapplication;
	private Set<MfaTransactionsInfo> mfaTransactionsInfos = new HashSet<MfaTransactionsInfo>(
			0);
	private Set<TransactionRule> transactionRules = new HashSet<TransactionRule>(
			0);
	private Set<ActorChannelMapping> actorChannelMappings = new HashSet<ActorChannelMapping>(
			0);

	public ChannelCode() {
	}

	public ChannelCode(BigDecimal id, Timestamp lastupdatetime,
			String updatedby, Timestamp createtime, String createdby,
			String channelcode, String channelname,
			long channelsourceapplication) {
		this.id = id;
		this.lastupdatetime = lastupdatetime;
		this.updatedby = updatedby;
		this.createtime = createtime;
		this.createdby = createdby;
		this.channelcode = channelcode;
		this.channelname = channelname;
		this.channelsourceapplication = channelsourceapplication;
	}

	public ChannelCode(BigDecimal id, Timestamp lastupdatetime,
			String updatedby, Timestamp createtime, String createdby,
			String channelcode, String channelname, String description,
			long channelsourceapplication,
			Set<MfaTransactionsInfo> mfaTransactionsInfos,
			Set<TransactionRule> transactionRules,
			Set<ActorChannelMapping> actorChannelMappings) {
		this.id = id;
		this.lastupdatetime = lastupdatetime;
		this.updatedby = updatedby;
		this.createtime = createtime;
		this.createdby = createdby;
		this.channelcode = channelcode;
		this.channelname = channelname;
		this.description = description;
		this.channelsourceapplication = channelsourceapplication;
		this.mfaTransactionsInfos = mfaTransactionsInfos;
		this.transactionRules = transactionRules;
		this.actorChannelMappings = actorChannelMappings;
	}

	
	@Column(name = "CHANNELCODE", unique = true, nullable = false, length = 1020)
	public String getChannelcode() {
		return this.channelcode;
	}

	public void setChannelcode(String channelcode) {
		this.channelcode = channelcode;
	}

	@Column(name = "CHANNELNAME", nullable = false, length = 1020)
	public String getChannelname() {
		return this.channelname;
	}

	public void setChannelname(String channelname) {
		this.channelname = channelname;
	}

	@Column(name = "DESCRIPTION", length = 1020)
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "CHANNELSOURCEAPPLICATION", nullable = false, precision = 10, scale = 0)
	public long getChannelsourceapplication() {
		return this.channelsourceapplication;
	}

	public void setChannelsourceapplication(long channelsourceapplication) {
		this.channelsourceapplication = channelsourceapplication;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "channelCode")
	public Set<MfaTransactionsInfo> getMfaTransactionsInfos() {
		return this.mfaTransactionsInfos;
	}

	public void setMfaTransactionsInfos(
			Set<MfaTransactionsInfo> mfaTransactionsInfos) {
		this.mfaTransactionsInfos = mfaTransactionsInfos;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "channelCode")
	public Set<TransactionRule> getTransactionRules() {
		return this.transactionRules;
	}

	public void setTransactionRules(Set<TransactionRule> transactionRules) {
		this.transactionRules = transactionRules;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "channelCode")
	public Set<ActorChannelMapping> getActorChannelMappings() {
		return this.actorChannelMappings;
	}

	public void setActorChannelMappings(
			Set<ActorChannelMapping> actorChannelMappings) {
		this.actorChannelMappings = actorChannelMappings;
	}

}
