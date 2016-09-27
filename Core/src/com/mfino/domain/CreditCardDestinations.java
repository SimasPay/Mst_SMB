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
import com.mfino.hibernate.Timestamp;

/**
 * CreditcardDestinations generated by hbm2java
 */
@Entity
@Table(name = "CREDITCARD_DESTINATIONS")
public class CreditCardDestinations extends Base implements java.io.Serializable {

	
	private Subscriber subscriber;
	private String destmdn;
	private String olddestmdn;
	private Long ccmdnstatus;

	public CreditCardDestinations() {
	}

	public CreditCardDestinations(BigDecimal id, Subscriber subscriber,
			Timestamp lastupdatetime, String updatedby,
			Timestamp createtime, String createdby) {
		this.id = id;
		this.subscriber = subscriber;
		this.lastupdatetime = lastupdatetime;
		this.updatedby = updatedby;
		this.createtime = createtime;
		this.createdby = createdby;
	}

	public CreditCardDestinations(BigDecimal id, Subscriber subscriber,
			Timestamp lastupdatetime, String updatedby,
			Timestamp createtime, String createdby, String destmdn,
			String olddestmdn, Long ccmdnstatus) {
		this.id = id;
		this.subscriber = subscriber;
		this.lastupdatetime = lastupdatetime;
		this.updatedby = updatedby;
		this.createtime = createtime;
		this.createdby = createdby;
		this.destmdn = destmdn;
		this.olddestmdn = olddestmdn;
		this.ccmdnstatus = ccmdnstatus;
	}

	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SUBSCRIBERID", nullable = false)
	public Subscriber getSubscriber() {
		return this.subscriber;
	}

	public void setSubscriber(Subscriber subscriber) {
		this.subscriber = subscriber;
	}

	
	@Column(name = "DESTMDN", length = 1020)
	public String getDestmdn() {
		return this.destmdn;
	}

	public void setDestmdn(String destmdn) {
		this.destmdn = destmdn;
	}

	@Column(name = "OLDDESTMDN", length = 1020)
	public String getOlddestmdn() {
		return this.olddestmdn;
	}

	public void setOlddestmdn(String olddestmdn) {
		this.olddestmdn = olddestmdn;
	}

	@Column(name = "CCMDNSTATUS", precision = 10, scale = 0)
	public Long getCcmdnstatus() {
		return this.ccmdnstatus;
	}

	public void setCcmdnstatus(Long ccmdnstatus) {
		this.ccmdnstatus = ccmdnstatus;
	}

}
