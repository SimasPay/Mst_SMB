package com.mfino.domain;

// Generated Sep 27, 2016 5:23:21 PM by Hibernate Tools 3.4.0.CR1

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.mfino.hibernate.Timestamp;

/**
 * SubscriberAddiInfo generated by hbm2java
 */
@Entity
@Table(name = "SUBSCRIBER_ADDI_INFO")
public class SubscriberAddiInfo extends Base implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	public static final String FieldName_SubscriberID = "subscriber";
	public static final String FieldName_KinName = "kinname";
	public static final String FieldName_KinMDN = "kinmdn";
	private Subscriber subscriber;
	private String proofofaddress;
	private String reference1;
	private String reference2;
	private String creditcheck;
	private String subscompanyname;
	private String certofincorporation;
	private String misc1;
	private String misc2;
	private String nationality;
	private String kinname;
	private String kinmdn;
	private Long controllreference;
	private String subscribermobilecompany;
	private String work;
	private BigDecimal income;
	private String goalofacctopening;
	private String sourceoffund;
	private Long electonicdeviceused;
	private String agreementnumber;
	private String agentcompanyname;
	private String latitude;
	private String longitude;
	private String userbankbranch;
	private Long bankacountstatus;
	private Timestamp agrementdate;
	private Timestamp implementatindate;
	private String otherwork;

	public SubscriberAddiInfo() {
	}

	

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SUBSCRIBERID", nullable = false)
	public Subscriber getSubscriber() {
		return this.subscriber;
	}

	public void setSubscriber(Subscriber subscriber) {
		this.subscriber = subscriber;
	}

	

	@Column(name = "PROOFOFADDRESS", length = 1020)
	public String getProofofaddress() {
		return this.proofofaddress;
	}

	public void setProofofaddress(String proofofaddress) {
		this.proofofaddress = proofofaddress;
	}

	@Column(name = "REFERENCE1", length = 1020)
	public String getReference1() {
		return this.reference1;
	}

	public void setReference1(String reference1) {
		this.reference1 = reference1;
	}

	@Column(name = "REFERENCE2", length = 1020)
	public String getReference2() {
		return this.reference2;
	}

	public void setReference2(String reference2) {
		this.reference2 = reference2;
	}

	@Column(name = "CREDITCHECK", length = 1020)
	public String getCreditcheck() {
		return this.creditcheck;
	}

	public void setCreditcheck(String creditcheck) {
		this.creditcheck = creditcheck;
	}

	@Column(name = "SUBSCOMPANYNAME", length = 1020)
	public String getSubscompanyname() {
		return this.subscompanyname;
	}

	public void setSubscompanyname(String subscompanyname) {
		this.subscompanyname = subscompanyname;
	}

	@Column(name = "CERTOFINCORPORATION", length = 1020)
	public String getCertofincorporation() {
		return this.certofincorporation;
	}

	public void setCertofincorporation(String certofincorporation) {
		this.certofincorporation = certofincorporation;
	}

	@Column(name = "MISC1", length = 1020)
	public String getMisc1() {
		return this.misc1;
	}

	public void setMisc1(String misc1) {
		this.misc1 = misc1;
	}

	@Column(name = "MISC2", length = 1020)
	public String getMisc2() {
		return this.misc2;
	}

	public void setMisc2(String misc2) {
		this.misc2 = misc2;
	}

	@Column(name = "NATIONALITY", length = 1020)
	public String getNationality() {
		return this.nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	@Column(name = "KINNAME", length = 1020)
	public String getKinname() {
		return this.kinname;
	}

	public void setKinname(String kinname) {
		this.kinname = kinname;
	}

	@Column(name = "KINMDN", length = 1020)
	public String getKinmdn() {
		return this.kinmdn;
	}

	public void setKinmdn(String kinmdn) {
		this.kinmdn = kinmdn;
	}

	@Column(name = "CONTROLLREFERENCE", precision = 10, scale = 0)
	public Long getControllreference() {
		return this.controllreference;
	}

	public void setControllreference(Long controllreference) {
		this.controllreference = controllreference;
	}

	@Column(name = "SUBSCRIBERMOBILECOMPANY", length = 1020)
	public String getSubscribermobilecompany() {
		return this.subscribermobilecompany;
	}

	public void setSubscribermobilecompany(String subscribermobilecompany) {
		this.subscribermobilecompany = subscribermobilecompany;
	}

	@Column(name = "WORK", length = 100)
	public String getWork() {
		return this.work;
	}

	public void setWork(String work) {
		this.work = work;
	}

	@Column(name = "INCOME", scale = 0)
	public BigDecimal getIncome() {
		return this.income;
	}

	public void setIncome(BigDecimal income) {
		this.income = income;
	}

	@Column(name = "GOALOFACCTOPENING", length = 100)
	public String getGoalofacctopening() {
		return this.goalofacctopening;
	}

	public void setGoalofacctopening(String goalofacctopening) {
		this.goalofacctopening = goalofacctopening;
	}

	@Column(name = "SOURCEOFFUND", length = 100)
	public String getSourceoffund() {
		return this.sourceoffund;
	}

	public void setSourceoffund(String sourceoffund) {
		this.sourceoffund = sourceoffund;
	}

	@Column(name = "ELECTONICDEVICEUSED", precision = 10, scale = 0)
	public Long getElectonicdeviceused() {
		return this.electonicdeviceused;
	}

	public void setElectonicdeviceused(Long electonicdeviceused) {
		this.electonicdeviceused = electonicdeviceused;
	}

	@Column(name = "AGREEMENTNUMBER")
	public String getAgreementnumber() {
		return this.agreementnumber;
	}

	public void setAgreementnumber(String agreementnumber) {
		this.agreementnumber = agreementnumber;
	}

	@Column(name = "AGENTCOMPANYNAME")
	public String getAgentcompanyname() {
		return this.agentcompanyname;
	}

	public void setAgentcompanyname(String agentcompanyname) {
		this.agentcompanyname = agentcompanyname;
	}

	@Column(name = "LATITUDE")
	public String getLatitude() {
		return this.latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	@Column(name = "LONGITUDE")
	public String getLongitude() {
		return this.longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	@Column(name = "USERBANKBRANCH")
	public String getUserbankbranch() {
		return this.userbankbranch;
	}

	public void setUserbankbranch(String userbankbranch) {
		this.userbankbranch = userbankbranch;
	}

	@Column(name = "BANKACOUNTSTATUS", precision = 10, scale = 0)
	public Long getBankacountstatus() {
		return this.bankacountstatus;
	}

	public void setBankacountstatus(Long bankacountstatus) {
		this.bankacountstatus = bankacountstatus;
	}

	@Column(name = "AGREMENTDATE")
	public Timestamp getAgrementdate() {
		return this.agrementdate;
	}

	public void setAgrementdate(Timestamp agrementdate) {
		this.agrementdate = agrementdate;
	}

	@Column(name = "IMPLEMENTATINDATE")
	public Timestamp getImplementatindate() {
		return this.implementatindate;
	}

	public void setImplementatindate(Timestamp implementatindate) {
		this.implementatindate = implementatindate;
	}

	@Column(name = "OTHERWORK")
	public String getOtherwork() {
		return this.otherwork;
	}

	public void setOtherwork(String otherwork) {
		this.otherwork = otherwork;
	}

}
