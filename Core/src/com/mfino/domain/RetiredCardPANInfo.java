package com.mfino.domain;

// Generated Sep 27, 2016 5:23:21 PM by Hibernate Tools 3.4.0.CR1

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.mfino.hibernate.Timestamp;

/**
 * RetiredCardpanInfo generated by hbm2java
 */
@Entity
@Table(name = "RETIRED_CARDPAN_INFO", uniqueConstraints = @UniqueConstraint(columnNames = "CARDPAN"))
public class RetiredCardPANInfo extends Base implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	public static final String FieldName_RetireCount = "retirecount";
	public static final String FieldName_CardPAN = "cardpan";
	private String cardpan;
	private long retirecount;

	public RetiredCardPANInfo() {
	}

	public RetiredCardPANInfo(BigDecimal id, Timestamp lastupdatetime,
			String updatedby, Timestamp createtime, String createdby,
			String cardpan, long retirecount) {
		this.id = id;
		this.lastupdatetime = lastupdatetime;
		this.updatedby = updatedby;
		this.createtime = createtime;
		this.createdby = createdby;
		this.cardpan = cardpan;
		this.retirecount = retirecount;
	}

	

	@Column(name = "CARDPAN", unique = true, nullable = false, length = 1020)
	public String getCardpan() {
		return this.cardpan;
	}

	public void setCardpan(String cardpan) {
		this.cardpan = cardpan;
	}

	@Column(name = "RETIRECOUNT", nullable = false, precision = 10, scale = 0)
	public long getRetirecount() {
		return this.retirecount;
	}

	public void setRetirecount(long retirecount) {
		this.retirecount = retirecount;
	}

}
