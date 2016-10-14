package com.mfino.domain;

// Generated Sep 27, 2016 5:23:21 PM by Hibernate Tools 3.4.0.CR1

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * CreditcardDestinations generated by hbm2java
 */
@Entity
@Table(name = "CREDITCARD_DESTINATIONS")
public class CreditcardDestinations extends Base implements java.io.Serializable {

	
	public static final String FieldName_Subscriber = "subscriber";
	public static final String FieldName_CCMDNStatus = "ccmdnstatus";
	private Subscriber subscriber;
	private String destmdn;
	private String olddestmdn;
	private Long ccmdnstatus;

	public CreditcardDestinations() {
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
